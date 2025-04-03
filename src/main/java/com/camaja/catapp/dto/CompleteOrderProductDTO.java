package com.camaja.catapp.dto;

import com.camaja.catapp.models.OrderProduct;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteOrderProductDTO {
    private OrderProduct orderProduct;

    public CompleteOrderProductDTO(OrderProduct orderProduct) {
        this.orderProduct = orderProduct;
    }
}
