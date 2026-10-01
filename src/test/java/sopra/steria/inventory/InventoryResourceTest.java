package sopra.steria.inventory;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import sopra.steria.outbox.OutboxEvent;
import sopra.steria.outbox.OutboxEventRepository;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class InventoryResourceTest {

    @Inject
    OutboxEventRepository outboxEventRepository;

    @Test
    void shouldCreateInventory() {

        String baseId =
                given()
                        .contentType("application/json")
                        .body("""
                                {
                                    "name": "Ben Het",
                                    "location": "Kontum"
                                }
                                """)
                        .when()
                        .post("/api/bases")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        String inventoryId =
                given()
                        .contentType("application/json")
                        .body("""
                                {
                                    "supplyType": "AMMUNITION",
                                    "quantity": 500
                                }
                                """)
                        .when()
                        .post("/api/bases/" + baseId + "/inventory")
                        .then()
                        .statusCode(201)
                        .body("id", notNullValue())
                        .body("baseId", equalTo(baseId))
                        .body("supplyType", equalTo("AMMUNITION"))
                        .body("quantity", equalTo(500))
                        .extract()
                        .path("id");

        OutboxEvent outboxEvent =
                outboxEventRepository.findByAggregateId(
                        UUID.fromString(inventoryId)
                );

        assertNotNull(outboxEvent);
        assertEquals(
                UUID.fromString(inventoryId),
                outboxEvent.getAggregateId()
        );
        assertEquals(
                "SUPPLY_CREATED",
                outboxEvent.getEventType()
        );
        assertFalse(outboxEvent.isPublished());
        assertNotNull(outboxEvent.getPayload());
        assertNotNull(outboxEvent.getCreatedAt());
    }

    @Test
    void shouldGetInventoryForBase() {

        String baseId =
                given()
                        .contentType("application/json")
                        .body("""
                                {
                                    "name": "FSB Cobra",
                                    "location": "Dak To"
                                }
                                """)
                        .when()
                        .post("/api/bases")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        given()
                .contentType("application/json")
                .body("""
                        {
                            "supplyType": "MEDICAL",
                            "quantity": 75
                        }
                        """)
                .when()
                .post("/api/bases/" + baseId + "/inventory")
                .then()
                .statusCode(201);

        given()
                .when()
                .get("/api/bases/" + baseId + "/inventory")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].baseId", equalTo(baseId))
                .body("[0].supplyType", equalTo("MEDICAL"))
                .body("[0].quantity", equalTo(75));
    }

    @Test
    void shouldRejectNegativeQuantity() {

        String baseId =
                given()
                        .contentType("application/json")
                        .body("""
                                {
                                    "name": "MACV-SOG FOB",
                                    "location": "Da Nang"
                                }
                                """)
                        .when()
                        .post("/api/bases")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        given()
                .contentType("application/json")
                .body("""
                        {
                            "supplyType": "FOOD",
                            "quantity": -10
                        }
                        """)
                .when()
                .post("/api/bases/" + baseId + "/inventory")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn404WhenCreatingInventoryForUnknownBase() {

        String unknownBaseId =
                java.util.UUID.randomUUID().toString();

        given()
                .contentType("application/json")
                .body("""
                        {
                            "supplyType": "FUEL",
                            "quantity": 100
                        }
                        """)
                .when()
                .post("/api/bases/" + unknownBaseId + "/inventory")
                .then()
                .statusCode(404);
    }
}