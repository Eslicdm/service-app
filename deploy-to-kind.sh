#!/bin/bash

set -e

echo "🚀 Deploying Service App to Kind Kubernetes..."

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check if kind cluster exists
if ! kind get clusters | grep -q "^service-app$"; then
    echo -e "${BLUE}Creating Kind cluster 'service-app'...${NC}"
    cat <<EOF | kind create cluster --name service-app --config=-
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
  - role: control-plane
    kubeadmConfigPatches:
    - |
      kind: InitConfiguration
      nodeRegistration:
        kubeletExtraArgs:
          node-labels: "ingress-ready=true"
    extraPortMappings:
    - containerPort: 30090
      hostPort: 8090
      protocol: TCP
    - containerPort: 30080
      hostPort: 8080
      protocol: TCP
EOF
else
    echo -e "${GREEN}Kind cluster 'service-app' already exists${NC}"
fi

# Set kubectl context
kubectl config use-context kind-service-app

# Install Local Path Provisioner for dynamic storage
echo -e "${BLUE}Installing Local Path Provisioner...${NC}"
kubectl apply -f https://raw.githubusercontent.com/rancher/local-path-provisioner/master/deploy/local-path-storage.yaml

# Build JARs
echo -e "${BLUE}Building application JARs...${NC}"
./mvnw clean package -DskipTests -f pom.xml

# Build and load required Docker images into Kind
echo -e "${BLUE}Building and loading Docker images into Kind...${NC}"
for service in member-request-service member-service pricing-service service-app-gateway; do
    IMAGE_TAG="1"
    echo -e "${BLUE}Building $service:$IMAGE_TAG...${NC}"
    docker build -t "$service:$IMAGE_TAG" -f "$service/Dockerfile" .
    echo -e "${BLUE}Loading $service:$IMAGE_TAG into kind...${NC}"
    kind load docker-image "$service:$IMAGE_TAG" --name service-app
done

wait_for_rollout() {
  local resource_type=$1
  local resource_name=$2
  local timeout=$3
  echo -e "${BLUE}Waiting for $resource_name ($resource_type) to be ready...${NC}"
  kubectl rollout status "$resource_type/$resource_name" -n service-app --timeout="$timeout"
  echo -e "${GREEN}$resource_name is ready.${NC}"
}

apply_and_wait() {
  local manifest_file=$1
  local resource_type=$2
  local resource_name=$3
  local timeout=$4

  echo -e "${BLUE}Applying manifest: $manifest_file...${NC}"
  kubectl apply -f "$manifest_file"
  wait_for_rollout "$resource_type" "$resource_name" "$timeout"
}

echo -e "${BLUE}Applying base manifests (Namespace, RBAC, Secrets)...${NC}"
kubectl apply -k service-app-infra/k8s/

echo -e "${BLUE}Waiting for infrastructure components to be ready...${NC}"
apply_and_wait "service-app-infra/k8s/10-keycloak-db.yaml" "statefulset" "keycloak-db" "5m"
apply_and_wait "service-app-infra/k8s/11-member-service-db.yaml" "statefulset" "member-service-db" "5m"
apply_and_wait "service-app-infra/k8s/12-pricing-service-db.yaml" "statefulset" "pricing-service-db" "5m"
apply_and_wait "service-app-infra/k8s/13-redis.yaml" "statefulset" "redis" "5m"
apply_and_wait "service-app-infra/k8s/15-kafka-zookeeper.yaml" "statefulset" "zookeeper" "5m"
wait_for_rollout "statefulset" "kafka" "5m"
apply_and_wait "service-app-infra/k8s/14-rabbitmq.yaml" "statefulset" "rabbitmq" "5m"
apply_and_wait "service-app-infra/k8s/40-otel-collector.yaml" "deployment" "otel-collector" "5m"

echo -e "${BLUE}Waiting for application pods to be ready...${NC}"

wait_for_pod() {
  local service_name=$1
  local service_label=$2
  local timeout=$3
  echo -e "${BLUE}Waiting for $service_name...${NC}"
  local pod_name
  pod_name=$(kubectl get pods -n service-app -l app="$service_label" -o jsonpath='{.items[0].metadata.name}')
  echo "Pod name: $pod_name"
  kubectl wait --for=condition=ready pod/"$pod_name" -n service-app --timeout="$timeout"
  echo -e "${GREEN}$service_name is ready.${NC}"
}

apply_and_wait_pod() {
  local manifest_file=$1
  local service_name=$2
  local service_label=$3
  local timeout=$4
  echo -e "${BLUE}Applying manifest: $manifest_file...${NC}"
  kubectl apply -f "$manifest_file"
  wait_for_pod "$service_name" "$service_label" "$timeout"
}

apply_and_wait_pod "service-app-infra/k8s/20-keycloak.yaml" "Keycloak" "keycloak" "5m"

echo -e "${BLUE}Applying application manifests...${NC}"
kubectl apply -f service-app-infra/k8s/30-member-service.yaml
kubectl apply -f service-app-infra/k8s/31-pricing-service.yaml
kubectl apply -f service-app-infra/k8s/32-member-request-service.yaml
kubectl apply -f service-app-infra/k8s/33-service-app-gateway.yaml

echo -e "${BLUE}Waiting for all application services to be ready in parallel...${NC}"
wait_for_pod "Member Service" "member-service" "4m" &
wait_for_pod "Pricing Service" "pricing-service" "4m" &
wait_for_pod "Member Request Service" "member-request-service" "4m" &
wait_for_pod "API Gateway" "service-app-gateway" "4m" &

# Wait for all background jobs to complete
wait

# Activate scripts
# chmod +x deploy-to-kind.sh cleanup.sh

# To resolve the iss with keycloak, angular and services
# Add-Content -Path C:\Windows\System32\drivers\etc\hosts -Value "`n127.0.0.1 keycloak"
echo -e "${GREEN}✅ All services are deployed and ready!${NC}"
echo ""
echo "Access your services:"
echo "  SpringDoc: http://localhost:8090/webjars/swagger-ui/index.html"
echo ""
echo "Check status:"
echo "  kubectl get pods -n service-app -w"
echo ""
echo "View logs:"
echo "  kubectl describe pod -n service-app rabbitmq-0"
echo "  kubectl logs -n service-app rabbitmq-0"
echo ""
echo "run-stop cluster:"
echo "  docker start service-app-control-plane"
echo "  docker stop service-app-control-plane"
