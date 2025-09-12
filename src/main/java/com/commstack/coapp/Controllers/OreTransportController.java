
package com.commstack.coapp.Controllers;

import com.commstack.coapp.DTO.OreTransportDTO;
import com.commstack.coapp.Models.OreTransport;
import com.commstack.coapp.Service.OreTransportService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/ore-transports")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "https://coappapi.commapp.online/", allowCredentials = "true")
@RequiredArgsConstructor
public class OreTransportController {

    @PutMapping("/{id}/update-sample-status-reason")
    public ResponseEntity<OreTransport> updateSampleStatusAndReason(
            @PathVariable("id") String oreTransportId,
            @RequestParam String newStatus,
            @RequestParam String newResult,
            @RequestParam String newReason) {
        return service.updateSampleStatusAndReason(oreTransportId, newStatus, newReason, Double.parseDouble(newResult));
    }

    @PutMapping("/{id}/collect-sample")
    public ResponseEntity<OreTransport> collectSample(
            @PathVariable("id") String oreTransportId,
            @RequestParam String sampleType,
            @RequestParam String sampleWeight,
            @RequestParam String status,
            @RequestParam String sampleSize) {
        return service.collectSample(oreTransportId, sampleType, sampleWeight, status, sampleSize);
    }

    @PutMapping("/{id}/fields")
    public ResponseEntity<String> updateTransportFields(
            @PathVariable String id,
            @RequestParam String selectedTransportdriver,
            @RequestParam String transportStatus,
            @RequestParam String selectedTransport,
            @RequestParam String transportReason,
            @RequestParam String location,
            Principal principal) {
        return service.updateTransportFields(id, selectedTransportdriver, transportStatus, selectedTransport,
                transportReason, location, principal);
    }

    private final OreTransportService service;

    @PutMapping("/{id}/mills/unknown")
    public ResponseEntity<OreTransport> setMillsToUnknown(@PathVariable String id, String millid, String millName,
            String millType, String location, Principal principal) {
        return service.setMillsToUnknown(id, millid, millName, millType, location, principal);
    }

    @PostMapping("/create")
    public ResponseEntity<OreTransport> create(@RequestBody OreTransportDTO oreTransportDTO, Principal principal) {
        return service.create(oreTransportDTO, principal);
    }

    @GetMapping("/allOre")
    public List<OreTransport> getAll() {
        return service.getAll();
    }

    @GetMapping("/with-selected-transportdriver-changed")
    public List<OreTransport> getAllWithSelectedTransportdriverChanged() {
        return service.getAllWithSelectedTransportdriverChanged();
    }

    @GetMapping("/{id}")
    public OreTransport getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable String id, @RequestBody OreTransport oreTransport,
            Principal principal) {
        return service.update(id, oreTransport, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }

    @PutMapping("/{id}/apply-tax")
    public ResponseEntity<OreTransport> applyTaxAndDeduct(@PathVariable String id, Principal principal) {
        return service.applyTaxAndDeduct(id, principal);
    }

    @GetMapping("/security-dispatcher/not-specified")
    public List<OreTransport> getAllWithSecurityDispatcherStatusNotSpecified() {
        return service.getAllWithSecurityDispatcherStatusNotSpecified();
    }

    @PutMapping("/{id}/security-dispatcher/dispatched")
    public ResponseEntity<String> updateSecurityDispatcherStatusToDispatched(@PathVariable String id,
            Principal principal) {
        return service.updateSecurityDispatcherStatusToDispatched(id, principal);
    }

    @GetMapping("/security-dispatcher/dispatched")
    public List<OreTransport> getAllWithSecurityDispatcherStatusDispatched() {
        return service.getAllWithSecurityDispatcherStatusDispatched();
    }

    @PutMapping("/{id}/security-dispatcher/received")
    public ResponseEntity<String> updateSecurityDispatcherStatusToReceived(@PathVariable String id,
            Principal principal) {
        return service.updateSecurityDispatcherStatusToReceived(id, principal);
    }

    @GetMapping("/security-dispatcher/received")
    public List<OreTransport> getAllWithSecurityDispatcherStatusReceived() {
        return service.getAllWithSecurityDispatcherStatusReceived();
    }
}
