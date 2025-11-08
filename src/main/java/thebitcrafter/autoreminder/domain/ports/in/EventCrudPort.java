package thebitcrafter.autoreminder.domain.ports.in;

import thebitcrafter.autoreminder.domain.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventCrudPort {
    //POST
    UUID createEvent(Event event);

    //GET
    Optional<Event> getEvent(UUID id);

    //GET
    List<Event> listEvents();

    //PUT
    void updateEvent(Event event);

    //DELETE
    boolean deleteEvent(UUID id);
}
