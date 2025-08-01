package org.payment.router.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.payment.router.model.PaymentRequest;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class PaymentRepository  {
    @Inject
    EntityManager em;

    @Transactional
    public void save(PaymentRequest payment) {
        em.createNativeQuery("""
            INSERT INTO payments (correlationId, amount, provider, requestedAt)
            VALUES (:correlationId, :amount, :provider, :requestedAt)
        """)
                .setParameter("correlationId", payment.correlationId)
                .setParameter("amount", payment.amount)
                .setParameter("provider", payment.provider)
                .setParameter("requestedAt", payment.requestedAt)
                .executeUpdate();
    }

    public List<PaymentRequest> findByRequestedAtBetween(Instant from, Instant to) {
        return em.createNativeQuery("""
            SELECT * FROM payments
            WHERE requestedAt BETWEEN :from AND :to
        """, PaymentRequest.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
}