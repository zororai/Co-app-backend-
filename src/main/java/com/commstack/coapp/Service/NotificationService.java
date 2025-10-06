package com.commstack.coapp.Service;

import com.commstack.coapp.Models.Notification;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface NotificationService {
    ResponseEntity<Notification> create(Notification notification, Principal principal);

    List<Notification> getAll();

    Notification getById(String id);

    ResponseEntity<String> delete(String id, Principal principal);
}
