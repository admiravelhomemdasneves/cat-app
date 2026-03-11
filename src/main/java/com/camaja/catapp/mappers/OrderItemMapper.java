package com.camaja.catapp.mappers;

import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.models.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {
    @Autowired ProductParameterMapper productParameterMapper;

    public OrderItemDTO toDto(OrderItem value) {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setIdOrderItem(value.getId_order_product());
        itemDTO.setIdOrder(value.getOrder().getId_order());
        itemDTO.setDescription(value.getDescription());
        itemDTO.setPrintingService(value.getPrintingService());
        itemDTO.setQuantity(value.getQuantity());
        itemDTO.setFlagActive(value.getFlagActive());

        if (value.getProduct() != null) {
            itemDTO.setProduct(productParameterMapper.toDto(value.getProduct()));
        }

        return itemDTO;
    }
}
