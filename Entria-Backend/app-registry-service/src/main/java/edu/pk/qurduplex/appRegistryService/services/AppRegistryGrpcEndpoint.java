package edu.pk.qurduplex.appRegistryService.services;

import edu.pk.qurduplex.appRegistryService.models.DeveloperApplication;
import edu.pk.qurduplex.appRegistryService.repositories.DeveloperApplicationRepository;
import edu.pk.qurduplex.common.grpc.AppRegistryGrpcServiceGrpc;
import edu.pk.qurduplex.common.grpc.AppRequest;
import edu.pk.qurduplex.common.grpc.AppResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class AppRegistryGrpcEndpoint extends AppRegistryGrpcServiceGrpc.AppRegistryGrpcServiceImplBase {

    private final DeveloperApplicationRepository repository;

    @Override
    @Transactional(readOnly = true)
    public void getApplicationByClientId(AppRequest request, StreamObserver<AppResponse> responseObserver) {
        Optional<DeveloperApplication> appOpt = repository.findByClientId(request.getClientId());

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
                    .withDescription("Application not found for clientId: " + request.getClientId())
                    .asRuntimeException());
        }
    }
}
