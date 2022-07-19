package com.herokuapp.restfulbooker.model.booking;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CreateBookingResponse {
    @SerializedName("bookingid")
    int bookingId;
    BookingDetails booking;
}
