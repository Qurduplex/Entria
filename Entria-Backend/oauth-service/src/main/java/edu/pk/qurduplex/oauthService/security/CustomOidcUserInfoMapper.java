package edu.pk.qurduplex.oauthService.security;

import edu.pk.qurduplex.common.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Slf4j
@Service
public class CustomOidcUserInfoMapper implements Function<OidcUserInfoAuthenticationContext, OidcUserInfo> {

    @GrpcClient("identity-service")
    private AuthServiceGrpc.AuthServiceBlockingStub authStub;

    @GrpcClient("userdata-service")
    private UserProfileGrpcServiceGrpc.UserProfileGrpcServiceBlockingStub userProfileStub;

    @Override
    public OidcUserInfo apply(OidcUserInfoAuthenticationContext context) {
        String userId = context.getAuthorization().getPrincipalName();
        Set<String> authorizedScopes = context.getAuthorization().getAuthorizedScopes();
        Map<String, Object> claims = new HashMap<>();

        claims.put("sub", userId);

        if (authorizedScopes.contains("email")) {
            try {
                GetUserEmailResponse emailResp = authStub.getUserEmail(
                        GetUserEmailRequest.newBuilder().setUserId(userId).build());
                if (emailResp.getEmail() != null && !emailResp.getEmail().isEmpty()) {
                    claims.put("email", emailResp.getEmail());
                }
            } catch (Exception e) {
                log.warn("Failed to fetch email via gRPC for user: {}", userId);
            }
        }

        boolean needsProfileData = authorizedScopes.contains("profile") ||
                authorizedScopes.contains("phone") ||
                authorizedScopes.contains("birthdate") ||
                authorizedScopes.contains("gender") ||
                authorizedScopes.contains("picture") ||
                authorizedScopes.contains("pesel");

        log.info("[CustomOidcUserInfoMapper] needsProfileData is {}, authorizedScopes: {}", needsProfileData, authorizedScopes);

        if (needsProfileData) {
            try {
                log.info("[CustomOidcUserInfoMapper] Calling userdata-service gRPC for userId: {}", userId);
                UserProfileResponse profile = userProfileStub.getUserProfile(
                        UserProfileRequest.newBuilder().setUserId(userId).build());

                if (authorizedScopes.contains("profile")) {
                    if (!profile.getFirstName().isEmpty()) claims.put("given_name", profile.getFirstName());
                    if (!profile.getLastName().isEmpty()) claims.put("family_name", profile.getLastName());
                }

                if (authorizedScopes.contains("phone") && !profile.getPhoneNumber().isEmpty()) {
                    claims.put("phone_number", profile.getPhoneNumber());
                }

                if (authorizedScopes.contains("birthdate") && !profile.getBirthDate().isEmpty()) {
                    claims.put("birthdate", profile.getBirthDate());
                }

                if (authorizedScopes.contains("gender") && !profile.getSex().isEmpty()) {
                    claims.put("gender", profile.getSex());
                }

                if (authorizedScopes.contains("picture") && !profile.getProfilePictureUrl().isEmpty()) {
                    claims.put("picture", profile.getProfilePictureUrl());
                }

                if (authorizedScopes.contains("pesel") && !profile.getPesel().isEmpty()) {
                    claims.put("pesel", profile.getPesel());
                }

            } catch (Exception e) {
                log.warn("Failed to fetch profile via gRPC for user: {}", userId);
            }
        }

        return new OidcUserInfo(claims);
    }
}
