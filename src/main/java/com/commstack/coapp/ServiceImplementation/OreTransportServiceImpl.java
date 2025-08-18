package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.CompanyRegistration;
import com.commstack.coapp.Models.GoldSale;
import com.commstack.coapp.Models.Mill;
import com.commstack.coapp.Models.MinerAuditTrail;
import com.commstack.coapp.Models.OreTransport;
import com.commstack.coapp.Models.OreTransportAuditTrail;
import com.commstack.coapp.Models.Regminer;
import com.commstack.coapp.Repositories.OreTransportRepository;
import com.commstack.coapp.Service.OreTransportService;
import com.commstack.coapp.Models.UserAuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OreTransportServiceImpl implements OreTransportService {

    public ResponseEntity<OreTransport> applyTaxAndDeduct(String id, Principal principal) {
        Optional<OreTransport> existing = repository.findById(id);
        if (existing.isPresent()) {
            OreTransport transport = existing.get();
            // Assume getTax() returns a List<Tax> or a single Tax object
            double totalTaxRate = 0.0;
            if (transport.getTax() instanceof List) {
                for (Object t : (List<?>) transport.getTax()) {
                    if (t instanceof com.commstack.coapp.Models.Tax tax) {
                        totalTaxRate += tax.getTaxRate();
                    }
                }
            } else if (transport.getTax() instanceof com.commstack.coapp.Models.Tax tax) {
                totalTaxRate = tax.getTaxRate();
            }

            double originalWeight = transport.getWeight();
            int originalBags = transport.getNumberOfBags();
            double newWeight = originalWeight - (originalWeight * totalTaxRate / 100.0);
            int newnumberOfBags = (int) Math.round(originalBags - (originalBags * totalTaxRate / 100.0));
            transport.setNewWeight(newWeight);
            transport.setNewnumberOfBags(newnumberOfBags);
            transport.setUpdatedDate(LocalDateTime.now());
            transport.setDedicationReason(id + " - Tax applied and deducted");
            repository.save(transport);
            // Optionally, add audit trail for tax application
            if (principal != null) {
                UserAuditTrail audit = UserAuditTrail.builder()
                        .userId(id)
                        .action("APPLY_TAX_AND_DEDUCT")
                        .description("Applied tax and deducted from OreTransport id=" + id)
                        .doneBy(principal.getName())
                        .dateTime(LocalDateTime.now())
                        .build();
                mongoTemplate.save(audit, "ore_transport_audit_trail");
            }
            return ResponseEntity.ok(transport);
        }
        return ResponseEntity.status(404).body(null);
    }

    public List<OreTransport> getAllWithSelectedTransportdriverChanged() {
        return repository.findAll().stream()
                .filter(t -> t.getSelectedTransportdriver() != null
                        && !"Not Selected".equals(t.getSelectedTransportdriver()))
                .toList();
    }

    @Override
    public List<OreTransport> getAllWithSecurityDispatcherStatusNotSpecified() {
        return repository.findAll().stream()
                .filter(t -> "Not Specified".equals(t.getSecurityDispatcherStatus()))
                .toList();
    }

    @Override
    public ResponseEntity<String> updateSecurityDispatcherStatusToDispatched(String id, Principal principal) {
        Optional<OreTransport> existing = repository.findById(id);
        if (existing.isPresent()) {
            OreTransport transport = existing.get();
            if (!"Not Specified".equals(transport.getSecurityDispatcherStatus())) {
                return ResponseEntity.badRequest().body("Status is not 'Not Specified'");
            }
            transport.setSecurityDispatcherStatus("Dispatched");
            transport.setUpdatedBy(principal != null ? principal.getName() : "system");
            transport.setUpdatedDate(LocalDateTime.now());
            repository.save(transport);
            return ResponseEntity.ok("SecurityDispatcherStatus updated to Dispatched");
        }
        return ResponseEntity.status(404).body("OreTransport not found");
    }

    @Override
    public List<OreTransport> getAllWithSecurityDispatcherStatusDispatched() {
        return repository.findAll().stream()
                .filter(t -> "Dispatched".equals(t.getSecurityDispatcherStatus()))
                .toList();
    }

    @Override
    public ResponseEntity<String> updateSecurityDispatcherStatusToReceived(String id, Principal principal) {
        Optional<OreTransport> existing = repository.findById(id);
        if (existing.isPresent()) {
            OreTransport transport = existing.get();
            if (!"Dispatched".equals(transport.getSecurityDispatcherStatus())) {
                return ResponseEntity.badRequest().body("Status is not 'Dispatched'");
            }
            transport.setSecurityDispatcherStatus("Received");
            transport.setUpdatedBy(principal != null ? principal.getName() : "system");
            transport.setUpdatedDate(LocalDateTime.now());
            repository.save(transport);
            return ResponseEntity.ok("SecurityDispatcherStatus updated to Received");
        }
        return ResponseEntity.status(404).body("OreTransport not found");
    }

    @Override
    public List<OreTransport> getAllWithSecurityDispatcherStatusReceived() {
        return repository.findAll().stream()
                .filter(t -> "Received".equals(t.getSecurityDispatcherStatus()))
                .toList();
    }

    public ResponseEntity<String> updateTransportFields(String id, String selectedTransportdriver,
            String transportStatus, String selectedTransport, String transportReason, String location,
            Principal principal) {
        Optional<OreTransport> existing = repository.findById(id);
        if (existing.isPresent()) {
            OreTransport transport = existing.get();
            transport.setSelectedTransportdriver(selectedTransportdriver);
            transport.setTransportStatus(transportStatus);
            transport.setSelectedTransport(selectedTransport);
            transport.setTransportReason(transportReason);
            transport.setLocation(location);
            transport.setUpdatedBy(principal != null ? principal.getName() : "system");
            transport.setUpdatedDate(LocalDateTime.now());
            repository.save(transport);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED_FIELDS")
                    .description(
                            "Updated selectedTransportdriver, transportStatus, selectedTransport, transportReason for OreTransport id="
                                    + id)
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "ore_transport_audit_trail");
            return ResponseEntity.ok("OreTransport fields updated successfully");
        }
        return ResponseEntity.status(404).body("OreTransport not found");
    }

    @Autowired
    private OreTransportRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private String generateRegistrationNumber() {
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        int randomPart = (int) (Math.random() * 9000) + 1000;
        return "ore-" + datePart + "-" + randomPart;
    }

    @Override
    public ResponseEntity<OreTransport> create(com.commstack.coapp.DTO.OreTransportDTO oreTransportDTO,
            Principal principal) {
        OreTransport oreTransport = OreTransport.builder()
                .shaftNumbers(oreTransportDTO.getShaftNumbers())
                .weight(oreTransportDTO.getWeight())
                .numberOfBags(oreTransportDTO.getNumberOfBags())
                .transportStatus(oreTransportDTO.getTransportStatus())
                .tax(oreTransportDTO.getTax())
                .processStatus(oreTransportDTO.getProcessStatus())
                .location(oreTransportDTO.getLocation())
                .build();
        oreTransport.setId(generateRegistrationNumber());
        oreTransportDTO.setDedicationReason("Not specified");
        oreTransport.setNewnumberOfBags(0);
        oreTransport.setNewWeight(0);
        oreTransport.setGoldSales(new ArrayList<>());
        oreTransport.setMills(new ArrayList<>());

        // --- Replace above 2 lines with this ---
        List<Mill> mills = new ArrayList<>();

        mills.add(Mill.builder()
                .millid("MILL-001")
                .millType("Hammer Mill")
                .location("Kadoma")
                .build());

        oreTransport.setMills(mills);
        List<GoldSale> goldSales = new ArrayList<>();

        goldSales.add(GoldSale.builder()
                .weight(10.5)
                .price(550.0)
                .buyer("ABC Refinery")
                .build());
        oreTransport.setGoldSales(goldSales);

        oreTransport.setGoldSales(goldSales);
        oreTransport.setSecurityDispatcherStatus("Not Specified");
        oreTransport.setOreUniqueId(generateRegistrationNumber());
        oreTransport.setCreatedBy(principal != null ? principal.getName() : "system");
        oreTransport.setCreatedDate(LocalDateTime.now());
        oreTransport.setSelectedTransportdriver("Not Selected");
        oreTransport.setSelectedTransport("Not Selected");
        oreTransport.setTransportReason("Not specified");

        OreTransport saved = repository.save(oreTransport);
        OreTransportAuditTrail audit = OreTransportAuditTrail.builder()
                .transportId(saved.getId())
                .action("CREATED")
                .description("Ore transport created: OreUniqueId=" + saved.getOreUniqueId())
                .doneBy(principal != null ? principal.getName() : "system")
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "ore_transport_audit_trail");
        return ResponseEntity.ok(saved);
    }

    @Override
    public List<OreTransport> getAll() {
        return repository.findAll();
    }

    @Override
    public OreTransport getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public ResponseEntity<String> setProcessStatus(String id, String ProcessStatus, String Location,
            String TransportReason, String SelectedTransportdriver, String SelectedTransport, Principal principal) {
        Optional<OreTransport> existing = repository.findById(id);
        if (existing.isPresent()) {
            OreTransport transport = existing.get();

            // Check if transport is in Not loaded status
            if (!"Not loaded".equals(transport.getProcessStatus())) {
                return ResponseEntity.status(400).body("Transport is not in Not loaded status");
            }

            // Update status and reason
            transport.setProcessStatus(ProcessStatus);
            transport.setSelectedTransportdriver(SelectedTransportdriver);
            transport.setSelectedTransport(SelectedTransport);
            transport.setTransportReason(TransportReason);
            transport.setLocation(Location);
            transport.setUpdatedBy(principal.getName());
            transport.setUpdatedDate(LocalDateTime.now());

            // Save the transport
            repository.save(transport);

            // Create audit trail
            OreTransportAuditTrail audit = OreTransportAuditTrail.builder()
                    .transportId(id)
                    .action("Transport Status Updated")
                    .description("Transport status updated to: '" + ProcessStatus + "' for transport: '" +
                            transport.getOreUniqueId() + "', registration number: '" + transport.getId() + "'")
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "ore_transport_audit_trail");

            return ResponseEntity.ok("Transport rejected successfully");
        }
        return ResponseEntity.status(404).body("Transport not found");
    }

    @Override
    public ResponseEntity<String> update(String id, OreTransport oreTransport, Principal principal) {
        Optional<OreTransport> existing = repository.findById(id);
        if (existing.isPresent()) {
            OreTransport existingTransport = existing.get();
            existingTransport.setOreUniqueId(oreTransport.getOreUniqueId());
            existingTransport.setShaftNumbers(oreTransport.getShaftNumbers());
            existingTransport.setWeight(oreTransport.getWeight());
            existingTransport.setNumberOfBags(oreTransport.getNumberOfBags());
            existingTransport.setTransportReason(oreTransport.getTransportReason());
            existingTransport.setProcessStatus(oreTransport.getProcessStatus());
            existingTransport.setLocation(oreTransport.getLocation());
            existingTransport.setTransportStatus(oreTransport.getTransportStatus());
            existingTransport.setSelectedTransport(oreTransport.getSelectedTransport());
            existingTransport.setDate(oreTransport.getDate());
            existingTransport.setTime(oreTransport.getTime());
            existingTransport.setUpdatedBy(principal != null ? principal.getName() : "system");
            existingTransport.setUpdatedDate(LocalDateTime.now());

            repository.save(existingTransport);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description("Ore transport updated: OreUniqueId=" + existingTransport.getOreUniqueId())
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "ore_transport_audit_trail");
            return ResponseEntity.ok("Ore transport updated successfully");
        }
        return ResponseEntity.status(404).body("Ore transport not found");
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        Optional<OreTransport> existing = repository.findById(id);
        if (existing.isPresent()) {
            repository.deleteById(id);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("DELETED")
                    .description("Ore transport deleted: OreUniqueId=" + existing.get().getOreUniqueId())
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "ore_transport_audit_trail");
            return ResponseEntity.ok("Ore transport deleted successfully");
        }
        return ResponseEntity.status(404).body("Ore transport not found");
    }
}
