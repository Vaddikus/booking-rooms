package com.herokuapp.restfulbooker;

import com.herokuapp.restfulbooker.config.BookerConfiguration;
import com.herokuapp.restfulbooker.service.BookerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.herokuapp.restfulbooker.model.enums.HeaderType.BASIC_AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = BookerConfiguration.class)
public class DeleteBookingTests {

    @Autowired
    private BookerService bookerService;

    @Test
    public void testDeleteValidBookingWithBasicAuth() {
        int bookingId = bookerService.createBooking().getBookingId();
        assertThat(bookerService.getBookingById(bookingId)).isNotNull();
        String response = bookerService.deleteBooking(bookingId, BASIC_AUTHORIZATION);

        assertThat(response).isEqualTo("Created");

//        bookerService.checkAbsenceOfBooking(bookingId);

        //Check correct behaviour after repeated call of delete
//        response = bookerService.deleteBooking(bookingId, BASIC_AUTHORIZATION);
//        assertThat(response).isEqualTo("Method Not Allowed");
    }

//    @Test
//    public void testDeleteValidBookingWithCookie() {
//        int bookingId = bookerService.createBooking().getBookingId();
//        bookerService.checkPresenceOfBooking(bookingId);
//
//        String response = bookerService.deleteBooking(bookingId, CREATED, COOKIE);
//        assertThat(response).isEqualTo("Created");
//
//        bookerService.checkAbsenceOfBooking(bookingId);
//
//        //Check correct behaviour after repeated call of delete
//        response = bookerService.deleteBooking(bookingId, METHOD_NOT_ALLOWED, COOKIE);
//        assertThat(response).isEqualTo("Method Not Allowed");
//    }
//
//    @Test
//    public void testDeleteValidBookingWithAbsentAuthorization() {
//        int bookingId = bookerService.createBooking().getBookingId();
//        String response = bookerService.deleteBooking(bookingId, FORBIDDEN, EMPTY);
//        assertThat(response).isEqualTo("Forbidden");
//    }
//
//    @Test
//    public void testDeleteValidBookingWithAbsentId() {
//        String response = bookerService.deleteBooking(8000008, METHOD_NOT_ALLOWED, COOKIE);
//        assertThat(response).isEqualTo("Method Not Allowed");
//
//        response = bookerService.deleteBooking(8000008, METHOD_NOT_ALLOWED, BASIC_AUTHORIZATION);
//        assertThat(response).isEqualTo("Method Not Allowed");
//    }
}