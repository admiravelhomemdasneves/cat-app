package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderBill;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.repository.OrderBillRepository;
import com.camaja.catapp.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
public class BillingService {
    private final OrderRepository orderRepository;
    private final OrderBillRepository orderBillRepository;
    private final TenantAccess tenantAccess;

    @Transactional
    public void recalculateForOrder(Long orderId) {
        Order order = orderRepository.findOwned(orderId, tenantAccess.company())
                .orElseThrow(() -> TenantAccess.notFound("Order", orderId));
        recalculate(order);
    }

    void recalculate(Order order) {
        OrderBill bill = order.getOrderBill();
        if (bill == null) {
            bill = new OrderBill();
            bill.setOrder(order);
        }

        BigDecimal priceSum = order.getOrderContents() == null ? BigDecimal.ZERO
                : order.getOrderContents().stream()
                        .filter(item -> Boolean.TRUE.equals(item.getFlagActive()) && item.getPrice() != null)
                        .map(OrderItem::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal vatRate = bill.getVatRate() != null ? bill.getVatRate() : BigDecimal.ZERO;
        bill.setTotalAmount(priceSum.multiply(BigDecimal.ONE.add(vatRate)).setScale(4, RoundingMode.HALF_UP));
        bill.setA$date$(Timestamp.from(Instant.now()));

        orderBillRepository.save(bill);
    }
}
