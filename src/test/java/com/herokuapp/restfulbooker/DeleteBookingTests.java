package com.herokuapp.restfulbooker;

import com.herokuapp.restfulbooker.model.enums.StatusCodes;
import com.herokuapp.restfulbooker.service.BookerService;
import org.junit.jupiter.api.Test;

import static com.herokuapp.restfulbooker.model.enums.HeaderType.*;
import static com.herokuapp.restfulbooker.model.enums.StatusCodes.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DeleteBookingTests  {

    private final BookerService bookerService = new BookerService();

    @Test
    public void testDeleteValidBookingWithBasicAuth() {
        int bookingId = bookerService.createBooking().getBookingId();
        bookerService.checkPresenceOfBooking(bookingId);

        String response = bookerService.deleteBooking(bookingId, CREATED, BASIC_AUTHORIZATION);
        assertThat(response).isEqualTo("Created");

        bookerService.checkAbsenceOfBooking(bookingId);

        //Check correct behaviour after repeated call of delete
        response = bookerService.deleteBooking(bookingId, METHOD_NOT_ALLOWED, BASIC_AUTHORIZATION);
        assertThat(response).isEqualTo("Method Not Allowed");
    }

    @Test
    public void testDeleteValidBookingWithCookie() {
        int bookingId = bookerService.createBooking().getBookingId();
        bookerService.checkPresenceOfBooking(bookingId);

        String response = bookerService.deleteBooking(bookingId, CREATED, COOKIE);
        assertThat(response).isEqualTo("Created");

        bookerService.checkAbsenceOfBooking(bookingId);

        //Check correct behaviour after repeated call of delete
        response = bookerService.deleteBooking(bookingId, METHOD_NOT_ALLOWED, COOKIE);
        assertThat(response).isEqualTo("Method Not Allowed");
    }

    @Test
    public void testDeleteValidBookingWithAbsentAuthorization() {
        int bookingId = bookerService.createBooking().getBookingId();
        String response = bookerService.deleteBooking(bookingId, FORBIDDEN, EMPTY);
        assertThat(response).isEqualTo("Forbidden");
    }

    @Test
    public void testDeleteValidBookingWithAbsentId() {
        String response = bookerService.deleteBooking(8000008, METHOD_NOT_ALLOWED, COOKIE);
        assertThat(response).isEqualTo("Method Not Allowed");

        response = bookerService.deleteBooking(8000008, METHOD_NOT_ALLOWED, BASIC_AUTHORIZATION);
        assertThat(response).isEqualTo("Method Not Allowed");
    }
}