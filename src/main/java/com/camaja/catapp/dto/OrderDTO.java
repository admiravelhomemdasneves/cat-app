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
    private Address billAddress;
    private BigDecimal vatRate;
    private BigDecimal vatAmount;
    private BigDecimal expensesAmount;
    private BigDecimal serviceAmount;
    private BigDecimal totalAmount;
    private Boolean flagActive;
}
