package sopra.steria.inventory.dto;

import sopra.steria.inventory.SupplyType;

import java.util.UUID;

public record InventoryResponse(
        UUID id,
        UUID baseId,
        SupplyType supplyType,
        Integer quantity
) {
}
