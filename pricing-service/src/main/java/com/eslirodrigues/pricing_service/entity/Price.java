package com.eslirodrigues.pricing_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "prices")
public class Price {

    @Id
    private String id;

    private PriceType priceType;

    private BigDecimal value;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Price() {
    }

    public Price(
            String id, PriceType priceType,
            BigDecimal value, String description,
            LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this.id = id;
        this.priceType = priceType;
        this.value = value;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return Objects.equals(id, price.id) &&
                priceType == price.priceType &&
                Objects.equals(value, price.value) &&
                Objects.equals(description, price.description) &&
                Objects.equals(createdAt, price.createdAt) &&
                Objects.equals(updatedAt, price.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, priceType, value, description, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Price{" +
                "id='" + id + '\'' +
                ", priceType=" + priceType +
                ", value=" + value +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}