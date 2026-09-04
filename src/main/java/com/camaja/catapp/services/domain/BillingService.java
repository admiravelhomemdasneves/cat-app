package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderBill;
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

    @Transactional
    public void recalculateForOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        recalculate(order);
    }

    void recalculate(Order order) {
        OrderBill bill = order.getOrderBill();
        if (bill == null) {
            bill = new OrderBill();
            bill.setOrder(order);
        }

        BigDecimal expensesAmount = bill.getExpensesAmount() != null ? bill.getExpensesAmount() : BigDecimal.ZERO;
        BigDecimal serviceAmount = bill.getServiceAmount() != null ? bill.getServiceAmount() : BigDecimal.ZERO;
        BigDecimal subtotal = expensesAmount.add(serviceAmount);

        BigDecimal vatRate = bill.getVatRate() != null ? bill.getVatRate() : BigDecimal.ZERO;
        BigDecimal vatAmount = subtotal.multiply(vatRate).setScale(4, RoundingMode.HALF_UP);

        bill.setVatAmount(vatAmount);
        bill.setTotalAmount(subtotal.add(vatAmount).setScale(4, RoundingMode.HALF_UP));
        bill.setA$date$(Timestamp.from(Instant.now()));

        orderBillRepository.save(bill);
    }
}
