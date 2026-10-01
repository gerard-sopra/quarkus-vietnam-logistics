package sopra.steria.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import jakarta.enterprise.context.ApplicationScoped;
import sopra.steria.kafka.event.SupplyEvent;

import java.io.IOException;

@ApplicationScoped
public class ElasticsearchService {

    private final ElasticsearchClient client;

    public ElasticsearchService(ElasticsearchClient client) {
        this.client = client;
    }

    public void index(SupplyEvent event) {
        try {
            IndexResponse response = client.index(i -> i
                    .index("logistics-events")
                    .id(event.eventId().toString())
                    .document(event)
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not index supply event in Elasticsearch",
                    e
            );
        }
    }
}