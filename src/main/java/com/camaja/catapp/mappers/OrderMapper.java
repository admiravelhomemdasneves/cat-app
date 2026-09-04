package com.camaja.catapp.mappers;

import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.models.*;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderMapper {
    private final ContactMapper contactMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderDTO toDto(Order value) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setIdOrder(value.getId_order());
        orderDTO.setDescription(value.getDescription());
        orderDTO.setStatus(value.getStatus());
        orderDTO.setPriority(value.getPriority());
        orderDTO.setDateRequested(value.getDate_requested());
        orderDTO.setDateStatus(value.getDate_status());
        orderDTO.setDateConclusion(value.getDate_conclusion());
        orderDTO.setDateDue(value.getDate_due());
        orderDTO.setDeliveryAddress(value.getDelivery_address());
        orderDTO.setFlagActive(value.getFlagActive());

        if (value.getContact() != null) {
            ContactDTO contactDTO = contactMapper.toDTO(value.getContact());
            orderDTO.setContact(contactDTO);
        }

        if (value.getOrderBill() != null) {
            OrderBill orderBill = value.getOrderBill();
            orderDTO.setBillAddress(orderBill.getBill_address());
            orderDTO.setVatRate(orderBill.getVatRate());
            orderDTO.setVatAmount(orderBill.getVatAmount());
            orderDTO.setExpensesAmount(orderBill.getExpensesAmount());
            orderDTO.setServiceAmount(orderBill.getServiceAmount());
            orderDTO.setTotalAmount(orderBill.getTotalAmount());
        }

        List<OrderItemDTO> orderItemDTOs = value.getOrderContents() == null ? List.of()
                : value.getOrderContents().stream().map(orderItemMapper::toDto).toList();
        orderDTO.setOrderContents(orderItemDTOs);

        return orderDTO;
    }

    public Order toEntity(OrderDTO value) {
        Order order = new Order();
        order.setId_order(value.getIdOrder());
        order.setDescription(value.getDescription());
        order.setStatus(value.getStatus());
        order.setPriority(value.getPriority());
        order.setDate_requested(value.getDateRequested());
        order.setDate_status(value.getDateStatus());
        order.setDate_conclusion(value.getDateConclusion());
        order.setDate_due(value.getDateDue());
        order.setDelivery_address(value.getDeliveryAddress());
        order.setFlagActive(value.getFlagActive());

        OrderBill orderBill = getOrderBill(value);
        order.setOrderBill(orderBill);

        if (value.getContact() != null) {
            Contact contact = contactMapper.toEntity(value.getContact());
            order.setContact(contact);
        }

        return order;
    }

    @Nonnull
    private static OrderBill getOrderBill(OrderDTO value) {
        OrderBill orderBill = new OrderBill();
        if (value.getBillAddress() != null) orderBill.setBill_address(value.getBillAddress());
        if (value.getVatRate() != null) orderBill.setVatRate(value.getVatRate());
        if (value.getVatAmount() != null) orderBill.setVatAmount(value.getVatAmount());
        if (value.getExpensesAmount() != null) orderBill.setExpensesAmount(value.getExpensesAmount());
        if (value.getServiceAmount() != null) orderBill.setServiceAmount(value.getServiceAmount());
        if (value.getTotalAmount() != null) orderBill.setTotalAmount(value.getTotalAmount());
        return orderBill;
    }
}
