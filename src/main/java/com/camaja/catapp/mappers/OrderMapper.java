package com.camaja.catapp.mappers;

import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.models.Contact;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.models.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {
    @Autowired ContactMapper contactMapper;
    @Autowired OrderItemMapper orderItemMapper;

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
        orderDTO.setBillAddress(value.getBill_address());
        orderDTO.setExpensesPrice(value.getExpensesPrice());
        orderDTO.setServicePrice(value.getServicePrice());
        orderDTO.setTotalPrice(value.getTotalPrice());
        orderDTO.setVat(value.getVat());
        orderDTO.setFlagActive(value.getFlagActive());

        if (value.getContact() != null) {
            ContactDTO contactDTO = contactMapper.toDTO(value.getContact());
            orderDTO.setContact(contactDTO);
        }

        List<OrderItemDTO> orderItemDTOS = value.getOrderItems().stream().map(orderItemMapper::toDto).toList();
        orderDTO.setOrderItems(orderItemDTOS);

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
        order.setBill_address(value.getBillAddress());
        order.setExpensesPrice(value.getExpensesPrice());
        order.setServicePrice(value.getServicePrice());
        order.setTotalPrice(value.getTotalPrice());
        order.setVat(value.getVat());
        order.setFlagActive(value.getFlagActive());

        if (value.getContact() != null) {
            Contact contact = contactMapper.toEntity(value.getContact());
            order.setContact(contact);
        }

        List<OrderItem> orderItemList = new ArrayList<>();

        if (!value.getOrderItems().isEmpty()) {
            value.getOrderItems().forEach(orderItem -> orderItemList.add(orderItemMapper.toEntity(orderItem)));
        }

        order.setOrderItems(orderItemList);

        return order;
    }
}
