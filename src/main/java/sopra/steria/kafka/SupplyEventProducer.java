package sopra.steria.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import sopra.steria.kafka.event.SupplyEvent;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class SupplyEventProducer {

    private final Emitter<SupplyEvent> emitter;

    public SupplyEventProducer(@Channel("supply-events") Emitter<SupplyEvent> emitter) {
        this.emitter = emitter;
    }

    public CompletionStage<Void> send(SupplyEvent event) {
        return emitter.send(event);
    }
}
