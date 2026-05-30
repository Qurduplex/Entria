package edu.pk.qurduplex.userDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pk.qurduplex.userDataService.repositories.UserProfileRepository;
import edu.pk.qurduplex.userDataService.services.JwtService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import edu.pk.qurduplex.userDataService.models.UserProfile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;
import edu.pk.qurduplex.userDataService.dto.UpdateUserProfileRequestDTO;
import edu.pk.qurduplex.common.messages.user.CreateProfileMessage;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class UserProfileFlowIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserProfileRepository userProfileRepository;
    @MockitoBean private JwtService jwtService;
    private UUID userId;
    @BeforeEach
    void setUp() {
        userProfileRepository.deleteAll();
        userId = UUID.randomUUID();
        userProfileRepository.save(UserProfile.builder()
                .userId(userId)
                .firstName("Jan")
                .lastName("Kowalski")
                .build());
    }
    @Test
    void getMe_returnsProfile() throws Exception {
        when(jwtService.extractUserId(anyString())).thenReturn(userId.toString());
        mockMvc.perform(get("/api/user-profile/me")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jan"));
    }

    @Test
    void updateUserProfile_returnsProfile() throws Exception {
        when(jwtService.extractUserId(anyString())).thenReturn(userId.toString());
        mockMvc.perform(put("/api/user-profile/me")
                .header("Authorization", "Bearer fake-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UpdateUserProfileRequestDTO.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"));
    }

    @Test
    void getMe_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/user-profile/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMe_profileNotFound_returns404() throws Exception {
        userProfileRepository.deleteAll();
        when(jwtService.extractUserId(anyString())).thenReturn(userId.toString());
        mockMvc.perform(get("/api/user-profile/me")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());
}

}
