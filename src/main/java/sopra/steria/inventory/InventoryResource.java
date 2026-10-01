package sopra.steria.inventory;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sopra.steria.inventory.dto.CreateInventoryRequest;
import sopra.steria.inventory.dto.InventoryResponse;

import java.util.List;
import java.util.UUID;

@Path("/api/bases/{baseId}/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryResource {

    private final InventoryService inventoryService;

    public InventoryResource(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GET
    public List<InventoryResponse> getInventory(
            @PathParam("baseId") UUID baseId
    ) {
        return inventoryService.findByBaseId(baseId);
    }

    @POST
    public Response create(
            @PathParam("baseId") UUID baseId,
            @Valid CreateInventoryRequest request) {
        InventoryResponse response = inventoryService.create(baseId, request);

        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }
}
