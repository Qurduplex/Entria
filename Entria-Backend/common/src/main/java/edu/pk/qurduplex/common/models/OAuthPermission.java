package edu.pk.qurduplex.common.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum OAuthPermission {

    OPENID("openid", "OpenID Connect access"),
    PROFILE("profile", "Basic profile info"),
    EMAIL("email", "Email address"),
    PHONE("phone", "Phone number"),
    PESEL("pesel", "PESEL number"),
    BIRTHDATE("birthdate", "Birth date"),
    GENDER("gender", "Gender"),
    PICTURE("picture", "Profile picture");

    private final String scope;
    private final String description;

    public static OAuthPermission fromScope(String scope) {
        return Arrays.stream(values())
                .filter(permission -> permission.getScope().equalsIgnoreCase(scope))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown scope: " + scope));
    }
}