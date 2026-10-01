package sopra.steria.inventory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import sopra.steria.base.Base;
import sopra.steria.base.BaseRepository;
import sopra.steria.inventory.dto.CreateInventoryRequest;
import sopra.steria.inventory.dto.InventoryResponse;
import sopra.steria.kafka.event.SupplyEvent;
import sopra.steria.outbox.OutboxEvent;
import sopra.steria.outbox.OutboxEventRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class InventoryService {

    private final BaseRepository baseRepository;
    private final InventoryRepository inventoryRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public InventoryService(
            InventoryRepository inventoryRepository,
            BaseRepository baseRepository,
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper) {

        this.inventoryRepository = inventoryRepository;
        this.baseRepository = baseRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    public List<InventoryResponse> findByBaseId(UUID baseId) {
        if (baseRepository.findById(baseId) == null) {
            throw new NotFoundException();
        }

        return inventoryRepository.findByBaseId(baseId)
                .stream()
                .map(
                        this::toResponse
                ).toList();
    }

    @Transactional
    public InventoryResponse create(UUID baseId, CreateInventoryRequest request) {
        Base base = baseRepository.findById(baseId);

        if (base == null) {
            throw new NotFoundException();
        }

        Inventory inventory = new Inventory();
        inventory.setBase(base);
        inventory.setSupplyType(request.supplyType());
        inventory.setQuantity(request.quantity());

        inventoryRepository.persist(inventory);

        UUID eventId = UUID.randomUUID();

        SupplyEvent event = new SupplyEvent(
                eventId,
                inventory.getId(),
                base.getId(),
                inventory.getSupplyType(),
                inventory.getQuantity(),
                Instant.now()
        );

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setId(eventId);
        outboxEvent.setAggregateId(inventory.getId());
        outboxEvent.setEventType("SUPPLY_CREATED");
        outboxEvent.setPayload(serialize(event));
        outboxEvent.setCreatedAt(Instant.now());
        outboxEvent.setPublished(false);

        outboxEventRepository.persist(outboxEvent);

        return toResponse(inventory);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getBase().getId(),
                inventory.getSupplyType(),
                inventory.getQuantity()
        );
    }

    private String serialize(SupplyEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize supply event", e);
        }
    }
}
