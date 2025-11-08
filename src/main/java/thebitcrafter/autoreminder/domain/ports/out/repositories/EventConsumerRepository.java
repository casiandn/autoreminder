package thebitcrafter.autoreminder.domain.ports.out.repositories;

import thebitcrafter.autoreminder.domain.model.EventConsumer;

public interface EventConsumerRepository {
    // sigue JPA
    EventConsumer findById(long id);
    EventConsumer findAll();
    EventConsumer create(EventConsumer eventConsumer);
    EventConsumer update(EventConsumer eventConsumer);
    void delete(EventConsumer eventConsumer);
}
