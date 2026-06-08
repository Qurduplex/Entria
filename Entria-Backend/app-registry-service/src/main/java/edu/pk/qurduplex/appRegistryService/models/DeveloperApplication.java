package edu.pk.qurduplex.appRegistryService.models;

import edu.pk.qurduplex.common.models.OAuthPermission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DeveloperApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String clientSecretHash;

    @Column(nullable = false)
    private String name;

    private String logoUrl;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "application_permissions",
            joinColumns = @JoinColumn(name = "application_id")
    )
    @MapKeyColumn(name = "permission_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "is_required")
    private Map<OAuthPermission, Boolean> permissions = new HashMap<>();

    @Column(nullable = false)
    private String redirectUri;

    private String tosPdfUrl;

    private UUID developerId;

    @Builder.Default
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
