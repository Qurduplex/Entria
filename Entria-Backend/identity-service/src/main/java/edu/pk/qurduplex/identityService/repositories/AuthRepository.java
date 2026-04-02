package edu.pk.qurduplex.identityService.repositories;

import edu.pk.qurduplex.identityService.models.AuthCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<AuthCredential, UUID> {
    boolean existsByEmail(String email);
    Optional<AuthCredential> findByEmail(String email);
}
