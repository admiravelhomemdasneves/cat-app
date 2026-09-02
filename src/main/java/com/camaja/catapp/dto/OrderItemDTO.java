package com.camaja.catapp.dto;

import com.camaja.catapp.models.PrintingService;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDTO {
    private Long idOrderItem;
    private Long idOrder;
    private String description;
    private PrintingService printingService;
    private Integer quantity;
    private Integer serviceHours;
    private BigDecimal unitPrice;
    private BigDecimal serviceRate;
    private ProductParameterDTO product;
    private Boolean flagActive;
}
