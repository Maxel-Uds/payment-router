package org.payment.router.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.payment.router.model.PaymentRequest;
import org.payment.router.model.PaymentsSummary;
import org.payment.router.service.PaymentService;
import org.payment.router.worker.PaymentProcessorWorker;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PaymentController {
    @Inject
    PaymentProcessorWorker paymentProcessorWorker;

    @Inject
    PaymentService paymentService;

    @POST
    @Path("/payments")
    public Response processPayment(PaymentRequest request) {
        paymentProcessorWorker.enqueuePaymentForProcess(request);
        return Response.accepted().build();
    }

    @GET
    @Path("/payments-summary")
    public PaymentsSummary getSummary(@QueryParam("from") String from, @QueryParam("to") String to) {
        Instant fromInstant = parseDate(from);
        Instant toInstant = parseDate(to);
        return paymentService.getSummary(fromInstant, toInstant);
    }

    private Instant parseDate(String dateStr) {
        if (dateStr == null) return null;
        try {
            return Instant.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid date format, must be ISO-8601 in UTC");
        }
    }
}