
package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Repositories.SectionRepository;
import com.commstack.coapp.Models.Section;

import com.commstack.coapp.Repositories.CompanyRegistrationRepository;
import com.commstack.coapp.Repositories.RegMinerRepository;
import com.commstack.coapp.Models.Loan;
import com.commstack.coapp.Models.Mill;
import com.commstack.coapp.Models.Regminer;
import com.commstack.coapp.Models.UserAuditTrail;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.security.Principal;
import java.time.LocalDateTime;
import com.commstack.coapp.Models.ShaftAssignment;
import com.commstack.coapp.Repositories.ShaftAssignmentRepository;
import com.commstack.coapp.Service.ShaftAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import com.commstack.coapp.Models.Loan;
import java.util.List;
import java.util.Optional;

@Service
public class ShaftAssignmentServiceImpl implements ShaftAssignmentService {

    /**
     * Returns a list of all shaftNumbers for a given sectionName.
     */
    public List<String> getShaftNumbersBySectionName(String sectionName) {
        return repository.findAll().stream()
                .filter(s -> sectionName != null && sectionName.equalsIgnoreCase(s.getSectionName()))
                .map(ShaftAssignment::getShaftNumbers)
                .filter(sn -> sn != null && !sn.isEmpty())
                .toList();
    }

    /**
     * Returns a list of all shaftNumbers from all ShaftAssignments.
     */
    public List<String> getAllShaftNumbers() {
        return repository.findAll().stream()
                .map(ShaftAssignment::getShaftNumbers)
                .filter(s -> s != null && !s.isEmpty())
                .toList();
    }

    /**
     * Returns all loans for a ShaftAssignment found by shaftNumbers.
     */
    public List<Loan> getLoansByShaftNumbers(String shaftNumbers) {
        ShaftAssignment shaftAssignment = repository.findByShaftNumbers(shaftNumbers);
        if (shaftAssignment != null && shaftAssignment.getLoans() != null) {
            return shaftAssignment.getLoans();
        }
        return new ArrayList<>();
    }

    /**
     * Updates paymentStatus, amountPaid, and balance for the first Loan in a
     * ShaftAssignment.
     * If amountPaid < amountOrGrams, sets paymentStatus to PARTIAL_PAYMENT and
     * balance to the difference.
     * If amountPaid == amountOrGrams and amountOrGrams > 0, sets paymentStatus to
     * PAID and balance to 0.
     * If both are 0, sets paymentStatus to Not Yet Specified and balance to 0.
     * Returns the updated ShaftAssignment or null if not found.
     */
    public ShaftAssignment updateLoanPayment(String shaftAssignmentId, double amountPaid, Principal principal) {
        ShaftAssignment shaftAssignment = repository.findByShaftNumbers(shaftAssignmentId);
        if (shaftAssignment != null) {
            List<Loan> loans = shaftAssignment.getLoans();
            if (loans != null && !loans.isEmpty()) {
                Loan loan = loans.get(0);
                double amountOrGrams = loan.getAmountOrGrams();
                double balance = 0;
                String paymentStatus = "Not Yet Specified";
                if (amountPaid < amountOrGrams) {
                    balance = amountOrGrams - amountPaid;
                    paymentStatus = "PARTIAL_PAYMENT";
                } else if (amountPaid == amountOrGrams && amountOrGrams > 0) {
                    balance = 0;
                    paymentStatus = "PAID";
                }
                shaftAssignment.setAmountPaid(amountPaid);
                shaftAssignment.setBalance(balance);
                loan.setPaymentStatus(paymentStatus);
                loans.set(0, loan);
                shaftAssignment.setLoans(loans);
                shaftAssignment.setUpdatedBy(principal.getName());
                shaftAssignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
                ShaftAssignment updated = repository.save(shaftAssignment);
                UserAuditTrail audit = UserAuditTrail.builder()
                        .userId(shaftAssignmentId)
                        .action("LOAN_PAYMENT_UPDATED")
                        .description("Updated loan payment for shaft assignment id: " + shaftAssignmentId)
                        .doneBy(principal.getName())
                        .dateTime(LocalDateTime.now())
                        .build();
                mongoTemplate.save(audit, "user_audit_trail");
                return updated;
            }
        }
        return null;
    }

    public ShaftAssignment pushBackLoan(String shaftAssignmentId, Principal principal) {
        Optional<ShaftAssignment> result = repository.findById(shaftAssignmentId);
        if (result.isPresent()) {
            ShaftAssignment shaftAssignment = result.get();
            List<Loan> loans = shaftAssignment.getLoans();
            if (loans != null && !loans.isEmpty() && "PENDING".equalsIgnoreCase(loans.get(0).getStatus())) {
                loans.get(0).setStatus("PUSHED_BACK");
                shaftAssignment.setLoans(loans);
                shaftAssignment.setUpdatedBy(principal.getName());
                shaftAssignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
                ShaftAssignment updated = repository.save(shaftAssignment);
                UserAuditTrail audit = UserAuditTrail.builder()
                        .userId(shaftAssignmentId)
                        .action("LOAN_PUSHED_BACK")
                        .description("Pushed back loan for shaft assignment id: " + shaftAssignmentId)
                        .doneBy(principal.getName())
                        .dateTime(LocalDateTime.now())
                        .build();
                mongoTemplate.save(audit, "user_audit_trail");
                return updated;
            }
        }
        return null;
    }

