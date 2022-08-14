package com.herokuapp.restfulbooker;

import com.herokuapp.restfulbooker.model.booking.BookingDetails;
import com.herokuapp.restfulbooker.model.booking.CreateBookingResponse;
import com.herokuapp.restfulbooker.service.BookerService;
import com.herokuapp.restfulbooker.util.BookerServiceHelper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Logger;

import static com.herokuapp.restfulbooker.model.enums.HeaderType.BASIC_AUTHORIZATION;
import static com.herokuapp.restfulbooker.model.enums.HeaderType.COOKIE;
import static com.herokuapp.restfulbooker.model.enums.StatusCodes.*;
import static io.restassured.http.ContentType.*;
import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor
@ContextConfiguration
public class PartialUpdateBookingTests {

    private final BookerService bookerService;
    private static CreateBookingResponse bookingResponse;
    private static int bookingId;

//    @BeforeAll
//    static void setUp() {
//        bookingResponse = bookerService.createBooking();
//        bookingId = bookingResponse.getBookingId();
//    }
//
//    @AfterAll
//    static void tearDown() {
//        bookerService.deleteBooking(bookingId, CREATED, COOKIE);
//    }
//
//    @Test
//    public void testPartialUpdateWithCookieAndJson() {
//
//        BookingDetails updateRequest = BookerService.buildRequest();
//        BookingDetails updateResponse = bookerService.updateBooking(updateRequest, SUCCESS, bookingId, JSON, COOKIE, BookingDetails.class);
//
//        checkUpdatedBookingData(updateRequest, updateResponse);
//        BookingDetails getResponse = bookerService.getBookingById(bookingId);
//        checkUpdatedBookingData(updateRequest, getResponse);
//    }
//
//    @Test
//    public void testValidDateUpdate() {
//
//        BookingDetails updateRequest = BookerService.buildRequest();
//        updateRequest.setBookingDates(BookingDetails.BookingDates.builder()
//                .checkIn(Date.from(Instant.now().plus(2, ChronoUnit.DAYS)))
//                .checkOut(Date.from(Instant.now().plus(6, ChronoUnit.DAYS)))
//                .build());
//
//        BookingDetails updateResponse = bookerService.updateBooking(updateRequest, SUCCESS,
//                bookingId, JSON, COOKIE, BookingDetails.class);
//
//        checkUpdatedBookingData(updateRequest, updateResponse);
//        BookingDetails getResponse = bookerService.getBookingById(bookingId);
//        checkUpdatedBookingData(updateRequest, getResponse);
//    }
//
//    //Test is failed because service shouldn't allow setting checkIn date AFTER checkOut date
//    @Test
//    public void testIncorrectDateUpdate() {
//
//        BookingDetails updateRequest = BookerService.buildRequest();
//        updateRequest.setBookingDates(BookingDetails.BookingDates.builder()
//                .checkIn(Date.from(Instant.now().plus(6, ChronoUnit.DAYS)))
//                .checkOut(Date.from(Instant.now().minus(6, ChronoUnit.DAYS)))
//                .build());
//
//        bookerService.updateBooking(updateRequest, BAD_REQUEST, bookingId, JSON, COOKIE, String.class);
//    }
//
//    //Test fails here - because status code is 200 (instead of 400) and no error message
//    @Test
//    public void invalidParameterUpdateTotalPrice() {
//        String body = "{\"totalprice\": \"a\"}";
//        bookerService.updateBooking(body, BAD_REQUEST, bookingId, JSON, COOKIE);
//    }
//
//    //Test fails here - because status code is 200 (instead of 400) and no error message
//    @Test
//    public void invalidParameterUpdateDepositPaid() {
//        String body = "{\"depositpaid\": \"a\"}";
//        bookerService.updateBooking(body, BAD_REQUEST, bookingId, JSON, COOKIE);
//    }
//
//    //Test fails here - because status code is 200 (instead of 400) and no error message
//    @Test
//    public void invalidParameterUpdateCheckIn() {
//        String body = "{\"bookingdates\": {\"checkin\": \"a\"}}";
//        bookerService.updateBooking(body, BAD_REQUEST, bookingId, JSON, COOKIE);
//    }
//
//    //Test fails here - because status code is 200 (instead of 400) and no error message
//    @Test
//    public void invalidParameterUpdateCheckOut() {
//        String body = "{\"bookingdates\": {\"checkout\": \"b\"}}";
//        bookerService.updateBooking(body, BAD_REQUEST, bookingId, JSON, COOKIE);
//    }
//
//    //Test is failed because update of <depositpaid> works very incorrect:
//    // when we pass "false" (when field is already defined as "true") - it doesn't change to "false"
//    // when we pass either "false" or "true" (when field is already defined as "false") -> it is re-set as "true" (in both cases)
//    @Test
//    public void testPartialUpdateWithXmlAndBasicAuth() {
//        BookingDetails updateRequest = BookerService.buildRequest();
//        String xmlRequest = BookerServiceHelper.getXml(updateRequest);
//        String xmlResponse = bookerService.updateBooking(xmlRequest, SUCCESS, bookingId, XML, BASIC_AUTHORIZATION);
//
//        BookingDetails updateResponse = BookerServiceHelper.convertXmlStringToBookingDetails(xmlResponse);
//
//        checkUpdatedBookingData(updateRequest, updateResponse);
//        BookingDetails getResponse = bookerService.getBookingById(bookingId);
//        checkUpdatedBookingData(updateRequest, getResponse);
//    }
//
//    //Test is failed because update of <depositpaid> works very incorrect:
//    // when we pass "false" (when field is already defined as "true") - it doesn't change to "false"
//    // when we pass either "false" or "true" (when field is already defined as "false") -> it is re-set as "true" (in both cases)
//    @Test
//    public void testPartialUpdateWithUrlEncoded() {
//        BookingDetails updateRequest = BookerService.buildRequest();
//        String updateResponse = bookerService.updateBookingWithParams(updateRequest, SUCCESS, bookingId, URLENC, COOKIE);
//        checkUrlEncodedResponse(updateResponse, updateRequest);
//
//        //GET by ID to check that correct data are saved
//        BookingDetails getResponse = bookerService.getBookingById(bookingId);
//        checkUpdatedBookingData(updateRequest, getResponse);
//    }
//
//    private void checkUpdatedBookingData(BookingDetails request, BookingDetails response) {
//        //In case when we partially update booking without changing dates
//        if (request.getBookingDates() == null) {
//            request.setBookingDates(BookingDetails.BookingDates.builder()
//                    .checkIn(bookingResponse.getBooking().getBookingDates().getCheckIn())
//                    .checkOut(bookingResponse.getBooking().getBookingDates().getCheckOut())
//                    .build());
//        }
//        assertThat(request).usingRecursiveComparison().ignoringFields("bookingDates.checkIn", "bookingDates.checkOut").isEqualTo(response);
//
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        assertThat(dateFormat.format(response.getBookingDates().getCheckIn())).
//                as("Check in date")
//                .isEqualTo(dateFormat.format(request.getBookingDates().getCheckIn()));
//        assertThat(dateFormat.format((response.getBookingDates().getCheckOut()))).
//                as("Check out date")
//                .isEqualTo(dateFormat.format(request.getBookingDates().getCheckOut()));
//    }
//
//    private void checkUrlEncodedResponse(String response, BookingDetails updateRequest) {
//        try {
//            assertThat(URLDecoder.decode(response, StandardCharsets.UTF_8.toString())).contains(updateRequest.getFirstName(),
//                    updateRequest.getLastName(),
//                    String.valueOf((int) updateRequest.getTotalPrice()),
//                    updateRequest.getAdditionalNeeds());
//        } catch (UnsupportedEncodingException e) {
//            LOGGER.info("URL decoding error occurs: " + e.getMessage());
//        }
//    }
}