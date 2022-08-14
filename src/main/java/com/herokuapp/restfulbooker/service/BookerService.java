package com.herokuapp.restfulbooker.service;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.herokuapp.restfulbooker.model.TokenCredentials;
import com.herokuapp.restfulbooker.model.booking.BookingDetails;
import com.herokuapp.restfulbooker.model.booking.CreateBookingResponse;
import com.herokuapp.restfulbooker.model.enums.HeaderType;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.herokuapp.restfulbooker.model.enums.HeaderType.BASIC_AUTHORIZATION;

@Slf4j
@Service
@TestPropertySource(properties = "application.properties")
public class BookerService {

    protected static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    @Autowired
    private final BookerClient client;

    @Value("Basic ${auth-header}")
    private String authHeader;
    @Value("${token.username}")
    private String username;
    @Value("${token.password}")
    private String password;
    private CreateBookingResponse bookingResponse;

    private String cookie;

    public BookerService(BookerClient client, TokenCredentials tokenCredentials) {
        this.client = client;
        String body = gson.toJson(tokenCredentials);
        String token = JsonPath.read(client.createToken(body), "$.token");
        log.info("Token is: {}", token);
        cookie = String.format("token=%s", token);
    }

    public CreateBookingResponse createBooking() {
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json");

        BookingDetails request = buildRequest();
        log.info("Create booking request: {}", gson.toJson(request));
//        bookingResponse = client.createBooking(gson.toJson(request));
        bookingResponse = gson.fromJson(client.createBooking(gson.toJson(request)), CreateBookingResponse.class);
        log.info("Create booking response: {}", gson.toJson(bookingResponse));

        return bookingResponse;
    }

    //GET by ID to check that correct data are saved
    public BookingDetails getBookingById(int bookingId) {

        BookingDetails response = gson.fromJson(client.getBookingById(bookingId), BookingDetails.class);
        log.info("Get booking by id - {} request: {}", bookingId, gson.toJson(response));

        return response;
    }

    public List<CreateBookingResponse> getAllBookings() {
        List<CreateBookingResponse> bookingsList = client.getBookingIds(null);
        log.info("Get all bookings: {}", gson.toJson(bookingsList));

        return bookingsList;
    }

    public List<CreateBookingResponse> getAllByIdsWithName(Map<String, String> params) {
        List<CreateBookingResponse> bookingsList = client.getBookingIds(params);
        log.info("Get all bookings by first name - {} and last name - {} : {}",
                params.get("firstname"),
                params.get("lastname"),
                gson.toJson(bookingsList));

        return bookingsList;
    }

    public List<CreateBookingResponse> getAllByIdsWithDates(Map<String, String> params) {
        List<CreateBookingResponse> bookingsList = client.getBookingIds(params);
        log.info("Get all bookings by check-in - {} and check-out - {} : {}",
                params.get("checkin"),
                params.get("checkout"),
                gson.toJson(bookingsList));

        return bookingsList;
    }

//    public void checkAbsenceOfBooking(int bookingId) {
//
//        //Check that "record" of created booking is absent
//        given()
//                .contentType(JSON)
//                .when()
//                .get("/booking/{id}", bookingId)
//                .then()
//                .statusCode(404)
//                .body(equalTo("Not Found"));
//
//        //Check that id of created booking is present in list of all ids
//        given()
//                .contentType(JSON)
//                .when()
//                .get("/booking")
//                .then()
//                .statusCode(200)
//                .body("bookingid.", not(hasItem(bookingId)));
//    }

    public String deleteBooking(int bookingId, HeaderType headerType) {
        String header = headerType == BASIC_AUTHORIZATION ? authHeader : cookie;

        String response = client.deleteBookingById(bookingId, new HashMap<>() {{
            put(headerType.getType(), header);
        }});
        log.info("Delete response by id {}: {}", bookingId, response);

        return response;
    }

    public String partialUpdateBooking(BookingDetails updateRequest, int bookingId,
                                       Map<String, String> headers) {
        String body = gson.toJson(updateRequest);
        String response = client.partialUpdateBooking(bookingId, headers, body);
        log.info("Partial update with body {} for id {}: {}", body, bookingId, response);

        return response;
    }

//    public String partialUpdateBooking(String updateRequest, StatusCodes statusCode, int bookingId,
//                                ContentType contentType, HeaderType headerType) {
//        String header = headerType == BASIC_AUTHORIZATION ? authHeader : cookie;
//
//
//        return given()
//                .accept(contentType.getContentTypeStrings()[0])
//                .contentType(contentType.getContentTypeStrings()[1])
//                .header(headerType.getType(), header)
//                .body(updateRequest)
//                .log()
//                .all()
//                .when()
//                .patch("/booking/{id}", bookingId)
//                .then()
//                .statusCode(statusCode.getCode())
//                .log()
//                .all()
//                .extract()
//                .asString();
//    }

    public String updateBookingWithParams(int bookingId, Map<String, String> headers, BookingDetails updateRequest) {
        String body = gson.toJson(updateRequest);
        String response = client.partialUpdateBookingWithParameters(bookingId, headers ,updateRequest);
        log.info("Partial update with body {} for id {}: {}", body, bookingId, response);

        return response;
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
}