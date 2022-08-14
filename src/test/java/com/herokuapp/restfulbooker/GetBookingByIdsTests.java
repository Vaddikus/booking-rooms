package com.herokuapp.restfulbooker;

import com.herokuapp.restfulbooker.model.booking.CreateBookingResponse;
import com.herokuapp.restfulbooker.model.enums.StatusCodes;
import com.herokuapp.restfulbooker.service.BookerService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static com.herokuapp.restfulbooker.model.enums.HeaderType.BASIC_AUTHORIZATION;
import static com.herokuapp.restfulbooker.model.enums.StatusCodes.*;
import static org.hamcrest.Matchers.*;

@AllArgsConstructor
@ContextConfiguration
public class GetBookingByIdsTests {

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
//        bookerService.deleteBooking(bookingId, CREATED, BASIC_AUTHORIZATION);
//    }
//
//    @Test
//    public void testGetAllByIds() {
//        bookerService.checkPresenceOfBooking(bookingId);
//        bookerService.getAllByIds(SUCCESS)
//                .body("bookingid.", hasItem(bookingId));
//    }
//
//    @Test
//    public void testGetAllByIdsAndName() {
//        String firstName = bookingResponse.getBooking().getFirstName();
//        String lastName = bookingResponse.getBooking().getLastName();
//
//        bookerService.getAllByIdsWithName(SUCCESS, List.of(firstName, lastName))
//                .body("bookingid.", hasItem(bookingId));
//    }
//
//    @Test
//    public void testGetAllByIdsAndOtherName() {
//        bookerService.getAllByIdsWithName(SUCCESS, List.of("Jim", "Brown"))
//                .body("bookingid.", not(hasItem(bookingId)));
//    }
//
//    @Test
//    public void testGetAllByIdsAndInvalidName() {
//        bookerService.getAllByIdsWithName(SUCCESS, List.of("Aaa", "Bbb111"))
//                .body(is("[]"));
//    }
//
//    //This test is failed because parameter "checkin" doesn't include passed boundary value
//    // It means that if we send checkin=2022-07-16&checkout=2022-07-18 - there will not be any booking with checkin date = 2022-07-16
//    // only later - since 2022-07-17. I consider it as a bug
//    @Test
//    public void testGetAllByDate() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String checkInDate = dateFormat.format(bookingResponse.getBooking().getBookingDates().getCheckIn());
//        String checkOutDate = dateFormat.format(bookingResponse.getBooking().getBookingDates().getCheckOut());
//
//        bookerService.getAllByIdsWithDates(SUCCESS, List.of(checkInDate, checkOutDate))
//                        .body("bookingid.", hasItem(bookingId));
//
//    }
//
//    @Test
//    public void testGetAllByDateWiderRange() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String checkInDate = dateFormat.format(Date.from(bookingResponse.getBooking().getBookingDates()
//                .getCheckIn()
//                .toInstant()
//                .minus(4, ChronoUnit.DAYS)));
//        String checkOutDate = dateFormat.format(Date.from(bookingResponse.getBooking().getBookingDates()
//                .getCheckIn()
//                .toInstant()
//                .plus(2, ChronoUnit.DAYS)));
//
//        bookerService.getAllByIdsWithDates(SUCCESS, List.of(checkInDate, checkOutDate))
//                .body("bookingid.", hasItem(bookingId));
//    }
//
//    @Test
//    public void testGetAllByDateLateCheckIn() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String checkInDate = dateFormat.format(Date.from(bookingResponse.getBooking().getBookingDates()
//                .getCheckIn()
//                .toInstant()
//                .plus(1, ChronoUnit.DAYS)));
//        String checkOutDate = dateFormat.format(bookingResponse.getBooking().getBookingDates().getCheckOut());
//
//        bookerService.getAllByIdsWithDates(SUCCESS, List.of(checkInDate, checkOutDate))
//                .body("bookingid.", not(hasItem(bookingId)));
//    }
//
//    @Test
//    public void testGetAllByDateEarlyCheckOut() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String checkInDate = dateFormat.format(bookingResponse.getBooking().getBookingDates().getCheckIn());
//        String checkOutDate = dateFormat.format(Date.from(bookingResponse.getBooking().getBookingDates()
//                .getCheckOut()
//                .toInstant()
//                .minus(1, ChronoUnit.DAYS)));
//
//        bookerService.getAllByIdsWithDates(SUCCESS, List.of(checkInDate, checkOutDate))
//                .body("bookingid.", not(hasItem(bookingId)));
//    }
//
//    //Instead of 400 Bad request error, service returns 500 Internal server error
//    //It is not good that service can't handle errors properly
//    @Test
//    public void testGetAllByDateCorruptedData() {
//
//        bookerService.getAllByIdsWithDates(BAD_REQUEST, List.of("2022-05-a", "2022-07-10"));
//
//    }
}