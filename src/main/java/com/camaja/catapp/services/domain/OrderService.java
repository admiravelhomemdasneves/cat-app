package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.mappers.OrderItemMapper;
import com.camaja.catapp.mappers.OrderMapper;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderBill;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MaterialRepository materialRepository;
    private final PrintingServiceRepository printingServiceRepository;
    private final ContactRepository contactRepository;
    private final PriorityRepository priorityRepository;
    private final StatusRepository statusRepository;
    private final OrderBillRepository orderBillRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final SecurityUtils securityUtils;
    private final BillingService billingService;

    public List<Order> getActiveOrders() {
        List<Order> activeOrders = orderRepository.findByCompanyAndFlagActive(securityUtils.getCurrentCompany(), true);
        activeOrders.forEach(order -> {
            order.setOrderContents(
                    order.getOrderContents() == null ? List.of()
                    : order.getOrderContents().stream().filter(item -> Boolean.TRUE.equals(item.getFlagActive())).toList()
            );
        });
        return activeOrders;
    }

    public List<OrderDTO> getActiveOrdersDTO() {
        return getActiveOrders().stream().map(orderMapper::toDto).toList();
    }

    public OrderDTO getOrderDTOById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id).orElse(null);
    }

    public void saveOrderItem(OrderItem orderItem) {
        orderItem.setA$date$(Timestamp.from(Instant.now()));
        orderItemRepository.save(orderItem);
    }

    @Transactional
    public void saveOrderDTO(OrderDTO orderDTO) {
        boolean isNewOrder = orderDTO.getIdOrder() == null;
        Order order = orderMapper.toEntity(orderDTO);

        // Detach the bill from the Order entity — managed separately below to avoid
        // the cascade replacing updated values with an unmodified proxy.
        order.setOrderBill(null);

        if (orderDTO.getPriority() != null && orderDTO.getPriority().getId_priority() != null) {
            order.setPriority(priorityRepository.getReferenceById(orderDTO.getPriority().getId_priority()));
        } else {
            order.setPriority(null);
        }

        if (orderDTO.getStatus() != null && orderDTO.getStatus().getId_status() != null) {
            order.setStatus(statusRepository.getReferenceById(orderDTO.getStatus().getId_status()));
        } else {
            order.setStatus(null);
        }

        if (orderDTO.getContact() != null && orderDTO.getContact().getIdContact() != null) {
            order.setContact(contactRepository.getReferenceById(orderDTO.getContact().getIdContact()));
        } else {
            order.setContact(null);
        }

        if (orderDTO.getDeliveryAddress() == null) {
            if (orderDTO.getContact() != null && orderDTO.getContact().getShippingAddress() != null) {
                order.setDelivery_address(orderDTO.getContact().getShippingAddress());
            }
        }

        if (order.getDate_requested() == null) {
            order.setDate_requested(Timestamp.from(Instant.now()));
        }

        order.setCompany(securityUtils.getCurrentCompany());
        order.setDate_status(Timestamp.from(Instant.now()));
        order.setA$date$(Timestamp.from(Instant.now()));
        Order savedOrder = orderRepository.save(order);

        // Manage OrderBill separately so ID_ORDER and field values are always correct.
        if (isNewOrder) {
            OrderBill bill = new OrderBill();
            bill.setOrder(savedOrder);
            orderBillRepository.save(bill);
        } else {
            OrderBill bill = orderBillRepository.findByOrder(savedOrder)
                .orElseGet(() -> { OrderBill b = new OrderBill(); b.setOrder(savedOrder); return b; });

            boolean vatRateChanged = !Objects.equals(bill.getVatRate(), orderDTO.getVatRate());
            bill.setNif(orderDTO.getNif());
            bill.setVatRate(orderDTO.getVatRate());
            bill.setBill_address(orderDTO.getBillAddress());

            if (vatRateChanged) {
                BigDecimal priceSum = orderItemRepository.findByOrderAndFlagActive(savedOrder, true).stream()
                    .filter(item -> item.getPrice() != null)
                    .map(OrderItem::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal vat = bill.getVatRate() != null ? bill.getVatRate() : BigDecimal.ZERO;
                bill.setTotalAmount(priceSum.multiply(BigDecimal.ONE.add(vat)).setScale(4, RoundingMode.HALF_UP));
            } else {
                bill.setTotalAmount(orderDTO.getTotalAmount());
            }

            orderBillRepository.save(bill);
        }
    }

    @Transactional
    public OrderItemDTO calculateOrderItemPrice(Long id) {
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OrderItem not found: " + id));

        BigDecimal price = BigDecimal.ZERO;
        BigDecimal expenses = null;

        // Material retail price × quantity
        if (item.getMaterial() != null
                && item.getMaterial().getRetailPrice() != null
                && item.getMaterial().getRetailPrice().getPriceValue() != null) {
            BigDecimal retailUnit = item.getMaterial().getRetailPrice().getPriceValue();
            BigDecimal qty = item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE;
            price = price.add(retailUnit.multiply(qty));
        }

        // Service rate component
        if (item.getService() != null && item.getServiceRate() != null) {
            String rateType = item.getService().getRateType();
            if ("HOURLY".equals(rateType) && item.getServiceHours() != null) {
                price = price.add(item.getServiceRate().multiply(item.getServiceHours()));
            } else if ("PER_UNIT".equals(rateType) && item.getQuantity() != null) {
                price = price.add(item.getServiceRate().multiply(item.getQuantity()));
            }
        }

        // Expenses = material cost price
        if (item.getMaterial() != null
                && item.getMaterial().getCostPrice() != null
                && item.getMaterial().getCostPrice().getPriceValue() != null) {
            expenses = item.getMaterial().getCostPrice().getPriceValue();
        }

        item.setPrice(price);
        item.setExpenses(expenses);
        item.setA$date$(Timestamp.from(Instant.now()));
        orderItemRepository.save(item);

        if (item.getOrder() != null) {
            billingService.recalculateForOrder(item.getOrder().getId_order());
        }

        return orderItemMapper.toDto(item);
    }

    @Transactional
    public void saveOrderItemDTO(OrderItemDTO dto) {
        OrderItem orderItem = orderItemMapper.toEntity(dto);

        if (dto.getIdOrder() != null) {
            orderItem.setOrder(orderRepository.getReferenceById(dto.getIdOrder()));
        } else {
            orderItem.setOrder(null);
        }

        if (dto.getService() != null && dto.getService().getId_printing_service() != null) {
            orderItem.setService(printingServiceRepository.getReferenceById(dto.getService().getId_printing_service()));
        } else {
            orderItem.setService(null);
        }

        if (dto.getMaterial() != null && dto.getMaterial().getIdMaterial() != null) {
            orderItem.setMaterial(materialRepository.getReferenceById(dto.getMaterial().getIdMaterial()));
        } else {
            orderItem.setMaterial(null);
        }

        orderItem.setA$date$(Timestamp.from(Instant.now()));
        orderItemRepository.save(orderItem);

        if (dto.getIdOrder() != null) {
            billingService.recalculateForOrder(dto.getIdOrder());
        }
    }
}
