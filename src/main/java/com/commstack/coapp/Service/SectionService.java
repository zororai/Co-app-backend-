package com.commstack.coapp.Service;

import com.commstack.coapp.Models.Section;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;

public interface SectionService {
    ResponseEntity<String> create(Section section, Principal principal);

    List<Section> getAll();

    Section getById(String id);

    ResponseEntity<String> update(String id, Section section, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    ResponseEntity<String> approval(String id, Principal principal);

    ResponseEntity<String> reject(String id, String reason, Principal principal);

    ResponseEntity<String> pushBack(String id, String reason, Principal principal);

    List<Section> findByMinerId(String minerId);

    Section findBySectionNameAndMinerId(String sectionName, String minerId);

    List<Section> findByNumberOfShaft(String numberOfShaft);

    ResponseEntity<List<Section>> getAllPendingSections();

    ResponseEntity<List<Section>> getAllApprovedSections();

    ResponseEntity<List<Section>> getAllRejectedSections();

    ResponseEntity<List<Section>> getAllPushedBackSections();

    ResponseEntity<String> activateSection(String id, Principal principal);

    ResponseEntity<String> deactivateSection(String id, Principal principal);

    ResponseEntity<List<Section>> getDeactivatedPendingSections();
}
