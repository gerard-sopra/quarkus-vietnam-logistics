package sopra.steria.base;

import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sopra.steria.base.dto.BaseResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    @Mock
    BaseRepository baseRepository;

    @Test
    void shouldFindBaseById() {

        UUID id = UUID.randomUUID();

        Base base = new Base();
        base.setId(id);
        base.setName("Ben Het");
        base.setLocation("Kontum");

        when(baseRepository.findById(id))
                .thenReturn(base);

        BaseService baseService =
                new BaseService(baseRepository);

        BaseResponse response =
                baseService.findById(id);

        assertEquals(id, response.id());
        assertEquals("Ben Het", response.name());
        assertEquals("Kontum", response.location());
    }

    @Test
    void shouldThrowNotFoundWhenBaseDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(baseRepository.findById(id))
                .thenReturn(null);

        BaseService baseService =
                new BaseService(baseRepository);

        assertThrows(
                NotFoundException.class,
                () -> baseService.findById(id)
        );
    }
}