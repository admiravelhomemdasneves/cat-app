package com.camaja.catapp.mappers;

import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.models.Material;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.models.PrintingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderItemMapper {

    public OrderItemDTO toDto(OrderItem value) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setIdOrderItem(value.getIdOrderItem());
        dto.setDescription(value.getDescription());
        dto.setService(value.getService());
        dto.setMaterial(value.getMaterial());
        dto.setQuantity(value.getQuantity());
        dto.setServiceRate(value.getServiceRate());
        dto.setServiceHours(value.getServiceHours());
        dto.setExpenses(value.getExpenses());
        dto.setPrice(value.getPrice());
        dto.setFlagActive(value.getFlagActive());

        if (value.getOrder() != null) {
            dto.setIdOrder(value.getOrder().getId_order());
        }

        return dto;
    }

    public OrderItem toEntity(OrderItemDTO value) {
        OrderItem orderItem = new OrderItem();
        orderItem.setIdOrderItem(value.getIdOrderItem());
        orderItem.setDescription(value.getDescription());
        orderItem.setQuantity(value.getQuantity());
        orderItem.setServiceRate(value.getServiceRate());
        orderItem.setServiceHours(value.getServiceHours());
        orderItem.setExpenses(value.getExpenses());
        orderItem.setPrice(value.getPrice());
        orderItem.setFlagActive(value.getFlagActive());

        if (value.getIdOrder() != null) {
            Order order = new Order();
            order.setId_order(value.getIdOrder());
            orderItem.setOrder(order);
        }

        if (value.getService() != null && value.getService().getId_printing_service() != null) {
            PrintingService ps = new PrintingService();
            ps.setId_printing_service(value.getService().getId_printing_service());
            orderItem.setService(ps);
        }

        if (value.getMaterial() != null && value.getMaterial().getIdMaterial() != null) {
            Material m = new Material();
            m.setIdMaterial(value.getMaterial().getIdMaterial());
            orderItem.setMaterial(m);
        }

        return orderItem;
    }
}
