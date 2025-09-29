package com.eslirodrigues.pricing_service;

import org.springframework.boot.SpringApplication;

public class TestPricingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(PricingServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
