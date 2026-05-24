package edu.pk.qurduplex.appRegistryService.controllers;

import edu.pk.qurduplex.appRegistryService.dto.RegisterApplicationRequest;
import edu.pk.qurduplex.appRegistryService.dto.RegisterApplicationResponse;
import edu.pk.qurduplex.common.models.OAuthPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "Application Registry", description = "Endpoints for developers to manage applications and validate project status")
public class AppRegistryController {


    @PostMapping("/register-application")
    public RegisterApplicationResponse registerApplication(
            @Valid @RequestBody RegisterApplicationRequest request
    ) {





        return null;
    }
}
