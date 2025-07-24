package org.payment.router.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.payment.router.model.PaymentRequest;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class PaymentRepository implements PanacheRepository<PaymentRequest> {
    public List<PaymentRequest> findByRequestedAtBetween(Instant from, Instant to) {
        if (from != null && to != null) {
            return list("requestedAt >= ?1 and requestedAt <= ?2", from, to);
        } else if (from != null) {
            return list("requestedAt >= ?1", from);
        } else if (to != null) {
            return list("requestedAt <= ?1", to);
        } else {
            return listAll();
        }
    }
}