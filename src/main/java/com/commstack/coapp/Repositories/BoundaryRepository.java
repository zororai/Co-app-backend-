package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.Boundary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoundaryRepository extends MongoRepository<Boundary, String> {

    @Query("{ 'geometry': { $geoIntersects: { $geometry: ?0 } } }")
    List<Boundary> findByGeometryIntersects(GeoJsonPoint point);
}