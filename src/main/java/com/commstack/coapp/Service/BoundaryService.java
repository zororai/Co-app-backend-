package com.commstack.coapp.Service;

import com.commstack.coapp.Models.Boundary;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

public interface BoundaryService {

    Boundary create(Boundary boundary);

    List<Boundary> getAll();

    Boundary getById(String id);

    List<Boundary> findContainingPoint(GeoJsonPoint point);
}