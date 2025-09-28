package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "boundaries")
public class Boundary {

    @Id
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Geometry is required")
    private GeoJsonPolygon geometry;

    @Builder.Default
    private Instant createdAt = Instant.now();
}