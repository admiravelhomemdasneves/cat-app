package com.camaja.catapp.mappers;

import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.models.PrintingService;
import com.camaja.catapp.models.ProductParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderItemMapper {
    private final ProductParameterMapper productParameterMapper;

    public OrderItemDTO toDto(OrderItem value) {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setIdOrderItem(value.getId_order_product());
        itemDTO.setIdOrder(value.getOrder().getId_order());
        itemDTO.setDescription(value.getDescription());
        itemDTO.setPrintingService(value.getPrintingService());
        itemDTO.setQuantity(value.getQuantity());
        itemDTO.setServiceHours(value.getServiceHours());
        itemDTO.setUnitPrice(value.getUnitPrice());
        itemDTO.setServiceHourlyRate(value.getServiceHourlyRate());
        itemDTO.setFlagActive(value.getFlagActive());

        if (value.getProduct() != null) {
            itemDTO.setProduct(productParameterMapper.toDto(value.getProduct()));
        }

        return itemDTO;
    }

    public OrderItem toEntity(OrderItemDTO value) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId_order_product(value.getIdOrderItem());
        orderItem.setDescription(value.getDescription());
        orderItem.setQuantity(value.getQuantity());
        orderItem.setServiceHours(value.getServiceHours());
        orderItem.setUnitPrice(value.getUnitPrice());
        orderItem.setServiceHourlyRate(value.getServiceHourlyRate());
        orderItem.setFlagActive(value.getFlagActive());

        if (value.getIdOrder() != null) {
            Order order = new Order();
            order.setId_order(value.getIdOrder());
            orderItem.setOrder(order);
        }

        if (value.getProduct() != null && value.getProduct().getIdProductParameter() != null) {
            ProductParameters productParameters = new ProductParameters();
            productParameters.setIdProductParameter(value.getProduct().getIdProductParameter());
            orderItem.setProduct(productParameters);
        }

        if (value.getPrintingService() != null && value.getPrintingService().getId_printing_service() != null) {
            PrintingService printingService = new PrintingService();
            printingService.setId_printing_service(value.getPrintingService().getId_printing_service());
            orderItem.setPrintingService(printingService);
        }

        return orderItem;
    }
}
