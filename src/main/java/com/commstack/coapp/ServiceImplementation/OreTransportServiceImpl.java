
package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.CompanyRegistration;
import com.commstack.coapp.Models.GoldSale;
import com.commstack.coapp.Models.Mill;
import com.commstack.coapp.Models.MinerAuditTrail;
import com.commstack.coapp.Models.OreSample;
import com.commstack.coapp.Models.OreTransport;
import com.commstack.coapp.Models.OreTransportAuditTrail;
import com.commstack.coapp.Models.Regminer;
import com.commstack.coapp.Models.TransportCost;
import com.commstack.coapp.Repositories.OreTransportRepository;
import com.commstack.coapp.Service.OreTransportService;

import jakarta.mail.Transport;

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

    // Update the first GoldSale in OreTransport by oreTransportId
    public ResponseEntity<OreTransport> updateGoldSale(String oreTransportId, double weight, double price,
            String buyer) {
        Optional<OreTransport> existing = repository.findById(oreTransportId);
        if (existing.isPresent()) {
            OreTransport oreTransport = existing.get();
            List<GoldSale> goldSales = oreTransport.getGoldSales();
            if (goldSales != null && !goldSales.isEmpty()) {
                GoldSale goldSale = goldSales.get(0);
                goldSale.setWeight(weight);
                goldSale.setPrice(price);
                goldSale.setBuyer(buyer);
                oreTransport.setGoldSales(goldSales);
                repository.save(oreTransport);
                // Audit trail
                UserAuditTrail audit = UserAuditTrail.builder()
                        .userId(oreTransportId)
                        .action("UPDATE_GOLD_SALE")
                        .description("Updated gold sale for OreTransport id=" + oreTransportId)
                        .doneBy("system")
                        .dateTime(java.time.LocalDateTime.now())
                        .build();
                mongoTemplate.save(audit, "ore_transport_audit_trail");
                Optional<com.commstack.coapp.Models.MillOnboarding> millOnboardingOpt = millOnboardingRepository
                        .findById(oreTransport.getId());
                if (millOnboardingOpt.isPresent()) {
                    com.commstack.coapp.Models.MillOnboarding millOnboarding = millOnboardingOpt.get();
                    millOnboarding.setActiveStatus(true);
                    millOnboardingRepository.save(millOnboarding);
                }
                return ResponseEntity.ok(oreTransport);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // Update OreSample by id, only if its reason, result, and status are the
    // default values

    @Override
    public ResponseEntity<OreTransport> updateSampleIfDefault(String oreTransportId, String newReason,
            double newResult, String newStatus) {
        Optional<OreTransport> existing = repository.findById(oreTransportId);
        if (existing.isPresent()) {
            OreTransport oreTransport = existing.get();
            List<OreSample> oreSamples = oreTransport.getOreSample();
            if (oreSamples != null) {
                for (OreSample sample : oreSamples) {
                    if (0.0 == sample.getResult()) {
                        sample.setReason(newReason);
                        sample.setResult(newResult);
                        sample.setStatus(newStatus);
                        if (newStatus.equals("REJECTED")) {
                            oreTransport.setProcessStatus("REJECTED");
                        } else {
                            oreTransport.setProcessStatus("APPROVED");
                        }
                        break;
                    }
                }
                oreTransport.setOreSample(oreSamples);
                repository.save(oreTransport);
                // Add audit trail for sample collection
                UserAuditTrail audit = UserAuditTrail.builder()
                        .userId(oreTransportId)
                        .action("COLLECT_SAMPLE")
                        .description("Collected ore sample for OreTransport id=" + oreTransportId)
                        .doneBy("system")
                        .dateTime(java.time.LocalDateTime.now())
                        .build();
                mongoTemplate.save(audit, "ore_transport_audit_trail");
                return ResponseEntity.ok(oreTransport);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // Update OreSample status and reason when current status is 'Unknown'
    @Override
    public ResponseEntity<OreTransport> updateSampleStatusAndReason(String oreTransportId, String newStatus, String newReason) {
        Optional<OreTransport> existing = repository.findById(oreTransportId);
        if (existing.isPresent()) {
            OreTransport oreTransport = existing.get();
            List<OreSample> oreSamples = oreTransport.getOreSample();
            if (oreSamples != null) {
                for (OreSample sample : oreSamples) {
                    if ("Unknown".equals(sample.getStatus())) {
                        sample.setStatus(newStatus);
                        sample.setReason(newReason);
                        
                        // Update process status based on the new status
                        if ("REJECTED".equals(newStatus)) {
                            oreTransport.setProcessStatus("REJECTED");
                        } else if ("APPROVED".equals(newStatus)) {
                            oreTransport.setProcessStatus("APPROVED");
                        }
                        break;
                    }
                }
                oreTransport.setOreSample(oreSamples);
                repository.save(oreTransport);
                
                // Add audit trail
                UserAuditTrail audit = UserAuditTrail.builder()
                        .userId(oreTransportId)
                        .action("UPDATE_SAMPLE_STATUS_REASON")
                        .description("Updated ore sample status and reason for OreTransport id=" + oreTransportId)
                        .doneBy("system")
                        .dateTime(java.time.LocalDateTime.now())
                        .build();
                mongoTemplate.save(audit, "ore_transport_audit_trail");
                return ResponseEntity.ok(oreTransport);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // Collect sample: update the first OreSample with sampleType 'Unknown' in the
    // OreTransport's oreSamples list
    @Override
    public ResponseEntity<OreTransport> collectSample(String oreTransportId, String sampleType,
            String sampleWeight, String status, String sampleSize) {
        Optional<OreTransport> existing = repository.findById(oreTransportId);
        if (existing.isPresent()) {
            OreTransport oreTransport = existing.get();
            List<OreSample> oreSamples = oreTransport.getOreSample();
            if (oreSamples != null) {
                for (OreSample sample : oreSamples) {
                    if ("Unknown".equals(sample.getSampleType())) {

                        sample.setSampleType(sampleType);
                        sample.setSampleWeight(sampleWeight);
                        sample.setStatus(status);
                        sample.setReason("Unknown");
                        sample.setResult(0.0);
                        break;
                    }
                }
                oreTransport.setOreSample(oreSamples);
                repository.save(oreTransport);
                // Add audit trail for sample collection
                UserAuditTrail audit = UserAuditTrail.builder()
                        .userId(oreTransportId)
                        .action("COLLECT_SAMPLE")
                        .description("Collected ore sample for OreTransport id=" + oreTransportId)
                        .doneBy("system")
                        .dateTime(java.time.LocalDateTime.now())
                        .build();
                mongoTemplate.save(audit, "ore_transport_audit_trail");
                return ResponseEntity.ok(oreTransport);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @Autowired
    private com.commstack.coapp.Repositories.MillOnboardingRepository millOnboardingRepository;

    // Update mills to a single 'Unknown' mill for a given OreTransport id
    public ResponseEntity<OreTransport> setMillsToUnknown(String id, String millid, String millName, String millType,
            String location, Principal principal) {
        Optional<OreTransport> existing = repository.findById(id);
        if (existing.isPresent()) {
            OreTransport oreTransport = existing.get();

            // Only update mills if the current list contains only a mill with millid
            // 'Unknown'
            List<Mill> currentMills = oreTransport.getMills();
            boolean onlyUnknown = currentMills != null && currentMills.size() == 1
                    && "Unknown".equals(currentMills.get(0).getMillid());
            if (onlyUnknown) {
                oreTransport.setProcessStatus("In Progress");
                List<Mill> millsList = new ArrayList<>();
                millsList.add(Mill.builder()
                        .millid(millid)
                        .millName(millName)
                        .millType(millType)
                        .location(location)
                        .build());
                oreTransport.setMills(millsList);
                repository.save(oreTransport);
            }

            // Set related MillOnboarding to inactive (activeStatus=false)
            Optional<com.commstack.coapp.Models.MillOnboarding> millOnboardingOpt = millOnboardingRepository
                    .findById(millid);
            if (millOnboardingOpt.isPresent()) {
                com.commstack.coapp.Models.MillOnboarding millOnboarding = millOnboardingOpt.get();
                millOnboarding.setActiveStatus(false);
                millOnboardingRepository.save(millOnboarding);
            }

            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("SET_MILLS_UNKNOWN")
                    .description("Set mills to a single 'Unknown' mill for OreTransport id=" + id)
                    .doneBy(principal != null ? principal.getName() : "system")
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "ore_transport_audit_trail");
            return ResponseEntity.ok(oreTransport);
        }
        return ResponseEntity.status(404).body(null);
    }

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

    @Autowired
    private com.commstack.coapp.Repositories.TransportCostonboardingRepository transportCostonboardingRepository;

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
        oreTransport.setOreSample(new ArrayList<>());

        // --- Replace above 2 lines with this ---
        List<Mill> mills = new ArrayList<>();

        mills.add(Mill.builder()
                .millid("Unknown")
                .millName("Unknown")
                .millType("Unknown")
                .location("Unknown")
                .build());

        oreTransport.setMills(mills);

        // --- Replace above 2 lines with this --
        List<OreSample> oreSamples = new ArrayList<>();

        oreSamples.add(OreSample.builder()

                .sampleType("Unknown")
                .sampleWeight("Unknown")
                .status("Unknown")
                .result(0.0)
                .reason("Unknown")
                .build());

        oreTransport.setOreSample(oreSamples);

        List<GoldSale> goldSales = new ArrayList<>();

        goldSales.add(GoldSale.builder()
                .weight(00.0)
                .price(0.00)
                .buyer("Unknown")
                .build());
        oreTransport.setGoldSales(goldSales);

        List<TransportCost> transportCosts = new ArrayList<>();
        // Fetch approved TransportCostonboarding records
        List<com.commstack.coapp.Models.TransportCostonboarding> approvedOnboardings = transportCostonboardingRepository
                .findAll()
                .stream()
                .filter(tc -> "APPROVED".equalsIgnoreCase(tc.getStatus()))
                .toList();

        if (!approvedOnboardings.isEmpty()) {
            for (com.commstack.coapp.Models.TransportCostonboarding tc : approvedOnboardings) {
                transportCosts.add(TransportCost.builder()
                        .paymentMethod(tc.getPaymentMethod())
                        .amountOrGrams(tc.getAmountOrGrams())
                        .status("Not Paid")
                        .reason("Ore Transport Cost")
                        .build());
            }
        } else {
            transportCosts.add(TransportCost.builder()
                    .paymentMethod("Not Specified")
                    .amountOrGrams(0.0)
                    .status("Not Paid")
                    .reason("Not Specified")
                    .build());
        }
        oreTransport.setTransportCosts(transportCosts);

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
