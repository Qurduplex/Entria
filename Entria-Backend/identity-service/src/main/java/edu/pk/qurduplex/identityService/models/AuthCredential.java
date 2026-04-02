package edu.pk.qurduplex.identityService.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AuthCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;
    private String passwordHash;
    private boolean isActive;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;
}
