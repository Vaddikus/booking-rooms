package com.herokuapp.restfulbooker.util;

import com.herokuapp.restfulbooker.model.booking.BookingDetails;
import com.herokuapp.restfulbooker.service.BookerService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Logger;

public class BookerServiceHelper {

    protected final static Logger LOGGER = Logger.getLogger(BookerService.class.getName());

    public static BookingDetails convertXmlStringToBookingDetails(String xmlResponse) {
        BookingDetails updateResponse = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(BookingDetails.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            updateResponse = (BookingDetails) unmarshaller.unmarshal(new StringReader(xmlResponse));
        } catch (JAXBException e) {
            LOGGER.info("Xml unmarshalling error occurs: " + e.getMessage());
        }
        return updateResponse;
    }

    public static String getXml(BookingDetails updateRequest) {
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
        return xmlRequest;
    }
}
