package sopra.steria.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import sopra.steria.inventory.SupplyType;

public record CreateInventoryRequest(
        @NotNull
        SupplyType supplyType,

        @NotNull
        @PositiveOrZero
        Integer quantity
) {
}
