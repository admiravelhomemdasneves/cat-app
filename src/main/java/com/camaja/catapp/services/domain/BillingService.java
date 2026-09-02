package com.camaja.catapp.services.domain;

import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderBill;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.repository.OrderBillRepository;
import com.camaja.catapp.repository.OrderRepository;
import com.camaja.catapp.repository.PrintingServiceRepository;
import com.camaja.catapp.repository.ProductParameterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BillingService {
    private final OrderRepository orderRepository;
    private final OrderBillRepository orderBillRepository;
    private final ProductParameterRepository productParameterRepository;
    private final PrintingServiceRepository printingServiceRepository;

    public void snapshotPrices(OrderItem orderItem, OrderItemDTO dto) {
        if (orderItem.getUnitPrice() == null
                && dto.getProduct() != null
                && dto.getProduct().getIdProductParameter() != null) {
            productParameterRepository.findById(dto.getProduct().getIdProductParameter())
                    .ifPresent(pp -> {
                        if (pp.getPrice() != null) orderItem.setUnitPrice(pp.getPrice());
                    });
        }
    }

    @Transactional
    public void recalculateForOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        recalculate(order);
    }

    void recalculate(Order order) {
        List<OrderItem> activeItems = order.getOrderItems() == null ? List.of()
                : order.getOrderItems().stream()
                        .filter(i -> Boolean.TRUE.equals(i.getFlagActive()))
                        .toList();

        BigDecimal expensesAmount = BigDecimal.ZERO;
        BigDecimal serviceAmount = BigDecimal.ZERO;

        for (OrderItem item : activeItems) {
            int qty = item.getQuantity() != null ? item.getQuantity() : 1;
            int hours = item.getServiceHours() != null ? item.getServiceHours() : 0;

            if (item.getUnitPrice() != null) {
                expensesAmount = expensesAmount.add(item.getUnitPrice().multiply(BigDecimal.valueOf(qty)));
            }
        }

        BigDecimal subtotal = expensesAmount.add(serviceAmount);

        OrderBill bill = order.getOrderBill();
        if (bill == null) {
            bill = new OrderBill();
            bill.setOrder(order);
        }

        BigDecimal vatRate = bill.getVatRate() != null ? bill.getVatRate() : BigDecimal.ZERO;
        BigDecimal vatAmount = subtotal.multiply(vatRate).setScale(4, RoundingMode.HALF_UP);

        bill.setExpensesAmount(expensesAmount.setScale(4, RoundingMode.HALF_UP));
        bill.setServiceAmount(serviceAmount.setScale(4, RoundingMode.HALF_UP));
        bill.setVatAmount(vatAmount);
        bill.setTotalAmount(subtotal.add(vatAmount).setScale(4, RoundingMode.HALF_UP));
        bill.setA$date$(Timestamp.from(Instant.now()));

        orderBillRepository.save(bill);
        log.debug("Recalculated bill for order {}: expenses={}, service={}, vat={}, total={}",
                order.getId_order(), bill.getExpensesAmount(), bill.getServiceAmount(),
                bill.getVatAmount(), bill.getTotalAmount());
    }
}
