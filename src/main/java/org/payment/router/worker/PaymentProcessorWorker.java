package org.payment.router.worker;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.payment.router.client.PaymentProcessorDefaultAsyncClient;
import org.payment.router.client.PaymentProcessorFallbackAsyncClient;
import org.payment.router.client.PaymentStorageAsyncClient;
import org.payment.router.model.PaymentRequest;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

@Singleton
public class PaymentProcessorWorker {
    @ConfigProperty(name = "max.workers.process")
    int maxWorkersToProcess;

    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    @RestClient
    PaymentProcessorDefaultAsyncClient paymentProcessorDefaultAsyncClient;

    @Inject
    @RestClient
    PaymentProcessorFallbackAsyncClient paymentProcessorFallbackAsyncClient;

    @Inject
    @RestClient
    PaymentStorageAsyncClient paymentStorageAsyncClient;

    private static final LinkedBlockingQueue<PaymentRequest> paymentsToProcess = new LinkedBlockingQueue<>();

    public void startProcessWorker(@Observes StartupEvent ev) {
        IntStream.range(0, maxWorkersToProcess).forEach(__-> managedExecutor.execute(() -> { for(;;) { this.processPayment(this.getNextPaymentToProcess()); } }));
    }

    private PaymentRequest getNextPaymentToProcess() {
        try {
            return paymentsToProcess.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void processPayment(PaymentRequest request) {
        try {
            final PaymentRequest paymentReadyToProcess = request.toProcessOnDefault();
            this.paymentProcessorDefaultAsyncClient.process(paymentReadyToProcess);
            this.paymentStorageAsyncClient.save(paymentReadyToProcess);
        } catch (Exception eDefault) {
            try {
                final PaymentRequest paymentReadyToProcess = request.toProcessOnFallback();
                this.paymentProcessorFallbackAsyncClient.process(paymentReadyToProcess);
                this.paymentStorageAsyncClient.save(paymentReadyToProcess);
            } catch (Exception eFallback) {
                this.enqueuePaymentForProcess(request);
            }
        }
    }

    public void enqueuePaymentForProcess(PaymentRequest request) {
        paymentsToProcess.offer(request);
    }
}
