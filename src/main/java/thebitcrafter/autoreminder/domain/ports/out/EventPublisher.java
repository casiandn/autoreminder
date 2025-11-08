package thebitcrafter.autoreminder.domain.ports.out;

import thebitcrafter.autoreminder.domain.model.Event;

import java.time.ZonedDateTime;

public interface EventPublisher {
    // Publish a single scheduled event occurrence
    void publish(Event event, ZonedDateTime scheduledTime);
}
