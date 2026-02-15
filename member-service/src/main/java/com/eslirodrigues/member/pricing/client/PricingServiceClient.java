package com.eslirodrigues.member.pricing.client;

import com.eslirodrigues.member.pricing.dto.PriceUpdateEventDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class PricingServiceClient {

    private final RestClient pricingServiceRestClient;

    public PricingServiceClient(RestClient pricingServiceRestClient) {
        this.pricingServiceRestClient = pricingServiceRestClient;
    }

    public List<PriceUpdateEventDTO> getAllPrices() {
        return pricingServiceRestClient.get()
                .uri("/prices")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}