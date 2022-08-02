package com.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.product.utils.ServiceUtil;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class Product {

    private String skus;
    private String description;
    private BigDecimal unitPrice;

    private Discount discount;

    public Boolean discountIsApplicable(Date dateInput ){
        if(discount != null && ServiceUtil.isThisDateWithinDateRange(this.discount.getStartDate(),
                this.discount.getEndDate(),
                dateInput) ) {
            return true;
        }else{
            return false;
        }
    }

}
