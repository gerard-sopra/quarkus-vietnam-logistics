package sopra.steria.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import sopra.steria.kafka.SupplyEventProducer;
import sopra.steria.kafka.event.SupplyEvent;

import java.util.List;

@ApplicationScoped
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final SupplyEventProducer supplyEventProducer;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            SupplyEventProducer supplyEventProducer,
            ObjectMapper objectMapper) {

        this.outboxEventRepository = outboxEventRepository;
        this.supplyEventProducer = supplyEventProducer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(every = "5s")
    @Transactional
    public void publish() {

        List<OutboxEvent> events = outboxEventRepository.findUnpublishedForUpdate(50);

        for (OutboxEvent event : events) {

            SupplyEvent supplyEvent =
                    deserialize(event.getPayload());

            supplyEventProducer
                    .send(supplyEvent)
                    .toCompletableFuture()
                    .join();
            ;

            event.setPublished(true);
        }
    }

    private SupplyEvent deserialize(String payload) {
        try {
            return objectMapper.readValue(
                    payload,
                    SupplyEvent.class
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not deserialize outbox event", e);
        }
    }
}
