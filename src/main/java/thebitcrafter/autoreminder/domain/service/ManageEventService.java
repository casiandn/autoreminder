package thebitcrafter.autoreminder.domain.service;

import thebitcrafter.autoreminder.domain.model.Event;
import thebitcrafter.autoreminder.domain.ports.in.EventCrudPort;
import thebitcrafter.autoreminder.domain.ports.in.ManageEventPort;
import thebitcrafter.autoreminder.domain.ports.out.EventPublisher;
import thebitcrafter.autoreminder.domain.ports.out.repositories.EventRepository;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ManageEventService implements EventCrudPort, ManageEventPort {
    private final EventRepository eventRepository;
    private final EventPublisher eventPublisher;

    public ManageEventService(EventRepository eventRepository, EventPublisher eventPublisher) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    // CRUD
    @Override
    public UUID createEvent(Event event) {
        event.setId(UUID.randomUUID());
        eventRepository.save(event);
        return event.getId();
    }

    @Override
    public Optional<Event> getEvent(UUID id) { return eventRepository.findById(id); }

    @Override
    public List<Event> listEvents() { return eventRepository.findAll(); }

    @Override
    public void updateEvent(Event event) {
        if (eventRepository.findById(event.getId()).isEmpty()) {
            throw new NoSuchElementException("Event not found: " + event.getId());
        }
        eventRepository.save(event);
    }

    @Override
    public boolean deleteEvent(UUID id) { return eventRepository.deleteById(id); }

    @Override
    public Optional<ZonedDateTime> nextOccurrence(UUID eventId, ZonedDateTime now) {
        return eventRepository.findById(eventId).flatMap(e -> e.nextTriggerAfter(now));
    }

    @Override
    public List<Event> findDueEvents(ZonedDateTime now) {
        ZonedDateTime ref = now.minusSeconds(1);
        return eventRepository.findAll().stream()
                .filter(Event::isActive)
                .filter(e -> e.nextTriggerAfter(ref).map(t -> !t.isAfter(now)).orElse(false))
                .collect(Collectors.toList());
    }

    @Override
    public int triggerDueEvents(ZonedDateTime now) {
        List<Event> due = findDueEvents(now);
        for (Event e : due) {
            ZonedDateTime scheduled = e.nextTriggerAfter(now.minusSeconds(1)).orElse(now);
            eventPublisher.publish(e, scheduled);
            // If non-recurring, deactivate after firing
            if (e.nextTriggerAfter(scheduled).isEmpty()) {
                e.setActive(false);
                eventRepository.save(e);
            }
        }
        return due.size();
    }
}
