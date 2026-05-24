package edu.pk.qurduplex.appRegistryService.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterApplicationResponse {
    private String clientId;
    private String clientSecret;
    private String name;
    private String redirectUri;
    private String logoUrl;
}
