package com.app.estore.ProductService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_lookup")
@Entity
public class ProductLookupEntity implements Serializable {
    @Id
    String productId;
    @Column(unique = true)
    String title;
}
