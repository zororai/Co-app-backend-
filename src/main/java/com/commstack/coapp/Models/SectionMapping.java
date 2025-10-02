package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "section_mappings")
public class SectionMapping {
    @Id
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    /** e.g. "collection" */
    private String type;

    /** coordinates: list of polygons */
    private List<Polygon> coordinates;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Polygon {
        private String type; // e.g. "polygon"
        private List<Point> points;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Point {
        private double x;
        private double y;
    }
}
