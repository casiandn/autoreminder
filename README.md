A small scheduling/core domain prototype for scheduled events (birthdays, anniversaries, reminders, meetings).
Designed with a hexagonal (ports & adapters) architecture so that persistence, messaging (Kafka), and other infrastructure can be introduced later with minimal coupling.
Goals
Model scheduled events with recurrence rules (none, daily, weekly, monthly, yearly).
Provide a service (ManageEventService) that exposes CRUD operations and scheduling operations (next occurrence, find due events, trigger due events).
Keep infrastructure isolated behind ports so adapters (in-memory, logging, Kafka, JPA) can be swapped in/out.
Provide a clean base for implementing Kafka publishing and JPA repositories in the next steps.
