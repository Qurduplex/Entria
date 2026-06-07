package edu.pk.qurduplex.oauthService.repositories;

import edu.pk.qurduplex.oauthService.models.AuthorizationConsent;
import edu.pk.qurduplex.oauthService.models.AuthorizationConsentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorizationConsentRepository extends JpaRepository<AuthorizationConsent, AuthorizationConsentId> {
    Optional<AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
    void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
    List<AuthorizationConsent> findByPrincipalName(String principalName);
}