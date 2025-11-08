package thebitcrafter.autoreminder.domain.ports.out.repositories;

import thebitcrafter.autoreminder.domain.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository {
    Event save(Event event);
    Optional<Event> findById(UUID id);
    List<Event> findAll();
    boolean deleteById(UUID id);
}
