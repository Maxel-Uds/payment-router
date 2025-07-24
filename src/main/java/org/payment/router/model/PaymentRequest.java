package org.payment.router.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class PaymentRequest {

    @Id
    @Column(nullable = false)
    public String correlationId;

    @Column(nullable = false)
    public Double amount;

    @Column(nullable = false)
    public Instant requestedAt;

    @Column(nullable = false)
    public String provider;

    public PaymentRequest() {}

    public PaymentRequest(String correlationId, Double amount) {
        this.correlationId = correlationId;
        this.amount = amount;
    }

    public PaymentRequest setProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public PaymentRequest setRequestedAt(Instant now) {
        this.requestedAt = now;
        return this;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "correlationId='" + correlationId + '\'' +
                ", amount=" + amount +
                ", requestedAt=" + requestedAt +
                ", provider='" + provider + '\'' +
                '}';
    }

    public PaymentRequest toProcess() {
        return this.setProvider("default").setRequestedAt(Instant.now());
    }
}
