package com.saranaresturantsystem.entities.sales;

import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.entities.catalog.ProductSerials;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tbl_sale_items")
public class SaleItems  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;
    @Column(nullable = false, precision = 25, scale = 4)
    private BigDecimal price;
    @Column(name = "item_discount", precision = 25, scale = 4)
    private BigDecimal itemDiscount;
    @Column(name = "sub_total")
    private BigDecimal subTotal;
    @ElementCollection
    @CollectionTable(
            name = "tbl_sale_item_serials",
            joinColumns = @JoinColumn(name = "sale_item_id")
    )
    @Column(name = "product_serial_id")
    private List<Long> productSerialIds;
    @ManyToOne
    @JoinColumn(name = "sales_id",nullable = false)
    private Sales sales;
}
