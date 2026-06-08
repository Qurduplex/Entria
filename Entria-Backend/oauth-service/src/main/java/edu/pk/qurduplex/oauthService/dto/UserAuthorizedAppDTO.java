package edu.pk.qurduplex.oauthService.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserAuthorizedAppDTO {
    private String clientId;
    private String appName;
    private String appLogoUrl;
    private String redirectUri;
    private List<String> grantedAuthorities;
}