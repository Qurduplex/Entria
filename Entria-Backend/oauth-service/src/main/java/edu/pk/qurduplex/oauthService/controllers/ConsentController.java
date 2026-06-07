package edu.pk.qurduplex.oauthService.controllers;

import edu.pk.qurduplex.common.grpc.*;
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

    @GrpcClient("userdata-service")
    private UserProfileGrpcServiceGrpc.UserProfileGrpcServiceBlockingStub userProfileStub;

    @GetMapping("/oauth2/consent")
    public String consent(Principal principal, Model model,
                          @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                          @RequestParam(name = OAuth2ParameterNames.SCOPE, required = false) String scope,
                          @RequestParam(OAuth2ParameterNames.STATE) String state) {

        String safeScope = (scope != null) ? scope : "";

        boolean requestOpenid = Arrays.asList(safeScope.split(" ")).contains("openid");
        model.addAttribute("requestOpenid", requestOpenid);

        List<String> scopesToApprove = Arrays.stream(safeScope.split(" "))
                .filter(s -> !s.trim().isEmpty() && !s.equals("openid"))
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
            Principal principal,
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

            if (principal != null && scope != null) {
                UserProfileResponse userProfile = userProfileStub.getUserProfile(
                        UserProfileRequest.newBuilder().setUserId(principal.getName()).build()
                );

                List<String> missingProfileData = new ArrayList<>();

                if (scope.contains("profile") && (isNullOrBlank(userProfile.getFirstName()) || isNullOrBlank(userProfile.getLastName()))) {
                    missingProfileData.add("First and last name");
                }
                if (scope.contains("phone") && isNullOrBlank(userProfile.getPhoneNumber())) {
                    missingProfileData.add("Phone number");
                }
                if (scope.contains("pesel") && isNullOrBlank(userProfile.getPesel())) {
                    missingProfileData.add("PESEL number");
                }
                if (scope.contains("sex") && isNullOrBlank(userProfile.getSex())) {
                    missingProfileData.add("Gender");
                }
                if (scope.contains("birthDate") && isNullOrBlank(userProfile.getBirthDate())) {
                    missingProfileData.add("Date of birth");
                }

                if (!missingProfileData.isEmpty()) {
                    String errorMsg = "You cannot share this data because it is missing from your profile. Please complete: " +
                            String.join(", ", missingProfileData);
                    return "redirect:/oauth2/error?error=missing_profile_data&state=" + state + "&message=" +
                            java.net.URLEncoder.encode(errorMsg, java.nio.charset.StandardCharsets.UTF_8);
                }
            }
        }

        return "forward:/oauth2/authorize";
    }

    private boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}