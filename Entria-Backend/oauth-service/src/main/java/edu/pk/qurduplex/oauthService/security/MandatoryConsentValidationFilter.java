package edu.pk.qurduplex.oauthService.security;

import edu.pk.qurduplex.common.grpc.AppRegistryGrpcServiceGrpc;
import edu.pk.qurduplex.common.grpc.AppRequest;
import edu.pk.qurduplex.common.grpc.AppResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class MandatoryConsentValidationFilter extends OncePerRequestFilter {

    private final AppRegistryGrpcServiceGrpc.AppRegistryGrpcServiceBlockingStub appStub;

    public MandatoryConsentValidationFilter(AppRegistryGrpcServiceGrpc.AppRegistryGrpcServiceBlockingStub appStub) {
        this.appStub = appStub;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("POST".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().equals("/oauth2/authorize")) {
            String consentAction = request.getParameter("consent_action");

            if ("approve".equals(consentAction)) {
                String clientId = request.getParameter("client_id");

                List<String> submittedScopes = new ArrayList<>();
                String[] scopeParams = request.getParameterValues("scope");

                log.info("==== CONSENT DIAGNOSTICS ====");
                log.info("1. Raw scope parameters from browser: {}", Arrays.toString(scopeParams));

                if (scopeParams != null) {
                    for (String s : scopeParams) {
                        submittedScopes.addAll(Arrays.asList(s.split(" ")));
                    }
                }

                log.info("2. Processed scopes (submittedScopes): {}", submittedScopes);

                try {
                    AppResponse appResponse = appStub.getApplicationByClientId(
                            AppRequest.newBuilder().setClientId(clientId).build()
                    );

                    Map<String, Boolean> permissionsMap = appResponse.getPermissionsMap();

                    log.info("3. Mandatory scopes from permissions map:");
                    List<String> missingMandatory = new ArrayList<>();

                    for (Map.Entry<String, Boolean> entry : permissionsMap.entrySet()) {
                        String scopeName = entry.getKey();
                        boolean isMandatory = entry.getValue();

                        if (isMandatory) {
                            boolean isPresent = submittedScopes.contains(scopeName);
                            log.info("   - {}: {} (Mandatory: true, Present: {})", scopeName,
                                    isPresent ? "✓" : "✗", isPresent);

                            if (!isPresent) {
                                missingMandatory.add(scopeName);
                            }
                        }
                    }

                    if (!missingMandatory.isEmpty()) {
                        log.warn("Validation FAILED: Missing mandatory scopes: {}", missingMandatory);
                        String state = request.getParameter("state");
                        response.sendRedirect("/oauth2/consent?client_id=" + clientId + "&state=" + state
                                + "&error=missing_mandatory&missing=" + String.join(",", missingMandatory));
                        return;
                    }

                    log.info("✓ All mandatory scopes are present - validation PASSED");

                } catch (Exception e) {
                    log.error("gRPC error while validating mandatory scopes", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "gRPC error while validating mandatory scopes");
                    return;
                }

                log.info("=============================");
            }
        }

        filterChain.doFilter(request, response);
    }
}