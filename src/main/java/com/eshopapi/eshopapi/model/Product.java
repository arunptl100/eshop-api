package com.eshopapi.eshopapi.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    private String name;
    private Double price;

    @Column(name = "added_at")
    private String addedAt; // Ideally, this should be of type LocalDate or LocalDateTime

    private String labels; // Stored as JSON text

    // Transient annotation to ignore this field for JPA
    // We need this since SQLite does not support array fields by default
    // We store the labels as a field of json array elements
    // e.g ["food", "condiment"]
    @Transient
    private List<String> labelsList; // Actual labels list, not stored in DB directly

    public List<String> getLabelsList() {
        if (labels != null) {
            try {
//              convert the list of strings to and from a JSON string format.
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(labels, List.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error reading labels from JSON", e);
            }
        }
        return null;
    }

    public void setLabelsList(List<String> labelsList) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.labels = mapper.writeValueAsString(labelsList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error writing labels to JSON", e);
        }
    }
}
