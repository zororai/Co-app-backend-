package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.Boundary;
import com.commstack.coapp.Models.BoundaryNotFoundException;
import com.commstack.coapp.Service.BoundaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/boundaries")
public class BoundaryController {

    private static final Logger logger = LoggerFactory.getLogger(BoundaryController.class);

    private final BoundaryService boundaryService;

    @PostMapping("/")
    @Operation(summary = "Create a new boundary", description = "Creates a new boundary with name and geometry")
    public ResponseEntity<Boundary> createBoundary(@Valid @RequestBody Boundary boundary) {
        try {
            logger.info("Received request to create boundary: {}", boundary.getName());
            Boundary createdBoundary = boundaryService.create(boundary);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBoundary);
        } catch (Exception e) {
            logger.error("Error creating boundary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/")
    @Operation(summary = "Get all boundaries", description = "Retrieves a list of all boundaries")
    public ResponseEntity<List<Boundary>> getAllBoundaries() {
        try {
            logger.info("Received request to get all boundaries");
            List<Boundary> boundaries = boundaryService.getAll();
            return ResponseEntity.ok(boundaries);
        } catch (Exception e) {
            logger.error("Error retrieving boundaries: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get boundary by ID", description = "Retrieves a specific boundary by its ID")
    public ResponseEntity<Boundary> getBoundaryById(
            @Parameter(description = "Boundary ID") @PathVariable String id) {
        try {
            logger.info("Received request to get boundary with ID: {}", id);
            Boundary boundary = boundaryService.getById(id);
            return ResponseEntity.ok(boundary);
        } catch (BoundaryNotFoundException e) {
            logger.warn("Boundary not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error retrieving boundary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Find boundaries containing a point", description = "Finds all boundaries that contain the specified geographic point")
    public ResponseEntity<List<Boundary>> findBoundariesContainingPoint(
            @Parameter(description = "Latitude coordinate") @RequestParam double lat,
            @Parameter(description = "Longitude coordinate") @RequestParam double lng) {
        try {
            logger.info("Received request to find boundaries containing point: [{}, {}]", lng, lat);

            // Create GeoJsonPoint (longitude, latitude - note the order!)
            GeoJsonPoint point = new GeoJsonPoint(lng, lat);

            List<Boundary> boundaries = boundaryService.findContainingPoint(point);
            return ResponseEntity.ok(boundaries);
        } catch (Exception e) {
            logger.error("Error finding boundaries containing point: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}