package com.app.userservice.handlers;

import com.estore.core.details.PaymentDetails;
import com.estore.core.details.User;
import com.estore.core.query.FetchUserPaymentDetailQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserEventsHandler {

    @QueryHandler
    public User getUser(FetchUserPaymentDetailQuery fetchUserPaymentDetailsQuery){
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("SERGEY KARGOPOLOV")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        User userRest = User.builder()
                .firstName("Sergey")
                .lastName("Kargopolov")
                .userId(fetchUserPaymentDetailsQuery.getUserId())
                .paymentDetails(paymentDetails)
                .build();

        return  userRest;
    }
}