    public ShaftAssignment rejectLoan(String shaftAssignmentId, Principal principal) {
        Optional<ShaftAssignment> result = repository.findById(shaftAssignmentId);
        if (result.isPresent()) {
            ShaftAssignment shaftAssignment = result.get();
            List<Loan> loans = shaftAssignment.getLoans();
            if (loans != null && !loans.isEmpty() && "PENDING".equalsIgnoreCase(loans.get(0).getStatus())) {
                loans.get(0).setStatus("REJECTED");
                shaftAssignment.setLoans(loans);
                shaftAssignment.setUpdatedBy(principal.getName());
                shaftAssignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
                ShaftAssignment updated = repository.save(shaftAssignment);
                UserAuditTrail audit = UserAuditTrail.builder()
                        .userId(shaftAssignmentId)
                        .action("LOAN_REJECTED")
                        .description("Rejected loan for shaft assignment id: " + shaftAssignmentId)
                        .doneBy(principal.getName())
                        .dateTime(LocalDateTime.now())
                        .build();
                mongoTemplate.save(audit, "user_audit_trail");
                return updated;
            }
        }
        return null;
    }

    public ShaftAssignment approveLoan(String shaftAssignmentId, Principal principal) {
        Optional<ShaftAssignment> result = repository.findById(shaftAssignmentId);
        if (result.isPresent()) {
            ShaftAssignment shaftAssignment = result.get();
            List<Loan> loans = shaftAssignment.getLoans();
            if (loans != null && !loans.isEmpty() && "PENDING".equalsIgnoreCase(loans.get(0).getStatus())) {
                loans.get(0).setStatus("APPROVED");
                shaftAssignment.setLoans(loans);
                shaftAssignment.setUpdatedBy(principal.getName());
                shaftAssignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
                ShaftAssignment updated = repository.save(shaftAssignment);
                UserAuditTrail audit = UserAuditTrail.builder()
                        .userId(shaftAssignmentId)
                        .action("LOAN_APPROVED")
                        .description("Approved loan for shaft assignment id: " + shaftAssignmentId)
                        .doneBy(principal.getName())
                        .dateTime(LocalDateTime.now())
                        .build();
                mongoTemplate.save(audit, "user_audit_trail");
                return updated;
            }
        }
        return null;
    }

    public ShaftAssignment updateLoanDetails(String shaftAssignmentId, String loanName, String paymentMethod,
            double amountOrGrams, String purpose, String status, String reason, Principal principal) {
        Optional<ShaftAssignment> result = repository.findById(shaftAssignmentId);
        if (result.isPresent()) {
            ShaftAssignment shaftAssignment = result.get();
            List<Loan> loans = new ArrayList<>();
            loans.add(Loan.builder()
                    .loanName(loanName)
                    .paymentMethod(paymentMethod)
                    .amountOrGrams(amountOrGrams)
                    .purpose(purpose)
                    .status(status)
                    .reason(reason)
                    .build());
            shaftAssignment.setLoans(loans);
            shaftAssignment.setUpdatedBy(principal.getName());
            shaftAssignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
            ShaftAssignment updated = repository.save(shaftAssignment);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(shaftAssignmentId)
                    .action("UPDATED_LOAN_DETAILS")
                    .description("Updated loan details for shaft assignment id: " + shaftAssignmentId)
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");
            return updated;
        }
        return null;
    }

    public List<ShaftAssignment> getByMinerId(String minerId) {
        return repository.findAll().stream()
                .filter(s -> minerId != null && minerId.equals(String.valueOf(s.getMinerId())))
                .toList();
    }

    @Autowired
    private SectionRepository sectionRepository;

    public ShaftAssignment pushBack(String id, String reason, Principal principal) {
        Optional<ShaftAssignment> result = repository.findById(id);
        if (result.isPresent()) {
            ShaftAssignment assignment = result.get();
            assignment.setStatus("PUSHED_BACK");
            assignment.setReason(reason);
            assignment.setUpdatedBy(principal.getName());
            assignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
            ShaftAssignment updated = repository.save(assignment);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("PUSHED_BACK")
                    .description(
                            "Pushed back shaft assignment for minerId: " + updated.getMinerId() + ", reason: " + reason)
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");
            return updated;
        }
        return null;
    }

    public List<ShaftAssignment> getAllApproved() {
        return repository.findAll().stream()
                .filter(s -> "APPROVED".equalsIgnoreCase(s.getStatus()))
                .toList();
    }

