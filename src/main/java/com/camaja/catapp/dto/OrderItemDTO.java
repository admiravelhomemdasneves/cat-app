package com.camaja.catapp.dto;

import com.camaja.catapp.models.Material;
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
    private PrintingService service;
    private Material material;
    private BigDecimal quantity;
    private BigDecimal serviceRate;
    private BigDecimal serviceHours;
    private BigDecimal expenses;
    private BigDecimal price;
    private Boolean flagActive;
}
