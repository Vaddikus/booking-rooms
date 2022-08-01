package com.herokuapp.restfulbooker.service;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.herokuapp.restfulbooker.model.TokenCredentials;
import com.herokuapp.restfulbooker.model.booking.BookingDetails;
import com.herokuapp.restfulbooker.model.booking.CreateBookingResponse;
import com.herokuapp.restfulbooker.model.enums.HeaderType;
import com.herokuapp.restfulbooker.model.enums.StatusCodes;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import static com.herokuapp.restfulbooker.model.enums.HeaderType.BASIC_AUTHORIZATION;
import static com.herokuapp.restfulbooker.model.enums.HeaderType.COOKIE;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class BookerService {

    protected static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    protected final static Logger LOGGER = Logger.getLogger(BookerService.class.getName());

    protected static String token;
    protected static CreateBookingResponse booking;
    protected static Properties properties = new Properties();

    static {
        try {
            properties.load(BookerService.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            LOGGER.info("Cannot load properties file: " + e.getMessage());
        }

        String body = gson.toJson(TokenCredentials.builder()
                .username(properties.getProperty("username"))
                .password(properties.getProperty("password"))
                .build());

        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RestAssured.config = RestAssuredConfig.config()
                .encoderConfig(EncoderConfig.encoderConfig()
                        .appendDefaultContentCharsetToContentTypeIfUndefined(false));

        token = RestAssured.given()
                .contentType(JSON)
                .body(body)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    public CreateBookingResponse createBooking() {
        BookingDetails request = buildRequest();

        return RestAssured.given()
                .contentType(JSON)
                .body(gson.toJson(request))
                .log()
                .all()
                .when()
                .post("/booking")
                .then()
                .log()
                .body()
                .statusCode(200)
                .extract()
                .as(CreateBookingResponse.class);
    }

    //GET by ID to check that correct data are saved
    public BookingDetails getBookingById(int bookingId) {
        return given()
                .contentType(JSON)
                .cookie("token=" + token)
                .log()
                .all()
                .when()
                .get("/booking/{id}", bookingId)
                .as(BookingDetails.class);
    }

    public ValidatableResponse getAllByIds(StatusCodes statusCode) {
        return given()
                .contentType(JSON)
                .when()
                .get("/booking")
                .then()
                .statusCode(statusCode.getCode());
    }

    public ValidatableResponse getAllByIdsWithName(StatusCodes statusCode, List<String> params) {
        return given()
                .contentType(JSON)
                .param("firstname", params.get(0))
                .param("lastname", params.get(1))
                .when()
                .get("/booking")
                .then()
                .statusCode(statusCode.getCode());
    }

    public ValidatableResponse getAllByIdsWithDates(StatusCodes statusCode, List<String> params) {
        return given()
                .contentType(JSON)
                .param("checkin", params.get(0))
                .param("checkout", params.get(1))
                .when()
                .get("/booking")
                .then()
                .statusCode(statusCode.getCode());
    }

    public void checkPresenceOfBooking(int bookingId) {
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

    public void checkAbsenceOfBooking(int bookingId) {

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

    public String deleteBooking(int bookingId, StatusCodes statusCode, HeaderType headerType) {
        String header = getHeader(headerType);

        return given()
                .contentType(JSON)
                .log()
                .all()
                .header(headerType.getType(), header)
                .when()
                .delete("/booking/{id}", bookingId)
                .then()
                .statusCode(statusCode.getCode())
                .log()
                .all()
                .extract()
                .asString();
    }

    public <T> T updateBooking(BookingDetails updateRequest, StatusCodes statusCode, int bookingId,
                               ContentType contentType, HeaderType headerType, Type type) {
        String header = getHeader(headerType);

        String response = given()
                .contentType(contentType)
                .header(headerType.getType(), header)
                .body(gson.toJson(updateRequest))
                .log()
                .all()
                .when()
                .patch("/booking/{id}", bookingId)
                .then()
                .statusCode(statusCode.getCode())
                .log()
                .all()
                .extract()
                .asString();
        return type.getTypeName().equals("String.class") ? (T) response : gson.fromJson(response, type);
    }

    public String updateBooking(String updateRequest, StatusCodes statusCode, int bookingId,
                                ContentType contentType, HeaderType headerType) {
        String header = getHeader(headerType);

        return given()
                .accept(contentType.getContentTypeStrings()[0])
                .contentType(contentType.getContentTypeStrings()[1])
                .header(headerType.getType(), header)
                .body(updateRequest)
                .log()
                .all()
                .when()
                .patch("/booking/{id}", bookingId)
                .then()
                .statusCode(statusCode.getCode())
                .log()
                .all()
                .extract()
                .asString();
    }

    public String updateBookingWithParams(BookingDetails updateRequest, StatusCodes statusCode, int bookingId,
                                          ContentType contentType, HeaderType headerType) {
        String header = getHeader(headerType);

        return given()
                .contentType(contentType)
                .header(headerType.getType(), header)
                .formParam("firstname", updateRequest.getFirstName())
                .formParam("lastname", updateRequest.getLastName())
                .formParam("totalprice", updateRequest.getTotalPrice())
                .formParam("depositpaid", updateRequest.isDepositPaid())
                .formParam("additionalneeds", updateRequest.getAdditionalNeeds())
                .log()
                .all()
                .when()
                .patch("/booking/{id}", bookingId)
                .then()
                .log()
                .all()
                .statusCode(statusCode.getCode())
                .extract()
                .asString();
    }

    public static BookingDetails buildRequest() {
        Faker faker = new Faker();
        return BookingDetails
                .builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .totalPrice(faker.number().numberBetween(102, 10000))
                .depositPaid(false)
                .additionalNeeds(faker.chuckNorris().fact())
                .bookingDates(BookingDetails.BookingDates
                        .builder()
                        .checkIn(Date.from(Instant.now()))
                        .checkOut(Date.from(Instant.now().plus(2, ChronoUnit.DAYS)))
                        .build())
                .build();
    }

    private String getHeader(HeaderType headerType) {
        String header;
        if (headerType == BASIC_AUTHORIZATION) {
            header = String.format("Basic %s", properties.getProperty("auth-header"));
        } else if (headerType == COOKIE) {
            header = String.format("token=%s", token);
        } else {
            header = "";
        }
        return header;
    }
}