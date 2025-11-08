package thebitcrafter.autoreminder;

import thebitcrafter.autoreminder.domain.model.*;
import thebitcrafter.autoreminder.domain.ports.in.EventCrudPort;
import thebitcrafter.autoreminder.domain.ports.in.ManageEventPort;
import thebitcrafter.autoreminder.domain.service.ManageEventService;
import thebitcrafter.autoreminder.infrastructure.logging.LoggingEventPublisher;
import thebitcrafter.autoreminder.infrastructure.memory.InMemoryEventRepository;

import java.time.*;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        var repository = new InMemoryEventRepository();
        var publisher = new LoggingEventPublisher();
        var service = new ManageEventService(repository, publisher);

        EventCrudPort crud = service;
        ManageEventPort manage = service;

        Event birthday = new Event(
                null,
                EventType.BIRTHDAY,
                "Alice Birthday",
                "Celebrate Alice's birthday",
                LocalDate.of(1990, 6, 1),
                LocalTime.of(9, 0),
                ZoneId.systemDefault(),
                Recurrence.YEARLY,
                true
        );
        UUID id = crud.createEvent(birthday);

        var now = ZonedDateTime.now();
        manage.nextOccurrence(id, now).ifPresent(next ->
                System.out.println("Next occurrence at: " + next));

        int triggered = manage.triggerDueEvents(now);
        System.out.println("Triggered events: " + triggered);
    }
}