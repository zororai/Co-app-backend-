package com.commstack.coapp.Service;

import com.commstack.coapp.Models.SectionMapping;
import java.util.List;

public interface SectionMappingService {
    SectionMapping create(SectionMapping sectionMapping);

    List<SectionMapping> getAll();

    SectionMapping getById(String id);

    SectionMapping update(String id, SectionMapping updated);

    void delete(String id);
}
