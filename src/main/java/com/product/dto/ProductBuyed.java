package com.product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Builder
public class ProductBuyed {

    private String        skus;
    private BigDecimal    price;
    private AtomicInteger quantity;
    private String        description;


}
