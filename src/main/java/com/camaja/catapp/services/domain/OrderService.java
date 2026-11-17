package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.mappers.OrderItemMapper;
import com.camaja.catapp.mappers.OrderMapper;
import com.camaja.catapp.models.Address;
import com.camaja.catapp.models.Company;
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
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final TenantAccess tenantAccess;
    private final BillingService billingService;

    public List<Order> getActiveOrders() {
        List<Order> activeOrders = orderRepository.findByCompanyAndFlagActive(tenantAccess.company(), true);
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

    /** The order if it belongs to the current company, otherwise 404. */
    public Order getOwnedOrder(Long orderId) {
        return orderRepository.findOwned(orderId, tenantAccess.company())
                .orElseThrow(() -> TenantAccess.notFound("Order", orderId));
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderDTOById(Long orderId) {
        return orderMapper.toDto(getOwnedOrder(orderId));
    }

    @Transactional
    public OrderDTO inactivateOrder(Long orderId) {
        Order order = getOwnedOrder(orderId);
        order.setFlagActive(false);
        order.setA$date$(Timestamp.from(Instant.now()));
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /** The order item if its order belongs to the current company, otherwise 404. */
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findOwned(id, tenantAccess.company())
                .orElseThrow(() -> TenantAccess.notFound("Order item", id));
    }

    @Transactional
    public OrderItemDTO inactivateOrderItem(Long id) {
        OrderItem orderItem = getOrderItemById(id);
        orderItem.setFlagActive(false);
        orderItem.setA$date$(Timestamp.from(Instant.now()));
        orderItemRepository.save(orderItem);
        return orderItemMapper.toDto(orderItem);
    }

    @Transactional
    public void saveOrderDTO(OrderDTO orderDTO) {
        boolean isNewOrder = orderDTO.getIdOrder() == null;
        if (!isNewOrder) {
            getOwnedOrder(orderDTO.getIdOrder());
        }
        Company company = tenantAccess.company();
        Order order = orderMapper.toEntity(orderDTO);

        // Detach the bill from the Order entity — managed separately below to avoid
        // the cascade replacing updated values with an unmodified proxy.
        order.setOrderBill(null);

        // Every referenced record must be visible to the current company (global or its own).
        if (orderDTO.getPriority() != null && orderDTO.getPriority().getId_priority() != null) {
            Long id = orderDTO.getPriority().getId_priority();
            order.setPriority(priorityRepository.findVisible(id, company)
                    .orElseThrow(() -> TenantAccess.notFound("Priority", id)));
        } else {
            order.setPriority(null);
        }

        if (orderDTO.getStatus() != null && orderDTO.getStatus().getId_status() != null) {
            Long id = orderDTO.getStatus().getId_status();
            order.setStatus(statusRepository.findVisible(id, company)
                    .orElseThrow(() -> TenantAccess.notFound("Status", id)));
        } else {
            order.setStatus(null);
        }

        if (orderDTO.getContact() != null && orderDTO.getContact().getIdContact() != null) {
            Long id = orderDTO.getContact().getIdContact();
            order.setContact(contactRepository.findByIdContactAndCompany(id, company)
                    .orElseThrow(() -> TenantAccess.notFound("Contact", id)));
        } else {
            order.setContact(null);
        }

        Address deliveryAddress = orderDTO.getDeliveryAddress();
        if (deliveryAddress == null && orderDTO.getContact() != null) {
            deliveryAddress = orderDTO.getContact().getShippingAddress();
        }
        if (deliveryAddress != null && deliveryAddress.getId_address() != null) {
            Long id = deliveryAddress.getId_address();
            order.setDelivery_address(addressRepository.findOwned(id, company)
                    .orElseThrow(() -> TenantAccess.notFound("Address", id)));
        } else {
            order.setDelivery_address(null);
        }

        if (order.getDate_requested() == null) {
            order.setDate_requested(Timestamp.from(Instant.now()));
        }

        order.setCompany(company);
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
        OrderItem item = getOrderItemById(id);

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
        // An item only belongs to a company through its order, so the order is mandatory.
        if (dto.getIdOrder() == null) {
            throw new IllegalArgumentException("idOrder is required");
        }
        if (dto.getIdOrderItem() != null) {
            getOrderItemById(dto.getIdOrderItem());
        }
        Company company = tenantAccess.company();
        OrderItem orderItem = orderItemMapper.toEntity(dto);
        orderItem.setOrder(getOwnedOrder(dto.getIdOrder()));

        if (dto.getService() != null && dto.getService().getId_printing_service() != null) {
            Long id = dto.getService().getId_printing_service();
            orderItem.setService(printingServiceRepository.findVisible(id, company)
                    .orElseThrow(() -> TenantAccess.notFound("Printing service", id)));
        } else {
            orderItem.setService(null);
        }

        if (dto.getMaterial() != null && dto.getMaterial().getIdMaterial() != null) {
            Long id = dto.getMaterial().getIdMaterial();
            orderItem.setMaterial(materialRepository.findByIdMaterialAndCompany(id, company)
                    .orElseThrow(() -> TenantAccess.notFound("Material", id)));
        } else {
            orderItem.setMaterial(null);
        }

        orderItem.setA$date$(Timestamp.from(Instant.now()));
        orderItemRepository.save(orderItem);

        billingService.recalculateForOrder(dto.getIdOrder());
    }
}
