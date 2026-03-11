package com.camaja.catapp.mappers;

import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    @Autowired ContactMapper contactMapper;
    @Autowired OrderItemMapper orderItemMapper;

    public OrderDTO toDto(Order value) {
        List<OrderItemDTO> orderItemDTOS = value.getOrderItems().stream().map(orderItemMapper::toDto).toList();
        ContactDTO contactDTO = contactMapper.toDTO(value.getContact());

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setIdOrder(value.getId_order());
        orderDTO.setDescription(value.getDescription());
        orderDTO.setStatus(value.getStatus());
        orderDTO.setContact(contactDTO);
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
        orderDTO.setOrderItems(orderItemDTOS);

        return orderDTO;
    }
}
