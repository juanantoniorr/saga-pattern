package com.estore.paymentservice.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    private String paymentId;
    @Column
    public String orderId;

}
