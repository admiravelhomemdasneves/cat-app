package com.camaja.catapp.dto;

import com.camaja.catapp.models.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Long idOrder;
    private String description;
    private Status status;
    private ContactDTO contact;
    private Priority priority;
    private List<OrderItemDTO> orderContents;
    private Timestamp dateRequested;
    private Timestamp dateStatus;
    private Timestamp dateConclusion;
    private Timestamp dateDue;
    private Address deliveryAddress;
    private Long idOrderBill;
    private String billAddress;
    private String nif;
    private BigDecimal vatRate;
    private BigDecimal totalAmount;
    private Boolean flagActive;
}
