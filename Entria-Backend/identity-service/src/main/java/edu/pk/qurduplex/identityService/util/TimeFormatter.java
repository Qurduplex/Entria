package edu.pk.qurduplex.identityService.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class TimeFormatter {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String formatInstant(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                .format(FORMATTER);
    }

    public Instant now() {
        return Instant.now();
    }

    public Instant plusMillis(Instant instant, long millis) {
        return instant.plusMillis(millis);
    }
}
