package org.payment.router.model;

public class PaymentResponse {
    public String message;
    public String createdID;

    public PaymentResponse() {
    }

    public PaymentResponse(String message) {
        this.message = message;
    }

    public PaymentResponse setId(String id) {
        this.createdID = id;
        return this;
    }
}