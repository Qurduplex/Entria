package edu.pk.qurduplex.identityService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateVerificationCodeResponseDTO {
    private String email;
    private boolean success;
}