    public ShaftAssignment approve(String id, Principal principal) {
        Optional<ShaftAssignment> result = repository.findById(id);
        if (result.isPresent()) {
            ShaftAssignment assignment = result.get();
            assignment.setStatus("APPROVED");
            assignment.setUpdatedBy(principal.getName());
            assignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
            assignment.setReason(null);
            ShaftAssignment updated = repository.save(assignment);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("APPROVED")
                    .description("Approved shaft assignment for minerId: " + updated.getMinerId())
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");
            return updated;
        }
        return null;
    }

    @Autowired
    private RegMinerRepository regMinerRepository;
    @Autowired
    private CompanyRegistrationRepository companyRegistrationRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ShaftAssignmentRepository repository;

    @Override
    public ShaftAssignment create(ShaftAssignment shaftAssignment, Principal principal) {
        // Check if creating this shaft exceeds the allowed number in Section
        shaftAssignment.setCreatedBy(principal.getName());
        shaftAssignment.setCreatedAt(LocalDateTime.now().toLocalDate());
        shaftAssignment.setUpdatedBy(principal.getName());
        shaftAssignment.setAmountPaid(0);
        shaftAssignment.setBalance(0);
        shaftAssignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
        shaftAssignment.setStatus("PENDING"); // Assuming reason is not set during creation
        shaftAssignment.setOperationStatus(false); // Set default operation status
        ShaftAssignment saved = repository.save(shaftAssignment);
        shaftAssignment.setId(saved.getId());
        shaftAssignment.setLoans(new ArrayList<Loan>());

        // --- Replace above 2 lines with this ---
        List<Loan> loans = new ArrayList<>();

        loans.add(Loan.builder()
                .loanName("Not Yet Specified")
                .paymentMethod("Unknown")
                .amountOrGrams(0)
                .paymentStatus("")
                .purpose("Unknown")
                .status("Not Borrowings")
                .reason("Unknown")
                .build());

        // Update shaftnumber in Regminer or CompanyRegistration if minerId matches
        boolean updated = false;
        if (saved.getMinerId() != null && !saved.getMinerId().isEmpty()) {
            // Check if the minerId exists in Regminer
            // Assuming minerId is a String, adjust as necessary
            String minerIdStr = String.valueOf(saved.getMinerId());
            Optional<Regminer> regminerOpt = regMinerRepository.findById(minerIdStr);

            if (regminerOpt.isPresent()) {
                Regminer regminer = regminerOpt.get();

                // Get the current shaft number
                Integer shaftNumberObj = regminer.getShaftnumber();
                int currentShaftNumber = shaftNumberObj != null ? shaftNumberObj : 0;

                // Increment by 1
                regminer.setShaftnumber(currentShaftNumber + 1);

                regMinerRepository.save(regminer);
                updated = true;
            }

            // If not found in Regminer, try CompanyRegistration
            if (!updated) {
                companyRegistrationRepository.findById(minerIdStr).ifPresent(company -> {
                    Integer shaftNumberObj = company.getShaftnumber();
                    int currentShaftNumber = shaftNumberObj != null ? shaftNumberObj : 0;
                    company.setShaftnumber(currentShaftNumber + 1);
                    companyRegistrationRepository.save(company);
                });
            }
        }

        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(saved.getId())
                .action("CREATED")
                .description("Created shaft assignment for minerId: " + saved.getMinerId())
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "user_audit_trail");
        return saved;
    }

    @Override
    public ShaftAssignment getById(String id) {
        Optional<ShaftAssignment> result = repository.findById(id);
        return result.orElse(null);
    }

    @Override
    public List<ShaftAssignment> getAll() {
        return repository.findAll();
    }

    @Override
    public ShaftAssignment update(String id, ShaftAssignment shaftAssignment, Principal principal) {
        shaftAssignment.setUpdatedBy(principal.getName());
        shaftAssignment.setUpdatedAt(LocalDateTime.now().toLocalDate());
        shaftAssignment.setStatus("PENDING");

        if (repository.existsById(id)) {
            shaftAssignment.setId(id);
            ShaftAssignment updated = repository.save(shaftAssignment);
            UserAuditTrail audit = UserAuditTrail.builder()
                    .userId(id)
                    .action("UPDATED")
                    .description("Updated shaft assignment for minerId: " + updated.getMinerId())
                    .doneBy(principal.getName())
                    .dateTime(LocalDateTime.now())
                    .build();
            mongoTemplate.save(audit, "user_audit_trail");
            return updated;
        }
        return null;
    }

    @Override
    public void delete(String id, Principal principal) {
        if (!repository.existsById(id)) {
            return; // Handle case where the ID does not exist
        }
        repository.deleteById(id);
        UserAuditTrail audit = UserAuditTrail.builder()
                .userId(id)
                .action("DELETED")
                .description("Deleted shaft assignment with id: " + id)
                .doneBy(principal.getName())
                .dateTime(LocalDateTime.now())
                .build();
        mongoTemplate.save(audit, "user_audit_trail");
    }
}
