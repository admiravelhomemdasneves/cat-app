package com.camaja.catapp.dto;

import com.camaja.catapp.models.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private List<OrderItemDTO> orderItems;
    private Timestamp dateRequested;
    private Timestamp dateStatus;
    private Timestamp dateConclusion;
    private Timestamp dateDue;
    private Address deliveryAddress;
    private Address billAddress;
    private Price expensesPrice;
    private Price servicePrice;
    private Price totalPrice;
    private Vat vat;
    private Boolean flagActive;
}
