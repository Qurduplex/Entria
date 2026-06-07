package edu.pk.qurduplex.appRegistryService.services;

import edu.pk.qurduplex.appRegistryService.models.DeveloperApplication;
import edu.pk.qurduplex.appRegistryService.repositories.DeveloperApplicationRepository;
import edu.pk.qurduplex.common.grpc.AppRegistryGrpcServiceGrpc;
import edu.pk.qurduplex.common.grpc.AppRequest;
import edu.pk.qurduplex.common.grpc.AppIdRequest; // Wygenerowana klasa
import edu.pk.qurduplex.common.grpc.AppResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class AppRegistryGrpcEndpoint extends AppRegistryGrpcServiceGrpc.AppRegistryGrpcServiceImplBase {

    private final DeveloperApplicationRepository repository;

    @Override
    @Transactional(readOnly = true)
    public void getApplicationByClientId(AppRequest request, StreamObserver<AppResponse> responseObserver) {
        handleApplicationResponse(repository.findByClientId(request.getClientId()),
                "Application not found for clientId: " + request.getClientId(), responseObserver);
    }

    @Override
    @Transactional(readOnly = true)
    public void getApplicationById(AppIdRequest request, StreamObserver<AppResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            handleApplicationResponse(repository.findById(id),
                    "Application not found for id: " + request.getId(), responseObserver);
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid UUID format: " + request.getId())
                    .asRuntimeException());
        }
    }

    private void handleApplicationResponse(Optional<DeveloperApplication> appOpt, String notFoundMsg, StreamObserver<AppResponse> responseObserver) {
        if (appOpt.isPresent()) {
            DeveloperApplication app = appOpt.get();

            Map<String, Boolean> grpcPermissions = app.getPermissions().entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().name().toLowerCase(),
                            Map.Entry::getValue
                    ));

            AppResponse response = AppResponse.newBuilder()
                    .setId(app.getId().toString())
                    .setClientId(app.getClientId())
                    .setClientSecretHash(app.getClientSecretHash())
                    .setRedirectUri(app.getRedirectUri())
                    .putAllPermissions(grpcPermissions)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(notFoundMsg)
                    .asRuntimeException());
        }
    }
}