package com.herokuapp.restfulbooker;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

public class GetBookingByIdsTests extends CreateBookingFixture {

    @Test
    public void testGetAllByIds() {
        given()
                .contentType(JSON)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("bookingid.", hasItem(bookingId));
    }

    @Test
    public void testGetAllByIdsAndName() {
        String firstName = booking.getBooking().getFirstName();
        String lastName = booking.getBooking().getLastName();

        given()
                .contentType(JSON)
                .param("firstname", firstName)
                .param("lastname", lastName)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("bookingid.", hasItem(bookingId));
    }

    @Test
    public void testGetAllByIdsAndOtherName() {
        given()
                .contentType(JSON)
                .param("firstname", "Jim")
                .param("lastname", "Brown")
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("bookingid.", not(hasItem(bookingId)));
    }

    @Test
    public void testGetAllByIdsAndInvalidName() {
        given()
                .contentType(JSON)
                .param("firstname", "Jim")
                .param("lastname", "Brown1")
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

    //This test is failed because parameter "checkin" doesn't include passed boundary value
    // It means that if we send checkin=2022-07-16&checkout=2022-07-18 - there will not be any booking with checkin date = 2022-07-16
    // only later - since 2022-07-17. I consider it as a bug
    @Test
    public void testGetAllByDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String checkInDate = dateFormat.format(booking.getBooking().getBookingDates().getCheckIn());
        String checkOutDate = dateFormat.format(booking.getBooking().getBookingDates().getCheckOut());
        given()
                .contentType(JSON)
                .contentType(JSON)
                .param("checkin", checkInDate)
                .param("checkout", checkOutDate)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("bookingid.", hasItem(bookingId));
    }

    @Test
    public void testGetAllByDateWiderRange() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String checkInDate =  dateFormat.format(Date.from(booking.getBooking().getBookingDates()
                .getCheckIn()
                .toInstant()
                .minus(4, ChronoUnit.DAYS)));
        String checkOutDate = dateFormat.format(Date.from(booking.getBooking().getBookingDates()
                .getCheckIn()
                .toInstant()
                .plus(2, ChronoUnit.DAYS)));;
        given()
                .contentType(JSON)
                .contentType(JSON)
                .param("checkin", checkInDate)
                .param("checkout", checkOutDate)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("bookingid.", hasItem(bookingId));
    }

    @Test
    public void testGetAllByDateLateCheckIn() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String checkInDate =  dateFormat.format(Date.from(booking.getBooking().getBookingDates()
                .getCheckIn()
                .toInstant()
                .plus(1, ChronoUnit.DAYS)));
        String checkOutDate = dateFormat.format(booking.getBooking().getBookingDates().getCheckOut());
        given()
                .contentType(JSON)
                .contentType(JSON)
                .param("checkin", checkInDate)
                .param("checkout", checkOutDate)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("bookingid.", not(hasItem(bookingId)));
    }

    @Test
    public void testGetAllByDateEarlyCheckOut() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String checkInDate = dateFormat.format(booking.getBooking().getBookingDates().getCheckIn());
        String checkOutDate =  dateFormat.format(Date.from(booking.getBooking().getBookingDates()
                .getCheckOut()
                .toInstant()
                .minus(1, ChronoUnit.DAYS)));
        given()
                .contentType(JSON)
                .contentType(JSON)
                .param("checkin", checkInDate)
                .param("checkout", checkOutDate)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("bookingid.", not(hasItem(bookingId)));
    }

    //Instead of 400 Bad request error, service returns 500 Internal server error
    //It is not good that service can't handle errors properly
    @Test
    public void testGetAllByDateCorruptedData() {
        given()
                .contentType(JSON)
                .contentType(JSON)
                .param("checkin", "2022-05-a")
                .param("checkout", "2022-07-10")
                .when()
                .get("/booking")
                .then()
                .statusCode(400);
    }

    @AfterAll
    static void tearDown(){
        given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/{id}", bookingId)
                .then()
                .statusCode(201)
                .body(equalTo("Created"));
    }
}