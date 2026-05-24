package edu.pk.qurduplex.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate()
                    .headers(headers -> {
                        headers.remove("X-User-Id");
                        headers.remove("X-User-Role");
                    });

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String developerId = extractUserIdFromToken(token);
                    String developerRole = extractUserRoleFromToken(token);

                    requestBuilder.header("X-User-Id", developerId);
                    requestBuilder.header("X-User-Role", developerRole);
                } catch (Exception e) {
                    // W razie błędu tokenu puszczamy żądanie bez nagłówków
                    System.out.println("Invalid token, proceeding without credentials");
                }
            }

            ServerHttpRequest modifiedRequest = requestBuilder.build();
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    public static class Config {
    }

    private String extractUserIdFromToken(String token) {
        return UUID.randomUUID().toString();
    }

    private String extractUserRoleFromToken(String token) {
        return "DEVELOPER";
    }
}