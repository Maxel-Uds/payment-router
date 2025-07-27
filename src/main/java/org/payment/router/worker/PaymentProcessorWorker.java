package org.payment.router.worker;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.payment.router.client.PaymentProcessorDefaultAsyncClient;
import org.payment.router.model.PaymentRequest;
import org.payment.router.repository.PaymentRepository;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

@Singleton
public class PaymentProcessorWorker {
    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    @RestClient
    PaymentProcessorDefaultAsyncClient paymentProcessorDefaultAsyncClient;

    private static final LinkedBlockingQueue<PaymentRequest> paymentsToProcess = new LinkedBlockingQueue<>();

    public void onStart(@Observes StartupEvent ev) {
        IntStream.range(0, 15).forEach(__-> managedExecutor.execute(() -> { for(;;) { this.processPayment(this.getNextPayment()); } }));
    }

    private PaymentRequest getNextPayment() {
        try {
            return paymentsToProcess.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void enqueuePaymentForProcess(PaymentRequest request) {
        paymentsToProcess.offer(request);
    }

    @Transactional
    public void processPayment(PaymentRequest request) {
        try {
            final PaymentRequest paymentReadyToProcess = request.toProcess();

            this.paymentProcessorDefaultAsyncClient.process(paymentReadyToProcess);
            this.saveProcessedPayment(paymentReadyToProcess);
        } catch (Exception e) {
            this.enqueuePaymentForProcess(request);
        }
    }

    private void saveProcessedPayment(PaymentRequest processedPayment) {
        this.paymentRepository.persist(processedPayment);
    }
}
