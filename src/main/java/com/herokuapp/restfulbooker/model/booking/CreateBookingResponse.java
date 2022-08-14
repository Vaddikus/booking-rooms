package com.herokuapp.restfulbooker.model.booking;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class CreateBookingResponse {
    @SerializedName("bookingid")
    int bookingId;
    @Nullable
    BookingDetails booking;
}
