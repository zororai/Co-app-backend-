package com.commstack.coapp.Service;

import com.commstack.coapp.DTO.OreTransportDTO;
import com.commstack.coapp.Models.OreTransport;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface OreTransportService {

    ResponseEntity<OreTransport> applyTaxAndDeduct(String id, java.security.Principal principal);

    ResponseEntity<String> updateTransportFields(String id, String selectedTransportdriver, String transportStatus,
            String selectedTransport, String transportReason, String location, java.security.Principal principal);

    ResponseEntity<OreTransport> create(OreTransportDTO oreTransportDTO, java.security.Principal principal);

    List<OreTransport> getAllWithSelectedTransportdriverChanged();

    List<OreTransport> getAll();

    OreTransport getById(String id);

    ResponseEntity<String> update(String id, OreTransport oreTransport, java.security.Principal principal);

    ResponseEntity<String> delete(String id, java.security.Principal principal);
}
