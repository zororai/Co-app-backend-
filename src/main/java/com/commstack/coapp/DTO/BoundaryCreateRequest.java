package com.commstack.coapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoundaryCreateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Geometry is required")
    private GeoJsonPolygon geometry;
}