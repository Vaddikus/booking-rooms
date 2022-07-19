package com.herokuapp.restfulbooker.model.booking;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "booking")
@XmlAccessorType(XmlAccessType.FIELD)
public class BookingDetails {
    @SerializedName("firstname")
    @XmlElement(name="firstname")
    String firstName;
    @SerializedName("lastname")
    @XmlElement(name="lastname")
    String lastName;
    @SerializedName("totalprice")
    @XmlElement(name="totalprice")
    float totalPrice;
    @SerializedName("depositpaid")
    @XmlElement(name="depositpaid")
    boolean depositPaid;
    @SerializedName("bookingdates")
    @XmlElement(name="bookingdates")
    BookingDates bookingDates;
    @SerializedName("additionalneeds")
    @XmlElement(name="additionalneeds")
    String additionalNeeds;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class BookingDates {
        @SerializedName("checkin")
        @XmlElement(name="checkin")
        Date checkIn;
        @SerializedName("checkout")
        @XmlElement(name="checkout")
        Date checkOut;
    }
}