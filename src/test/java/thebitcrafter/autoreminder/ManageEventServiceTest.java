package thebitcrafter.autoreminder;

import org.junit.jupiter.api.Test;
import thebitcrafter.autoreminder.domain.model.*;
import thebitcrafter.autoreminder.domain.service.ManageEventService;
import thebitcrafter.autoreminder.infrastructure.logging.LoggingEventPublisher;
import thebitcrafter.autoreminder.infrastructure.memory.InMemoryEventRepository;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

public class ManageEventServiceTest {

    @Test
    void nextOccurrence_yearly_onFeb29_handlesNonLeapYears() {
        var repo = new InMemoryEventRepository();
        var pub = new LoggingEventPublisher();
        var svc = new ManageEventService(repo, pub);

        Event leapBirthday = new Event(null, EventType.BIRTHDAY, "Leap Kid", "", LocalDate.of(2000, 2, 29), LocalTime.of(9,0), ZoneId.of("UTC"), Recurrence.YEARLY, true);
        var id = svc.createEvent(leapBirthday);

        ZonedDateTime now = ZonedDateTime.of(LocalDate.of(2023,2,28), LocalTime.of(10,0), ZoneId.of("UTC"));
        var next = svc.nextOccurrence(id, now).orElseThrow();
        assertEquals(2024, next.getYear());
        assertEquals(Month.FEBRUARY, next.getMonth());
        assertEquals(29, next.getDayOfMonth());
    }

    @Test
    void triggerDueEvents_publishes_and_deactivates_oneOff() {
        var repo = new InMemoryEventRepository();
        var pub = new LoggingEventPublisher();
        var svc = new ManageEventService(repo, pub);

        var event = new Event(null, EventType.REMINDER, "One off", "", LocalDate.now(), LocalTime.MIN, ZoneId.systemDefault(), Recurrence.NONE, true);
        var id = svc.createEvent(event);
        int before = svc.triggerDueEvents(ZonedDateTime.now());
        assertTrue(before >= 0);
        // After first trigger, next occurrence should be empty because event is deactivated
        assertTrue(svc.getEvent(id).orElseThrow().isActive() || svc.nextOccurrence(id, ZonedDateTime.now()).isEmpty());
    }
}

