package sopra.steria.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sopra.steria.base.Base;
import sopra.steria.base.BaseRepository;
import sopra.steria.inventory.dto.CreateInventoryRequest;
import sopra.steria.inventory.dto.InventoryResponse;
import sopra.steria.outbox.OutboxEvent;
import sopra.steria.outbox.OutboxEventRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    InventoryRepository inventoryRepository;

    @Mock
    BaseRepository baseRepository;

    @Mock
    OutboxEventRepository outboxEventRepository;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        inventoryService = new InventoryService(
                inventoryRepository,
                baseRepository,
                outboxEventRepository,
                objectMapper
        );
    }

    @Test
    void shouldCreateInventoryAndOutboxEvent() {
        UUID baseId = UUID.randomUUID();

        Base base = new Base();
        base.setId(baseId);
        base.setName("Ben Het");
        base.setLocation("Kontum");

        when(baseRepository.findById(baseId))
                .thenReturn(base);

        CreateInventoryRequest request =
                new CreateInventoryRequest(
                        SupplyType.AMMUNITION,
                        500
                );

        InventoryResponse response =
                inventoryService.create(baseId, request);

        assertEquals(baseId, response.baseId());
        assertEquals(SupplyType.AMMUNITION, response.supplyType());
        assertEquals(500, response.quantity());

        verify(inventoryRepository).persist(any(Inventory.class));
        verify(outboxEventRepository).persist(any(OutboxEvent.class));
    }
}