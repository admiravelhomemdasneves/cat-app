package com.camaja.catapp.dto;

import com.camaja.catapp.models.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactDTO {
    private Long idContact;
    private String firstName;
    private String lastName;
    private String nif;
    private String phoneNumber;
    private String email;
    private Address shippingAddress;
    private Address billingAddress;
    private Boolean flagActive;
}
