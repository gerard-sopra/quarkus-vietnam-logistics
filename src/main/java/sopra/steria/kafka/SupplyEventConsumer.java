package sopra.steria.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import sopra.steria.elasticsearch.ElasticsearchService;
import sopra.steria.kafka.event.SupplyEvent;

import java.time.Instant;

@ApplicationScoped
public class SupplyEventConsumer {

    private static final Logger LOG =
            Logger.getLogger(SupplyEventConsumer.class);

    private final ProcessedEventRepository processedEventRepository;
    private final ElasticsearchService elasticsearchService;

    public SupplyEventConsumer(
            ProcessedEventRepository processedEventRepository,
            ElasticsearchService elasticsearchService) {
        this.processedEventRepository = processedEventRepository;
        this.elasticsearchService = elasticsearchService;
    }

    @Incoming("supply-events-in")
    @Transactional
    public void consume(SupplyEvent event) {

        if (processedEventRepository.findById(event.eventId()) != null) {
            LOG.infof(
                    "Ignoring duplicate event %s",
                    event.eventId()
            );

            return;
        }

        LOG.infof(
                "Processing supply event %s: inventory=%s, base=%s, type=%s, quantity=%d",
                event.eventId(),
                event.inventoryId(),
                event.baseId(),
                event.supplyType(),
                event.quantity()
        );

        elasticsearchService.index(event);

        ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setEventId(event.eventId());
        processedEvent.setProcessedAt(Instant.now());

        processedEventRepository.persist(processedEvent);
    }
}