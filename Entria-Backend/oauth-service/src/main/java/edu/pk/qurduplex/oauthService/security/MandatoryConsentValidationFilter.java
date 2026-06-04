package edu.pk.qurduplex.oauthService.security;

import edu.pk.qurduplex.common.grpc.AppRegistryGrpcServiceGrpc;
import edu.pk.qurduplex.common.grpc.AppRequest;
import edu.pk.qurduplex.common.grpc.AppResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class MandatoryConsentValidationFilter extends OncePerRequestFilter {

    @GrpcClient("app-registry-service")
    private AppRegistryGrpcServiceGrpc.AppRegistryGrpcServiceBlockingStub appStub;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("POST".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().equals("/oauth2/authorize")) {
            String consentAction = request.getParameter("consent_action");

            if ("approve".equals(consentAction)) {
                String clientId = request.getParameter("client_id");

                List<String> submittedScopes = new ArrayList<>();
                String[] scopeParams = request.getParameterValues("scope");
                if (scopeParams != null) {
                    for (String s : scopeParams) {
                        submittedScopes.addAll(Arrays.asList(s.split(" ")));
                    }
                }

                try {
                    AppResponse appResponse = appStub.getApplicationByClientId(
                            AppRequest.newBuilder().setClientId(clientId).build()
                    );

                    Map<String, Boolean> permissionsMap = appResponse.getPermissionsMap();

                    for (Map.Entry<String, Boolean> entry : permissionsMap.entrySet()) {
                        String scopeName = entry.getKey();
                        boolean isMandatory = entry.getValue();

                        if (isMandatory && !submittedScopes.contains(scopeName)) {
                            String state = request.getParameter("state");
                            response.sendRedirect("/oauth2/consent?client_id=" + clientId + "&state=" + state + "&error=missing_mandatory");
                            return;
                        }
                    }
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "gRPC error while validating mandatory scopes");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}