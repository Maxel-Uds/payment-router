package org.payment.router.client;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.payment.router.model.PaymentRequest;
import org.payment.router.model.PaymentResponse;

@RegisterRestClient(configKey="processor-fallback-async-api", baseUri = "${quarkus.rest-client.processor-fallback-async-api.url}")
public interface PaymentProcessorFallbackAsyncClient {
    @POST
    @Path("/payments")
    PaymentResponse process(PaymentRequest request);
}
