package com.herokuapp.restfulbooker.service;

import com.herokuapp.restfulbooker.config.FeignConfig;
import com.herokuapp.restfulbooker.model.booking.BookingDetails;
import com.herokuapp.restfulbooker.model.booking.CreateBookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "bookerClient", url = "${feign.client.url}", configuration = FeignConfig.class)
public interface BookerClient {

    @GetMapping("/booking")
    List<CreateBookingResponse> getBookingIds(@RequestParam(required = false) Map<String, String> params);

    @GetMapping("/booking/{id}")
    String getBookingById(@PathVariable("id") int id);

    @DeleteMapping("/booking/{id}")
    String deleteBookingById(@PathVariable("id") int id, @RequestHeader Map<String, String> header);

    @PostMapping(value = "/booking", consumes = MediaType.APPLICATION_JSON_VALUE)
    String createBooking(@RequestBody String body);

    @PatchMapping("/booking/{id}")
    String partialUpdateBooking(@PathVariable("id") int id,
                                @RequestHeader Map<String, String> headers,
                                @RequestBody String body);

    @PatchMapping("/booking/{id}")
    String partialUpdateBookingWithParameters(@PathVariable("id") int id,
                                              @RequestHeader Map<String, String> headers,
                                              @SpringQueryMap BookingDetails params);

    @PostMapping(value = "/auth", consumes = MediaType.APPLICATION_JSON_VALUE)
    String createToken(@RequestBody String body);
}
