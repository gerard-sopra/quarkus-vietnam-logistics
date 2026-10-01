package sopra.steria.kafka.event;

import sopra.steria.inventory.SupplyType;

import java.time.Instant;
import java.util.UUID;

public record SupplyEvent(
        UUID eventId,
        UUID inventoryId,
        UUID baseId,
        SupplyType supplyType,
        Integer quantity,
        Instant timestamp
) {
}