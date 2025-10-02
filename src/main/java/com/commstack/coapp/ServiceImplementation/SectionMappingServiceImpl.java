package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.SectionMapping;
import com.commstack.coapp.Repositories.SectionMappingRepository;
import com.commstack.coapp.Service.SectionMappingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionMappingServiceImpl implements SectionMappingService {

    private final SectionMappingRepository repository;

    public SectionMappingServiceImpl(SectionMappingRepository repository) {
        this.repository = repository;
    }

    @Override
    public SectionMapping create(SectionMapping sectionMapping) {
        return repository.save(sectionMapping);
    }

    @Override
    public List<SectionMapping> getAll() {
        return repository.findAll();
    }

    @Override
    public SectionMapping getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public SectionMapping update(String id, SectionMapping updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setType(updated.getType());
                    existing.setCoordinates(updated.getCoordinates());
                    return repository.save(existing);
                })
                .orElse(null);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}
