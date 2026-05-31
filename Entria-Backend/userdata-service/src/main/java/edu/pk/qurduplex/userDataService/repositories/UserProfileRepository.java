package edu.pk.qurduplex.userDataService.repositories;

import edu.pk.qurduplex.userDataService.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    boolean existsByUserId(UUID userId);
    Optional<UserProfile> findByUserId(UUID userId);
}
