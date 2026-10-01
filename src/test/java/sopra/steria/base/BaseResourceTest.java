package sopra.steria.base;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class BaseResourceTest {

    @Test
    void shouldCreateBase() {
        given()
                .contentType("application/json")
                .body("""
                        {
                            "name": "FSB Hammer",
                            "location": "Pleiku"
                        }
                        """)
                .when()
                .post("/api/bases")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("FSB Hammer"))
                .body("location", equalTo("Pleiku"));
    }

    @Test
    void shouldGetBases() {
        given()
                .when()
                .get("/api/bases")
                .then()
                .statusCode(200)
                .body("$", is(notNullValue()));
    }

    @Test
    void shouldReturn404WhenBaseDoesNotExist() {
        UUID id = UUID.randomUUID();

        given()
                .when()
                .get("/api/bases/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldRejectInvalidBase() {
        given()
                .contentType("application/json")
                .body("""
                        {
                            "name": "",
                            "location": ""
                        }
                        """)
                .when()
                .post("/api/bases")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldGetCreatedBase() {

        String id =
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

        given()
                .when()
                .get("/api/bases/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Ben Het"))
                .body("location", equalTo("Kontum"));
    }
}