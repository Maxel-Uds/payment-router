package org.payment.router.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.payment.router.model.PaymentRequest;

@ApplicationScoped
public class PaymentRepository implements PanacheRepository<PaymentRequest> {
}