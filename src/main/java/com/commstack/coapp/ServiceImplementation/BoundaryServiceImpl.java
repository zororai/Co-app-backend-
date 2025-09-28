package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.Boundary;
import com.commstack.coapp.Models.BoundaryNotFoundException;
import com.commstack.coapp.Repositories.BoundaryRepository;
import com.commstack.coapp.Service.BoundaryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoundaryServiceImpl implements BoundaryService {

    private static final Logger logger = LoggerFactory.getLogger(BoundaryServiceImpl.class);

    private final BoundaryRepository boundaryRepository;

    @Override
    public Boundary create(Boundary boundary) {
        try {
            logger.info("Creating new boundary with name: {}", boundary.getName());

            // Set created time if not already set
            if (boundary.getCreatedAt() == null) {
                boundary.setCreatedAt(Instant.now());
            }

            Boundary savedBoundary = boundaryRepository.save(boundary);
            logger.info("Successfully created boundary with ID: {}", savedBoundary.getId());

            return savedBoundary;
        } catch (Exception e) {
            logger.error("Error creating boundary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create boundary: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Boundary> getAll() {
        try {
            logger.info("Retrieving all boundaries");
            List<Boundary> boundaries = boundaryRepository.findAll();
            logger.info("Found {} boundaries", boundaries.size());
            return boundaries;
        } catch (Exception e) {
            logger.error("Error retrieving boundaries: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve boundaries: " + e.getMessage(), e);
        }
    }

    @Override
    public Boundary getById(String id) {
        try {
            logger.info("Retrieving boundary with ID: {}", id);
            return boundaryRepository.findById(id)
                    .orElseThrow(() -> new BoundaryNotFoundException("Boundary not found with ID: " + id));
        } catch (BoundaryNotFoundException e) {
            logger.warn("Boundary not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving boundary with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve boundary: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Boundary> findContainingPoint(GeoJsonPoint point) {
        try {
            logger.info("Finding boundaries containing point: [{}, {}]",
                    point.getCoordinates().get(0), point.getCoordinates().get(1));

            List<Boundary> boundaries = boundaryRepository.findByGeometryIntersects(point);
            logger.info("Found {} boundaries containing the point", boundaries.size());

            return boundaries;
        } catch (Exception e) {
            logger.error("Error finding boundaries containing point: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to find boundaries containing point: " + e.getMessage(), e);
        }
    }
}