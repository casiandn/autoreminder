package thebitcrafter.autoreminder.infrastructure.memory;

import thebitcrafter.autoreminder.domain.model.Event;
import thebitcrafter.autoreminder.domain.ports.out.repositories.EventRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEventRepository implements EventRepository {
    private final Map<UUID, Event> store = new ConcurrentHashMap<>();

    @Override
    public Event save(Event event) {
        store.put(event.getId(), event);
        return event;
    }

    @Override
    public Optional<Event> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Event> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean deleteById(UUID id) {
        return store.remove(id) != null;
    }
}

