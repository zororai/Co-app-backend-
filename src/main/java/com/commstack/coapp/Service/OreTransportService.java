package com.commstack.coapp.Service;

import com.commstack.coapp.DTO.OreTransportDTO;
import com.commstack.coapp.Models.OreTransport;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface OreTransportService {
        ResponseEntity<OreTransport> updateGoldSale(String oreTransportId, double weight, double price, String buyer);

        ResponseEntity<OreTransport> updateSampleIfDefault(String oreTransportId, String newReason,
                        double newResult, String newStatus);

        ResponseEntity<OreTransport> setMillsToUnknown(String id, String millid, String millName, String millType,
                        String location, Principal principal);

        ResponseEntity<OreTransport> applyTaxAndDeduct(String id, Principal principal);

        ResponseEntity<OreTransport> updateSampleStatusAndReason(String oreTransportId, String newStatus,
                        String newReason, double newResult);

        ResponseEntity<String> updateTransportFields(String id, String selectedTransportdriver, String transportStatus,
                        String selectedTransport, String transportReason, String location, Principal principal);

        ResponseEntity<OreTransport> create(OreTransportDTO oreTransportDTO, Principal principal);

        List<OreTransport> getAllWithSelectedTransportdriverChanged();

        List<OreTransport> getAll();

        OreTransport getById(String id);

        List<OreTransport> getAllWithSecurityDispatcherStatusNotSpecified();

        ResponseEntity<String> updateSecurityDispatcherStatusToDispatched(String id, java.security.Principal principal);

        List<OreTransport> getAllWithSecurityDispatcherStatusDispatched();

        ResponseEntity<String> updateSecurityDispatcherStatusToReceived(String id, java.security.Principal principal);

        List<OreTransport> getAllWithSecurityDispatcherStatusReceived();

        ResponseEntity<String> update(String id, OreTransport oreTransport, java.security.Principal principal);

        ResponseEntity<String> delete(String id, java.security.Principal principal);

        ResponseEntity<OreTransport> collectSample(String oreTransportId, String sampleType,
                        String sampleWeight, String status, String sampleSize);

}
