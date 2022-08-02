package com.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmountToBePaid {

    private BigDecimal totalAmount = BigDecimal.ZERO;
    private HashMap<String , ProductBuyed> productBuyed = new HashMap<>();
}
