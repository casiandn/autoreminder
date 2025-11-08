package thebitcrafter.autoreminder.domain.ports.in;

import thebitcrafter.autoreminder.domain.model.Event;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManageEventPort {
    Optional<ZonedDateTime> nextOccurrence(UUID eventId, ZonedDateTime now);
    List<Event> findDueEvents(ZonedDateTime now);
    int triggerDueEvents(ZonedDateTime now);
}
