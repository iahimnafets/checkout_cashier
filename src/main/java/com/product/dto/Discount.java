package com.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class Discount {

    private Integer nrOfProducts;
    private BigDecimal specialPrice;
    private String  startDateStr;
    private Integer forNrDays;

    @JsonIgnore
    private Date startDate;
    @JsonIgnore
    private Date  endDate;

}
