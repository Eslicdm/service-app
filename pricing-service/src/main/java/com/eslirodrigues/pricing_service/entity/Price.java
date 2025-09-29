package com.eslirodrigues.pricing_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "prices")
public class Price {

    @Id
    private String id;

    private PriceType priceType;

    private BigDecimal value;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}