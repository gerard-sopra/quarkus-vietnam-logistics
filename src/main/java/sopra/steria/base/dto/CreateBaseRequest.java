package sopra.steria.base.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBaseRequest(

        @NotBlank
        String name,

        @NotBlank
        String location

) {
}