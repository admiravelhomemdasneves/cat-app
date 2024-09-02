package com.camaja.catapp.dto;

import com.camaja.catapp.models.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteOrderDTO {
    private Order order;
    private Iterable<CompleteOrderProductDTO> orderProducts;

    public CompleteOrderDTO(Order order, Iterable<CompleteOrderProductDTO> orderProducts) {
        this.order = order;
        this.orderProducts = orderProducts;
    }
}
