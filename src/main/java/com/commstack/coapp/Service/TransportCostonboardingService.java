package com.commstack.coapp.Service;

import com.commstack.coapp.Models.TransportCostonboarding;
import java.security.Principal;
import java.util.List;

public interface TransportCostonboardingService {
    TransportCostonboarding create(TransportCostonboarding transportCostonboarding, Principal principal);

    TransportCostonboarding pushBack(String id, String reason, Principal principal);

    TransportCostonboarding approve(String id, Principal principal);

    TransportCostonboarding reject(String id, Principal principal);

    List<TransportCostonboarding> getAll();

    TransportCostonboarding getById(String id);
}
