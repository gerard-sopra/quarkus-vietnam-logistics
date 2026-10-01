package sopra.steria.outbox;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OutboxEventRepository implements PanacheRepositoryBase<OutboxEvent, UUID> {

    private final EntityManager entityManager;

    public OutboxEventRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<OutboxEvent> findUnpublishedForUpdate(int limit) {
        return entityManager
                .createNativeQuery("""
                        SELECT *
                        FROM outbox_events
                        WHERE published = false
                        ORDER BY created_at
                        FOR UPDATE SKIP LOCKED
                        LIMIT :limit
                        """, OutboxEvent.class)
                .setParameter("limit", limit)
                .getResultList();
    }

    public OutboxEvent findByAggregateId(UUID aggregateId) {
        return find("aggregateId", aggregateId)
                .firstResult();
    }
}
