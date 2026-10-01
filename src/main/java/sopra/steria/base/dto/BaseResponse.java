package sopra.steria.base.dto;

import java.util.UUID;

public record BaseResponse(
        UUID id,
        String name,
        String location
) {
}
