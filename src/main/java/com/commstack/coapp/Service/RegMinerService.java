package com.commstack.coapp.Service;

import com.commstack.coapp.Models.Regminer;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;

public interface RegMinerService {
    ResponseEntity<String> create(Regminer miner, Principal principal);

    List<Regminer> getAll();

    Regminer getById(String id);

    ResponseEntity<String> update(String id, Regminer miner, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    ResponseEntity<String> approval(String id, Principal principal);

    ResponseEntity<String> pushBack(String id, String reason, Principal principal);

    ResponseEntity<String> getallApprovedMiners();

    ResponseEntity<String> reject(String id, String reason, Principal principal);

    ResponseEntity<String> getallRejectedMiners();

    ResponseEntity<String> getallPushedBackMiners();

    ResponseEntity<String> getallPendingMiners();

}
