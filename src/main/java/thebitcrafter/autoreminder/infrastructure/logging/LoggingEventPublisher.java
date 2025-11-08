package thebitcrafter.autoreminder.infrastructure.logging;

import thebitcrafter.autoreminder.domain.model.Event;
import thebitcrafter.autoreminder.domain.ports.out.EventPublisher;

import java.time.ZonedDateTime;

public class LoggingEventPublisher implements EventPublisher {
    @Override
    public void publish(Event event, ZonedDateTime scheduledTime) {
        System.out.printf("[PUBLISH] %s at %s%n", event, scheduledTime);
    }
}

