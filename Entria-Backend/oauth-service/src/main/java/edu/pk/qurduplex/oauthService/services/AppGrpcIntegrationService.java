package edu.pk.qurduplex.oauthService.services;

import edu.pk.qurduplex.common.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AppGrpcIntegrationService {

    @GrpcClient("app-registry-service")
    private AppRegistryGrpcServiceGrpc.AppRegistryGrpcServiceBlockingStub appRegistryStub;

    public record AppExternalDetails(String redirectUri, String appName, String appLogoUrl) {}

    public AppExternalDetails getAppDetails(String clientId) {
        if (clientId == null || "Unknown".equals(clientId)) {
            return new AppExternalDetails(null, "Unknown application (" + clientId + ")", null);
        }

        try {
            AppResponse appResponse = appRegistryStub.getApplicationByClientId(
                    AppRequest.newBuilder().setClientId(clientId).build()
            );

            String appName = appResponse.getAppName().isEmpty() ? "Unknown application (" + clientId + ")" : appResponse.getAppName();
            String logoUrl = appResponse.getLogoUrl().isEmpty() ? null : appResponse.getLogoUrl();

            return new AppExternalDetails(appResponse.getRedirectUri(), appName, logoUrl);

        } catch (Exception e) {
            log.warn("Failed to fetch OAuth data from registry for clientId: {}", clientId, e);
            return new AppExternalDetails(null, "Unknown application (" + clientId + ")", null);
        }
    }
}