package com.herokuapp.restfulbooker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.herokuapp.restfulbooker.model.TokenCredentials;
import com.herokuapp.restfulbooker.model.booking.BookingDetails;
import com.herokuapp.restfulbooker.model.booking.CreateBookingResponse;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.jupiter.api.BeforeAll;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class CreateBookingFixture {

    protected static final String AUTH_HEADER = "Basic YWRtaW46cGFzc3dvcmQxMjM=";
    protected static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    protected static final Logger LOGGER = Logger.getLogger(GetBookingByIdsTests.class.getName());

    protected static String token;
    protected static int bookingId;
    protected static CreateBookingResponse booking;

    @BeforeAll
    static void createBooking() {
        getToken();

        BookingDetails request = BookingDetails
                .builder()
                .firstName("Franz")
                .lastName("Ferdinand")
                .depositPaid(true)
                .totalPrice(101)
                .additionalNeeds("Mountains view room")
                .bookingDates(BookingDetails.BookingDates
                        .builder()
                        .checkIn(Date.from(Instant.now()))
                        .checkOut(Date.from(Instant.now().plus(2, ChronoUnit.DAYS)))
                        .build())
                .build();
        LOGGER.info("Booking request: " + gson.toJson(request));

        booking = given()
                .contentType(JSON)
                .body(gson.toJson(request))
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateBookingResponse.class);
        LOGGER.info("Booking response: " + gson.toJson(booking));
        bookingId = booking.getBookingId();
    }

    private static void getToken() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RestAssured.config = RestAssuredConfig.config()
                .encoderConfig(EncoderConfig.encoderConfig()
                        .appendDefaultContentCharsetToContentTypeIfUndefined(false));
        token = given()
                .contentType(JSON)
                .body(TokenCredentials.builder().build())
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
