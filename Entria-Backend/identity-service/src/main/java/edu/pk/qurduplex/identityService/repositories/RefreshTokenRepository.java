package edu.pk.qurduplex.identityService.repositories;

import edu.pk.qurduplex.identityService.models.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {
    void deleteByUserId(UUID userId);
}
