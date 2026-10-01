package sopra.steria.base;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sopra.steria.base.dto.BaseResponse;
import sopra.steria.base.dto.CreateBaseRequest;

import java.util.List;
import java.util.UUID;

@Path("/api/bases")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BaseResource {

    private final BaseService baseService;

    public BaseResource(BaseService baseService) {
        this.baseService = baseService;
    }

    @GET
    public List<BaseResponse> getBases() {
        return baseService.findAll();
    }

    @GET
    @Path("/{id}")
    public BaseResponse getBase(@PathParam("id") UUID id) {
        return baseService.findById(id);
    }

    @POST
    public Response create(@Valid CreateBaseRequest request) {
        BaseResponse response = baseService.create(request);

        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }
}
