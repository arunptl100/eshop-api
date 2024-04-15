package com.eshopapi.eshopapi.model;

import com.eshopapi.eshopapi.exception.JsonConversionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.orm.jpa.JpaSystemException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "products", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @Column(length = 200) // db level validation on size of name
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    private Double price;

    @Column(name = "added_at", updatable = false)
    private String addedAt;

    // We need the converter since SQLite does not support array fields by default
    // We store the labels as a field of json array elements
    // e.g ["food", "drink"]
    @Convert(converter = JsonToListConverter.class)
    private List<String> labels;

    // Called before the entity manager persists the entity.
    // Sets the addedAt field to the current date when adding a product
    @PrePersist
    protected void onPrePersist() {
        this.addedAt = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    @Converter
    public static class JsonToListConverter implements AttributeConverter<List<String>, String> {
        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(List<String> attribute) {
            try {
                return mapper.writeValueAsString(attribute);
            } catch (JpaSystemException | JsonProcessingException e) {
                throw new JsonConversionException("Failed to convert list to JSON string.", e);
            }
        }

        @Override
        public List<String> convertToEntityAttribute(String dbData) {
            try {
                return dbData == null ?
                        Collections.emptyList() : mapper.readValue(dbData, new TypeReference<List<String>>() {});
            } catch (IOException e) {
                throw new JsonConversionException("Failed to convert JSON string to list.", e);
            }
        }
    }
}
