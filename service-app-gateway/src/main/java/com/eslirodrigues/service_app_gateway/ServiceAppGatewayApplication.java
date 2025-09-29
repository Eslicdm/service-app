package com.eslirodrigues.service_app_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceAppGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAppGatewayApplication.class, args);
	}

}
