package edu.pk.qurduplex.userDataService.consumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import edu.pk.qurduplex.common.messages.user.CreateProfileMessage;

import edu.pk.qurduplex.userDataService.services.UserProfileService;
import edu.pk.qurduplex.userDataService.models.UserProfile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegisterEventConsumer {
    private final UserProfileService userProfileService;

    @RabbitListener(queues = "#{rabbitMQProperties.getUserRegisteredEventTopic()}")
    public void processUserRegisterEvent(CreateProfileMessage message) {
        log.info("Received user register event: {}", message);
        UserProfile userProfile = userProfileService.createFromRegistrationEvent(message);
        if (userProfile == null) {
            log.warn("Failed to create user profile for user id: {}", message.getUserId());
            return;
        }
        log.info("User registration event processed successfully for user id: {}", message.getUserId());
    }
}
