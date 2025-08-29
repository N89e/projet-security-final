package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST pour les endpoints de test "Hello".
 *
 * Fournit des endpoints publics et sécurisés avec contrôle des rôles.
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    /**
     * Endpoint accessible à tous, même sans authentification.
     *
     * @return un message public simple
     */
    @GetMapping("/public")
    public ResponseEntity<String> getPublic() {
        return ResponseEntity.ok("Hello getPublic");
    }

    /**
     * Endpoint accessible aux utilisateurs authentifiés avec rôle USER ou ADMIN.
     *
     * @return un message privé pour USER ou ADMIN
     */
    @GetMapping("/private")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> getPrivate() {
        return ResponseEntity.ok("Hello getPrivate");
    }

    /**
     * Endpoint réservé uniquement aux utilisateurs avec rôle ADMIN.
     *
     * @return un message privé pour ADMIN
     */
    @GetMapping("/private-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getPrivateAdmin() {
        return ResponseEntity.ok("Hello private admin");
    }
}
