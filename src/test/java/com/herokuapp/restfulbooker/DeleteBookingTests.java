package com.herokuapp.restfulbooker;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

public class DeleteBookingTests extends CreateBookingFixture {

    @Test
    public void testDeleteValidBookingWithAbsentAuthorization() {
        given()
                .contentType(JSON)
                .when()
                .delete("/booking/{id}", bookingId)
                .then()
                .statusCode(403)
                .body(equalTo("Forbidden"));
    }

    @Test
    public void testDeleteValidBookingWithCookie() {
        checkPresenceOfBooking();

        //DELETE booking
        given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/{id}", bookingId)
                .then()
                .statusCode(201)
                .body(equalTo("Created"));

        checkAbsenceOfBooking();

        //Check correct behaviour after repeated call of delete
        given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/{id}", bookingId)
                .then()
                .statusCode(405)
                .body(equalTo("Method Not Allowed"));
    }

    @Test
    public void testDeleteValidBookingWithAbsentId() {
        given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/8000008")
                .then()
                .statusCode(405)
                .body(equalTo("Method Not Allowed"));
    }

    @Test
    public void testDeleteValidBookingWithBasicAuth() {
        createBooking();
        checkPresenceOfBooking();

        //DELETE booking
        given()
                .contentType(JSON)
                .header("Authorization", AUTH_HEADER)
                .when()
                .delete("/booking/{id}", bookingId)
                .then()
                .statusCode(201)
                .body(equalTo("Created"));

        checkAbsenceOfBooking();

        //Check correct behaviour after repeated call of delete
        given()
                .contentType(JSON)
                .header("Authorization", AUTH_HEADER)
                .when()
                .delete("/booking/{id}", bookingId)
                .then()
                .statusCode(405)
                .body(equalTo("Method Not Allowed"));
    }

    private void checkPresenceOfBooking(){
        //Check that "record" of created booking is available
        given()
                .contentType(JSON)
                .when()
                .get("/booking/{id}", bookingId)
                .then()
                .statusCode(200);

        //Check that id of created booking is present in list of all ids
        given()
                .contentType(JSON)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("bookingid.", hasItem(bookingId));
    }

    private void checkAbsenceOfBooking(){

        //Check that "record" of created booking is absent
        given()
                .contentType(JSON)
                .when()
                .get("/booking/{id}", bookingId)
                .then()
                .statusCode(404)
                .body(equalTo("Not Found"));

        //Check that id of created booking is present in list of all ids
        given()
                .contentType(JSON)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("bookingid.", not(hasItem(bookingId)));
    }
}