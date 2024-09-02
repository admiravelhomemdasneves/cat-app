package com.camaja.catapp.dto;

import com.camaja.catapp.models.OrderProduct;
import com.camaja.catapp.models.OrderProductSpecs;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteOrderProductDTO {
    private OrderProduct orderProduct;
    private Iterable<OrderProductSpecs> orderProductSpecs;

    public CompleteOrderProductDTO(OrderProduct orderProduct, Iterable<OrderProductSpecs> orderProductSpecs) {
        this.orderProduct = orderProduct;
        this.orderProductSpecs = orderProductSpecs;
    }
}
