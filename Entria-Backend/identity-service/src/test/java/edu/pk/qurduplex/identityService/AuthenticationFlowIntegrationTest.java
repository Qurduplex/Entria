package edu.pk.qurduplex.identityService;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pk.qurduplex.identityService.dto.*;
import edu.pk.qurduplex.identityService.models.AuthCredential;
import edu.pk.qurduplex.identityService.models.RefreshToken;
import edu.pk.qurduplex.identityService.models.ResetPasswordCode;
import edu.pk.qurduplex.identityService.models.VerificationCode;
import edu.pk.qurduplex.identityService.repositories.AuthRepository;
import edu.pk.qurduplex.identityService.repositories.RefreshTokenRepository;
import edu.pk.qurduplex.identityService.repositories.ResetPasswordCodeRepository;
import edu.pk.qurduplex.identityService.repositories.VerificationCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthRepository authRepository;

    @MockitoBean
    private VerificationCodeRepository verificationCodeRepo;

    @MockitoBean
    private ResetPasswordCodeRepository resetPasswordCodeRepo;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepo;

    private Map<UUID, VerificationCode> verificationCodeStore;
    private Map<UUID, ResetPasswordCode> resetPasswordCodeStore;
    private Map<UUID, RefreshToken> refreshTokenStore;

    @BeforeEach
    void setUp() {
        authRepository.deleteAll();

        verificationCodeStore = new ConcurrentHashMap<>();
        resetPasswordCodeStore = new ConcurrentHashMap<>();
        refreshTokenStore = new ConcurrentHashMap<>();

        setupRedisMocks();
    }

    @Test
    @DisplayName("User Journey: Register -> Verify -> Login -> Refresh Token -> Reset Password -> Logout")
    void fullAuthenticationLifecycle() throws Exception {
        final String TEST_EMAIL = "integration.test@example.com";
        final String TEST_PASSWORD = "SuperSecretPassword123!";
        final String NEW_PASSWORD = "EvenMoreSecretPassword456!";

        // 1. USER REGISTRATION
        RegisterRequestDTO registerRequest = new RegisterRequestDTO(TEST_EMAIL, TEST_PASSWORD);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        AuthCredential user = authRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assertThat(user.isActive()).isFalse();

        // 2. LOGIN TRY (SHOULD FAIL - ACCOUNT NOT VERIFIED)
        LoginRequestDTO loginRequest = new LoginRequestDTO(TEST_EMAIL, TEST_PASSWORD);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()) // Throws 401 because account is not active
                .andExpect(jsonPath("$.message").value("Account with email " + TEST_EMAIL + " is not verified"));


        // 3. ACCOUNT VERIFICATION
        VerificationCode vCode = verificationCodeStore.get(user.getId());
        assertThat(vCode).isNotNull();

        AccountVerificationRequestDTO verifyRequest = new AccountVerificationRequestDTO(TEST_EMAIL, vCode.getCode());
        mockMvc.perform(post("/api/auth/verification-code/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test that the user is now active
        user = authRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assertThat(user.isActive()).isTrue();

        // 4. LOGIN COMPLETED SUCCESSFULLY
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseDTO loginResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), LoginResponseDTO.class);
        assertThat(loginResponse.getJwtToken()).isNotBlank();
        assertThat(loginResponse.getRefreshToken()).isNotBlank();

        String jwtToken = loginResponse.getJwtToken();
        UUID refreshToken = UUID.fromString(loginResponse.getRefreshToken());

        assertThat(refreshTokenStore.containsKey(refreshToken)).isTrue();

        // 5. REFRESH TOKEN
        Thread.sleep(1000); // Adding a small delay to ensure the new token will have a different timestamp

        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO(refreshToken);
        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andReturn();

        TokenDTO tokenDTO = objectMapper.readValue(refreshResult.getResponse().getContentAsString(), TokenDTO.class);
        assertThat(tokenDTO.getJwtToken()).isNotBlank();
        assertThat(tokenDTO.getJwtToken()).isNotEqualTo(jwtToken);

        String newJwtToken = tokenDTO.getJwtToken();

        // 6. LOGOUT FROM ALL DEVICES
        mockMvc.perform(post("/api/auth/logout/all")
                        .header("Authorization", "Bearer " + newJwtToken))
                .andExpect(status().isOk());

        assertThat(refreshTokenStore).isEmpty();

        // 7. REQUEST RESET PASSWORD CODE
        GenerateResetPasswordCodeRequestDTO resetRequest = new GenerateResetPasswordCodeRequestDTO(TEST_EMAIL);
        mockMvc.perform(post("/api/auth/reset-password-code/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isOk());

        // 8. RESET PASSWORD USING THE CODE
        ResetPasswordCode rCode = resetPasswordCodeStore.get(user.getId());
        assertThat(rCode).isNotNull();

        ResetPasswordRequestDTO doResetRequest = new ResetPasswordRequestDTO(TEST_EMAIL, rCode.getCode(), NEW_PASSWORD);
        mockMvc.perform(post("/api/auth/reset-password-code/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doResetRequest)))
                .andExpect(status().isOk());

        // 9. LOGIN WITH NEW PASSWORD
        LoginRequestDTO newLoginRequest = new LoginRequestDTO(TEST_EMAIL, NEW_PASSWORD);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLoginRequest)))
                .andExpect(status().isOk());
    }

    // It simulates the behavior of Redis repositories by using in-memory ConcurrentHashMaps to store entities.
    private void setupRedisMocks() {
        // --- VerificationCode Mocks ---
        when(verificationCodeRepo.existsById(any())).thenAnswer(i -> verificationCodeStore.containsKey((UUID) i.getArgument(0)));
        when(verificationCodeRepo.save(any())).thenAnswer(i -> {
            VerificationCode code = i.getArgument(0);
            verificationCodeStore.put(code.getId(), code);
            return code;
        });
        when(verificationCodeRepo.findById(any())).thenAnswer(i -> Optional.ofNullable(verificationCodeStore.get((UUID) i.getArgument(0))));
        doAnswer(i -> {
            VerificationCode code = i.getArgument(0);
            verificationCodeStore.remove(code.getId());
            return null;
        }).when(verificationCodeRepo).delete(any());
        doAnswer(i -> {
            verificationCodeStore.remove((UUID) i.getArgument(0));
            return null;
        }).when(verificationCodeRepo).deleteById(any());

        // --- ResetPasswordCode Mocks ---
        when(resetPasswordCodeRepo.existsById(any())).thenAnswer(i -> resetPasswordCodeStore.containsKey((UUID) i.getArgument(0)));
        when(resetPasswordCodeRepo.save(any())).thenAnswer(i -> {
            ResetPasswordCode code = i.getArgument(0);
            resetPasswordCodeStore.put(code.getId(), code);
            return code;
        });
        when(resetPasswordCodeRepo.findById(any())).thenAnswer(i -> Optional.ofNullable(resetPasswordCodeStore.get((UUID) i.getArgument(0))));
        doAnswer(i -> {
            ResetPasswordCode code = i.getArgument(0);
            resetPasswordCodeStore.remove(code.getId());
            return null;
        }).when(resetPasswordCodeRepo).delete(any());
        doAnswer(i -> {
            resetPasswordCodeStore.remove((UUID) i.getArgument(0));
            return null;
        }).when(resetPasswordCodeRepo).deleteById(any());

        // --- RefreshToken Mocks ---
        when(refreshTokenRepo.save(any())).thenAnswer(i -> {
            RefreshToken token = i.getArgument(0);
            refreshTokenStore.put(token.getToken(), token);
            return token;
        });
        when(refreshTokenRepo.findById(any())).thenAnswer(i -> Optional.ofNullable(refreshTokenStore.get((UUID) i.getArgument(0))));
        doAnswer(i -> {
            refreshTokenStore.remove((UUID) i.getArgument(0));
            return null;
        }).when(refreshTokenRepo).deleteById(any());
        doAnswer(i -> {
            UUID userId = i.getArgument(0);
            refreshTokenStore.values().removeIf(token -> token.getUserId().equals(userId));
            return null;
        }).when(refreshTokenRepo).deleteByUserId(any());
    }
}