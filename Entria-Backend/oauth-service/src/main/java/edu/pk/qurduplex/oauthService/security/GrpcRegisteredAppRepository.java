package edu.pk.qurduplex.oauthService.security;

import edu.pk.qurduplex.common.grpc.AppRegistryGrpcServiceGrpc;
import edu.pk.qurduplex.common.grpc.AppRequest;
import edu.pk.qurduplex.common.grpc.AppResponse;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

@Component
public class GrpcRegisteredAppRepository implements RegisteredClientRepository {

    @GrpcClient("app-registry-service")
    private AppRegistryGrpcServiceGrpc.AppRegistryGrpcServiceBlockingStub appStub;

    @Override
    public RegisteredClient findByClientId(String clientId) {
        try {
            AppResponse response = appStub.getApplicationByClientId(
                    AppRequest.newBuilder().setClientId(clientId).build()
            );

            return RegisteredClient.withId(response.getId())
                    .clientId(response.getClientId())
                    .clientSecret(response.getClientSecretHash())
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUri(response.getRedirectUri())
                    .scopes(scopes -> {
                        scopes.add(OidcScopes.OPENID);
                        for (String permission : response.getPermissionsMap().keySet()) {
                            scopes.add(permission.toLowerCase());
                        }
                    })
                    .clientSettings(org.springframework.security.oauth2.server.authorization.settings.ClientSettings.builder()
                            .requireAuthorizationConsent(true)
                            .build())
                    .build();

        } catch (StatusRuntimeException e) {
            return null;
        }
    }

    @Override
    public RegisteredClient findById(String id) {
        try {
            AppResponse response = appStub.getApplicationById(
                    edu.pk.qurduplex.common.grpc.AppIdRequest.newBuilder().setId(id).build()
            );

            return RegisteredClient.withId(response.getId())
                    .clientId(response.getClientId())
                    .clientSecret(response.getClientSecretHash())
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUri(response.getRedirectUri())
                    .scopes(scopes -> {
                        scopes.add(OidcScopes.OPENID);
                        for (String permission : response.getPermissionsMap().keySet()) {
                            scopes.add(permission.toLowerCase());
                        }
                    })
                    .clientSettings(org.springframework.security.oauth2.server.authorization.settings.ClientSettings.builder()
                            .requireAuthorizationConsent(true)
                            .build())
                    .build();

        } catch (io.grpc.StatusRuntimeException e) {
            return null;
        }
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        // todo: maybe todo
        throw new UnsupportedOperationException("Saving clients is handled by app-registry-service");
    }
}
