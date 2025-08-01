package org.payment.router.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.payment.router.model.PaymentRequest;
import org.payment.router.model.PaymentsSummary;
import org.payment.router.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PaymentService {
    @Inject
    PaymentRepository paymentRepository;

    public PaymentsSummary getSummary(Instant from, Instant to) {
        List<PaymentRequest> filtered = paymentRepository.findByRequestedAtBetween(from, to);

        List<PaymentRequest> defaultList = filtered.stream()
                .filter(p -> "default".equalsIgnoreCase(p.provider))
//                .filter(p -> !p.requestedAt.isBefore(from) && !p.requestedAt.isAfter(to))
                .collect(Collectors.toList());

        List<PaymentRequest> fallbackList = filtered.stream()
                .filter(p -> "fallback".equalsIgnoreCase(p.provider))
//                .filter(p -> !p.requestedAt.isBefore(from) && !p.requestedAt.isAfter(to))
                .collect(Collectors.toList());

        PaymentsSummary summary = new PaymentsSummary();
        summary.defaultProcessor = buildSummaryData(defaultList);
        summary.fallbackProcessor = buildSummaryData(fallbackList);

        return summary;
    }

    private PaymentsSummary.SummaryData buildSummaryData(List<PaymentRequest> list) {
        int totalRequests = list.size();
        BigDecimal totalAmount = list.stream()
                .map(p -> BigDecimal.valueOf(p.amount))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PaymentsSummary.SummaryData(totalRequests, totalAmount);
    }
}