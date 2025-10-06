package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.Notification;
import com.commstack.coapp.Service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody Notification notification, Principal principal) {
        ResponseEntity<Notification> resp = service.create(notification, principal);
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            return ResponseEntity.created(URI.create("/api/notifications/" + resp.getBody().getId())).body(resp.getBody());
        }
        return ResponseEntity.status(resp.getStatusCode()).build();
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getById(@PathVariable String id) {
        Notification n = service.getById(id);
        return n != null ? ResponseEntity.ok(n) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }
}
