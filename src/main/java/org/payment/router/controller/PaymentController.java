package org.payment.router.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.payment.router.model.PaymentRequest;
import org.payment.router.worker.PaymentProcessorWorker;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PaymentController {
    @Inject
    PaymentProcessorWorker paymentProcessorWorker;

    @POST
    @Path("/payments")
    public void processPayment(PaymentRequest request) {
        paymentProcessorWorker.enqueuePaymentForProcess(request);
    }
}