package edu.pk.qurduplex.common.messages.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileMessage {
    private UUID userId;
    private String firstName;
    private String lastName;
}
