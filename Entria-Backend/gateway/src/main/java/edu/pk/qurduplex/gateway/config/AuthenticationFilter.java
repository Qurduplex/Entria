package edu.pk.qurduplex.gateway.config;

import edu.pk.qurduplex.common.grpc.AuthServiceGrpc;
import edu.pk.qurduplex.common.grpc.ValidateTokenRequest;
import edu.pk.qurduplex.common.grpc.ValidateTokenResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @GrpcClient("identity-service")
    private AuthServiceGrpc.AuthServiceStub authServiceStub;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest secureRequest = stripInternalHeaders(exchange.getRequest());
            ServerWebExchange secureExchange = exchange.mutate().request(secureRequest).build();

            String token = extractBearerToken(secureRequest);

            if (token == null) {
                return chain.filter(secureExchange);
            }

            return validateTokenAsync(token)
                    .flatMap(response -> {
                        if (response.getIsValid()) {
                            ServerHttpRequest authorizedRequest = injectValidatedHeaders(secureRequest, response);
                            return chain.filter(secureExchange.mutate().request(authorizedRequest).build());
                        } else {
                            return rejectRequest(secureExchange);
                        }
                    })
                    .onErrorResume(e -> rejectRequest(secureExchange));
        };
    }

    private String extractBearerToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private ServerHttpRequest stripInternalHeaders(ServerHttpRequest request) {
        return request.mutate()
                .headers(headers -> {
                    headers.remove("X-User-Id");
                    headers.remove("X-User-Role");
                })
                .build();
    }

    private ServerHttpRequest injectValidatedHeaders(ServerHttpRequest request, ValidateTokenResponse response) {
        return request.mutate()
                .header("X-User-Id", response.getUserId())
                .header("X-User-Role", response.getRole())
                .build();
    }

    private Mono<Void> rejectRequest(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<ValidateTokenResponse> validateTokenAsync(String token) {
        return Mono.create(sink -> {
            ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                    .setToken(token)
                    .build();

            authServiceStub.validateToken(request, new StreamObserver<>() {
                @Override
                public void onNext(ValidateTokenResponse value) {
                    sink.success(value);
                }

                @Override
                public void onError(Throwable t) {
                    sink.error(t);
                }

                @Override
                public void onCompleted() {

                }
            });
        });
    }

    public static class Config {
    }
}