package com.herokuapp.restfulbooker;

import com.github.javafaker.Faker;
import com.herokuapp.restfulbooker.model.booking.BookingDetails;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.URLENC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PartialUpdateBookingTests extends CreateBookingFixture {

    @Test
    public void testPartialUpdateWithCookieAndJson() {

        checkDefaultBookingData();
        BookingDetails updateRequest = buildRequest();

        BookingDetails updateResponse = given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .body(gson.toJson(updateRequest))
                .when()
                .patch("/booking/{id}", bookingId)
                .as(BookingDetails.class);

        checkUpdatedBookingData(updateRequest, updateResponse);
        BookingDetails getResponse = getBookingById();
        checkUpdatedBookingData(updateRequest, getResponse);
    }


    @Test
    public void testValidDateUpdate() {

        checkDefaultBookingData();
        BookingDetails updateRequest = buildRequest();
        updateRequest.setBookingDates(BookingDetails.BookingDates.builder()
                        .checkIn(Date.from(Instant.now().plus(2, ChronoUnit.DAYS)))
                        .checkOut(Date.from(Instant.now().plus(6, ChronoUnit.DAYS)))
                        .build());

        BookingDetails updateResponse = given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .body(gson.toJson(updateRequest))
                .when()
                .patch("/booking/{id}", bookingId)
                .as(BookingDetails.class);

        checkUpdatedBookingData(updateRequest, updateResponse);
        BookingDetails getResponse = getBookingById();
        checkUpdatedBookingData(updateRequest, getResponse);
    }

    //Test is failed because service shouldn't allow setting checkIn date AFTER checkOut date
    @Test
    public void testIncorrectDateUpdate() {

        checkDefaultBookingData();

        BookingDetails updateRequest = BookingDetails.builder()
                .bookingDates(BookingDetails.BookingDates.builder()
                        .checkIn(Date.from(Instant.now().plus(6, ChronoUnit.DAYS)))
                        .checkOut(Date.from(Instant.now().minus(6, ChronoUnit.DAYS)))
                        .build())
                .build();

        given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .body(gson.toJson(updateRequest))
                .when()
                .patch("/booking/{id}", bookingId)
                .then()
                .statusCode(400);
    }

    @Test
    public void invalidParameterUpdate() {

        //Test fails here - because status code is 200 and no error message
        given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .body("{\"totalprice\": \"a\"}")
                .when()
                .patch("/booking/{id}", bookingId)
                .then()
                .statusCode(400);

        //Test fails here - because status code is 200 and no error message
        given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .body("{\"depositpaid\": \"a\"}")
                .when()
                .patch("/booking/{id}", bookingId)
                .then()
                .statusCode(400);


        //Test fails here - "Invalid date" error message is displayed, but status code is 200
        given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .body("{\"bookingdates\": {\"checkin\": \"a\"}")
                .when()
                .patch("/booking/{id}", bookingId)
                .then()
                .statusCode(400);

        //Test fails here - "Invalid date" error message is displayed, but status code is 200
        given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .body("{\"bookingdates\": {\"checkout\": \"b\"}}")
                .when()
                .patch("/booking/{id}", bookingId)
                .then()
                .statusCode(400);

    }

    //Test is failed because update of <depositpaid> works very incorrect:
    // when we pass "false" (when field is already defined as "true") - it doesn't change to "false"
    // when we pass either "false" or "true" (when field is already defined as "false") -> it is re-set as "true" (in both cases)
    @Test
    public void testPartialUpdateWithXmlAndBasicAuth() {

        checkDefaultBookingData();
        BookingDetails updateRequest = buildRequest();

        String xmlRequest = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(BookingDetails.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            StringWriter writer = new StringWriter();
            jaxbMarshaller.marshal(updateRequest, writer);
            xmlRequest = writer.toString();
        } catch (JAXBException e) {
            LOGGER.info("Xml marshalling error occurs: " + e.getMessage());
        }

        String xmlResponse = given()
                .header("Authorization", AUTH_HEADER)
                .accept("application/xml")
                .contentType("text/xml")
                .body(xmlRequest)
                .when()
                .patch("/booking/{id}", bookingId)
                .asString();

        BookingDetails updateResponse = null;

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(BookingDetails.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            updateResponse = (BookingDetails) unmarshaller.unmarshal(new StringReader(xmlResponse));
        } catch (JAXBException e) {
            LOGGER.info("Xml unmarshalling error occurs: " + e.getMessage());
        }

        checkUpdatedBookingData(updateRequest, updateResponse);
        BookingDetails getResponse = getBookingById();
        checkUpdatedBookingData(updateRequest, getResponse);
    }

    //Test is failed because update of <depositpaid> works very incorrect:
    // when we pass "false" (when field is already defined as "true") - it doesn't change to "false"
    // when we pass either "false" or "true" (when field is already defined as "false") -> it is re-set as "true" (in both cases)
    @Test
    public void testPartialUpdateWithUrlEncoded() {

        checkDefaultBookingData();
        BookingDetails updateRequest = buildRequest();

        String updateResponse = given()
                .contentType(URLENC)
                .accept(URLENC)
                .header("Cookie", "token=" + token)
                .formParam("firstname", updateRequest.getFirstName())
                .formParam("lastname", updateRequest.getLastName())
                .formParam("totalprice", updateRequest.getTotalPrice())
                .formParam("depositpaid", updateRequest.isDepositPaid())
                .formParam("additionalneeds", updateRequest.getAdditionalNeeds())
                .when()
                .patch("/booking/{id}", bookingId)
                .asString();

        checkUrlEncodedResponse(updateResponse, updateRequest);

        //GET by ID to check that correct data are saved
        BookingDetails getResponse = given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .urlEncodingEnabled(true)
                .when()
                .get("/booking/{id}", bookingId)
                .as(BookingDetails.class);

        checkUpdatedBookingData(updateRequest, getResponse);
    }

    @BeforeEach
    public void setUp() {
        createBooking();
    }

    @AfterEach
    public void tearDown() {
        given()
                .contentType(JSON)
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/{id}", bookingId)
                .then()
                .statusCode(201)
                .body(equalTo("Created"));
    }

    //GET by ID to check that correct data are saved
    private BookingDetails getBookingById() {
        return given()
                .contentType(JSON)
                .cookie("token=" + token)
                .when()
                .get("/booking/{id}", bookingId)
                .as(BookingDetails.class);
    }

    //Partial update name, totalprice,depositpaid and additionalneeds fields
    private BookingDetails buildRequest() {
        Faker faker = new Faker();
        return BookingDetails
                .builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .totalPrice(faker.number().numberBetween(102, 10000))
                .depositPaid(false)
                .additionalNeeds(faker.chuckNorris().fact())
                .build();
    }

    private void checkDefaultBookingData() {

        BookingDetails response = given()
                .contentType(JSON)
                .when()
                .get("/booking/{id}", bookingId)
                .as(BookingDetails.class);
        SoftAssertions softAssert = new SoftAssertions();
        softAssert.assertThat(response.getFirstName()).
                as("First name").isEqualTo("Franz");
        softAssert.assertThat(response.getLastName()).
                as("Last name").isEqualTo("Ferdinand");
        softAssert.assertThat(response.isDepositPaid()).
                as("Deposit paid").isEqualTo(true);
        softAssert.assertThat(response.getAdditionalNeeds()).
                as("Additional needs").isEqualTo("Mountains view room");
        softAssert.assertThat(response.getTotalPrice()).
                as("Total price").isEqualTo(101);

        softAssert.assertAll();
    }

    private void checkUpdatedBookingData(BookingDetails request, BookingDetails response) {
        SoftAssertions softAssert = new SoftAssertions();
        softAssert.assertThat(response.getFirstName()).
                as("First name").isEqualTo(request.getFirstName());
        softAssert.assertThat(response.getLastName()).
                as("Last name").isEqualTo(request.getLastName());
        softAssert.assertThat(response.isDepositPaid()).
                as("Deposit paid").isEqualTo(false);
        softAssert.assertThat(response.getAdditionalNeeds()).
                as("Additional needs").isEqualTo(request.getAdditionalNeeds());
        softAssert.assertThat(response.getTotalPrice()).
                as("Total price").isEqualTo(request.getTotalPrice());
        Date checkInDate;
        Date checkOutDate;
        //In case when we partially update booking without changing dates
        if (request.getBookingDates() == null) {
            checkInDate = booking.getBooking().getBookingDates().getCheckIn();
            checkOutDate = booking.getBooking().getBookingDates().getCheckOut();
        } else {
            checkInDate = request.getBookingDates().getCheckIn();
            checkOutDate = request.getBookingDates().getCheckOut();
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        softAssert.assertThat(dateFormat.format(response.getBookingDates().getCheckIn())).
                as("Check in date")
                .isEqualTo(dateFormat.format(checkInDate));
        softAssert.assertThat(dateFormat.format((response.getBookingDates().getCheckOut()))).
                as("Check out date")
                .isEqualTo(dateFormat.format(checkOutDate));

        softAssert.assertAll();
    }

    private void checkUrlEncodedResponse(String response, BookingDetails updateRequest) {
        try {
            assertThat(URLDecoder.decode(response, StandardCharsets.UTF_8.toString())).contains(updateRequest.getFirstName(),
                    updateRequest.getLastName(),
                    String.valueOf((int) updateRequest.getTotalPrice()),
                    updateRequest.getAdditionalNeeds());
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("URL decoding error occurs: " + e.getMessage());
        }
    }
}