package edu.pk.qurduplex.appRegistryService.repositories;

import edu.pk.qurduplex.appRegistryService.models.DeveloperApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeveloperApplicationRepository extends JpaRepository<DeveloperApplication, UUID> {
    boolean existsByNameIgnoreCase(String name);
    List<DeveloperApplication> findAllByDeveloperId(UUID developerId);
    Optional<DeveloperApplication> findByClientId(String clientId);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
}
