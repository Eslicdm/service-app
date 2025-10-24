package com.eslirodrigues.member.client;

import com.eslirodrigues.member.dto.PriceUpdateEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PricingServiceClient {

    private final RestClient pricingServiceRestClient;

    public List<PriceUpdateEventDTO> getAllPrices() {
        return pricingServiceRestClient.get()
                .uri("/prices")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}