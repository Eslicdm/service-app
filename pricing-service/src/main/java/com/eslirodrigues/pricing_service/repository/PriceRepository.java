package com.eslirodrigues.pricing_service.repository;

import com.eslirodrigues.pricing_service.entity.Price;
import com.eslirodrigues.pricing_service.entity.PriceType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PriceRepository extends MongoRepository<Price, String> {
    Optional<Price> findByPriceType(PriceType priceType);
}