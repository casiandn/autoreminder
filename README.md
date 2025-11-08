# AutoReminder (core)

A lightweight Java scheduling & domain prototype for managing scheduled events — birthdays, anniversaries, reminders, meetings.

This project is designed with a Hexagonal (Ports & Adapters) architecture: the core domain is framework- and infra-agnostic so adapters (in-memory, logging, Kafka, JPA, etc.) can be added later.

Summary
- Domain models scheduled events with recurrence rules: `NONE`, `DAILY`, `WEEKLY`, `MONTHLY`, `YEARLY`.
- `ManageEventService` exposes CRUD + scheduling operations:
  - `createEvent`, `getEvent`, `listEvents`, `updateEvent`, `deleteEvent`
  - `nextOccurrence(UUID, ZonedDateTime)`
  - `findDueEvents(ZonedDateTime)`
  - `triggerDueEvents(ZonedDateTime)`
- Infrastructure adapters live under `infrastructure/` and implement ports from `domain.ports.out`.
- Lombok is used in the domain model to reduce boilerplate (`Event`).

What’s in this repository

Source layout (high level)
```
src/main/java/thebitcrafter/autoreminder/
  Main.java
  domain/
    model/
      Event.java
      EventType.java
      Recurrence.java
      EventConsumer.java
    ports/
      in/
        EventCrudPort.java
        ManageEventPort.java
      out/
        EventPublisher.java
        repositories/
          EventRepository.java
    service/
      ManageEventService.java
  infrastructure/
    memory/
      InMemoryEventRepository.java
    logging/
      LoggingEventPublisher.java

src/test/java/thebitcrafter/autoreminder/
  ManageEventServiceTest.java

pom.xml
README.md
```

Key implementation notes
- `Event` (domain model) contains scheduling logic: `nextTriggerAfter(ZonedDateTime)` returns the next scheduled ZonedDateTime according to the event's recurrence and zone.
- `ManageEventService` implements both CRUD (`EventCrudPort`) and scheduling operations (`ManageEventPort`) and is the main orchestrator.
- `InMemoryEventRepository` is a thread-safe, non-persistent ConcurrentHashMap-based repository used for quick local development and tests.
- `LoggingEventPublisher` prints published events to stdout. A future `KafkaEventPublisher` will implement `EventPublisher` to produce scheduled events to Kafka.
- Lombok is used for `Event` (builder, getters/setters, equals/hashCode, toString). Ensure Lombok is enabled in your IDE.

Requirements
- Java 21
- Maven
- Lombok (IDE support recommended)

Build & test

Run tests:

```bash
mvn -q test
```

Run the demo `Main` class (from an IDE or via command line):

```bash
# build classes
mvn -q -DskipTests=true compile
# run using the classpath
java -cp target/classes thebitcrafter.autoreminder.Main
```

(or run `Main` directly from your IDE)

Notes about Kafka (next step)
- A `KafkaEventPublisher` should implement `domain.ports.out.EventPublisher` and map scheduled events to a small JSON DTO containing: id, type, title, description, scheduledTime (ISO-8601), zone, recurrence, active.
- Use `org.apache.kafka:kafka-clients` + Jackson (`jackson-databind` + `jackson-datatype-jsr310`) for serialization.
- Topic: `events.scheduled` (or env-prefixed), key: `event.id`.
- Producer config: `acks=all`, `enable.idempotence=true`, `retries>0`, sensible `linger.ms`/`batch.size`.
- For integration tests, use Testcontainers Kafka.

Development tips
- Event date handling: the implementation accounts for month length and leap years (e.g., Feb 29 handling).
- One-off (`Recurrence.NONE`) events are deactivated after firing by `ManageEventService`.
- The repo is currently in-memory; add a JPA adapter when you need persistence.

Next steps (recommended)
1. Implement `KafkaEventPublisher` adapter and add Kafka & Jackson dependencies.
2. Add a JPA-backed `EventRepository` implementation (or Spring Data adapter).
3. Add a scheduled task (Quartz or Spring scheduler) that calls `triggerDueEvents()` periodically.
4. Add metrics (Micrometer) and structured logging.

Example — demo output
```
Next occurrence at: 2026-06-01T09:00:00+02:00[Europe/Paris]
[PUBLISH] Event{id=..., type=BIRTHDAY, title='Alice Birthday', recurrence=YEARLY, active=true} at 2026-06-01T09:00:00+02:00[Europe/Paris]
Triggered events: 1
```
