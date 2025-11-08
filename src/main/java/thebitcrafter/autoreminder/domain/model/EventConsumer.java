package thebitcrafter.autoreminder.domain.model;

import java.time.ZonedDateTime;

@FunctionalInterface
public interface EventConsumer {
    void onEvent(Event event, ZonedDateTime scheduledTime);
}
