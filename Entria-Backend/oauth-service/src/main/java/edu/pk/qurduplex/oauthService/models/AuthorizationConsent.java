package edu.pk.qurduplex.oauthService.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oauth2_authorization_consent")
@IdClass(AuthorizationConsentId.class)
public class AuthorizationConsent {

    @Id
    @Column(length = 100)
    private String registeredClientId;

    @Id
    @Column(length = 200)
    private String principalName;

    @Column(length = 1000, nullable = false)
    private String authorities;
}