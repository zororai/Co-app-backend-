package com.commstack.coapp.Service;

import com.commstack.coapp.Models.Penality;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface PenalityService {
    Penality create(Penality penality, Principal principal);
    List<Penality> getAll();
    Penality getById(String id);
    Penality update(String id, Penality penality, Principal principal);
    void delete(String id);
    List<Penality> findByShaftNumber(String shaftNumber);
    Penality markAsPaid(String id, Principal principal);
}
