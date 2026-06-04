package edu.pk.qurduplex.oauthService.controllers;

import edu.pk.qurduplex.common.grpc.AppRegistryGrpcServiceGrpc;
import edu.pk.qurduplex.common.grpc.AppRequest;
import edu.pk.qurduplex.common.grpc.AppResponse;
import jakarta.servlet.http.HttpServletRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ConsentController {

    @GrpcClient("app-registry-service")
    private AppRegistryGrpcServiceGrpc.AppRegistryGrpcServiceBlockingStub appStub;

    @GetMapping("/oauth2/consent")
    public String consent(Principal principal, Model model,
                          @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                          @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                          @RequestParam(OAuth2ParameterNames.STATE) String state) {

        List<String> scopesToApprove = Arrays.stream(scope.split(" "))
                .filter(s -> !s.equals("openid"))
                .toList();

        AppResponse appResponse = appStub.getApplicationByClientId(
                AppRequest.newBuilder().setClientId(clientId).build()
        );
        Map<String, Boolean> permissionsMap = appResponse.getPermissionsMap();

        Map<String, Boolean> scopesMandatoryMap = new LinkedHashMap<>();
        for (String scopeName : scopesToApprove) {
            scopesMandatoryMap.put(scopeName, permissionsMap.getOrDefault(scopeName, false));
        }

        model.addAttribute("clientId", clientId);
        model.addAttribute("state", state);
        model.addAttribute("principalName", principal.getName());
        model.addAttribute("scopes", scopesMandatoryMap);
        return "consent";
    }

    @PostMapping("/oauth2/consent")
    public String handleConsent(
            HttpServletRequest request,
            @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
            @RequestParam(required = false) List<String> scope,
            @RequestParam String state,
            @RequestParam String consent_action) {

        if ("cancel".equals(consent_action)) {
            return "redirect:/oauth2/error?error=access_denied&state=" + state;
        }

        if ("approve".equals(consent_action)) {
            AppResponse appResponse = appStub.getApplicationByClientId(
                    AppRequest.newBuilder().setClientId(clientId).build()
            );
            Map<String, Boolean> permissionsMap = appResponse.getPermissionsMap();

            List<String> missingMandatory = new ArrayList<>();
            for (Map.Entry<String, Boolean> entry : permissionsMap.entrySet()) {
                if (entry.getValue() && (scope == null || !scope.contains(entry.getKey()))) {
                    missingMandatory.add(entry.getKey());
                }
            }

            if (!missingMandatory.isEmpty()) {
                String errorMsg = "Missing obligatory permissions: " + String.join(", ", missingMandatory);
                return "redirect:/oauth2/error?error=missing_mandatory&state=" + state + "&message=" +
                        java.net.URLEncoder.encode(errorMsg, java.nio.charset.StandardCharsets.UTF_8);
            }

            List<String> finalScopes = new ArrayList<>(scope != null ? scope : new ArrayList<>());
            finalScopes.add("openid");

            request.setAttribute("scope", finalScopes);
        }

        return "forward:/oauth2/authorize";
    }
}