package com.product.service;


import com.product.exception.ApiRequestException;
import com.product.dto.Discount;
import com.product.dto.Product;
import com.product.dto.AmountToBePaid;
import com.product.dto.ProductBuyed;
import com.product.utils.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Slf4j
public class ProductService {

    private HashMap<String, Product> allProducts = new HashMap<>();

    public void setPriceToProduct(BigDecimal price, String skus) {
        log.info("setPriceToProduct-RUN");
        checkProductExists(new String[]{skus});

        if (Objects.isNull(price) || (price.compareTo(ServiceUtil.bigDecimalZero) != 1)) {
            String messageError = "Price is mandatory, need to be more than zero";
            log.error("setPriceToProduct-ParamInput: {}", messageError);
            throw new ApiRequestException(messageError);
        }
        allProducts.get(skus).setUnitPrice(price);
        log.info("setPriceToProduct- new price for skus: {} is: {}", skus, price);
    }

    public void addProduct(Product product) {
        log.info("addProduct-RUN");
        chechFieldsProduct(product);

        if (allProducts.containsKey(product.getSkus())) {
            String messageError = "Exist product with SKUS: " + product.getSkus();
            log.error("addProduct-Error: {}", messageError);
            throw new ApiRequestException(messageError);
        }
        allProducts.put(product.getSkus(), product);
        addDiscountForProduct(product.getDiscount(), product.getSkus() );
        log.info( "added product with SKUS: {}", product.getSkus() );
    }

    public void addDiscountForProduct(Discount discount, String skus) {
        log.info( "addDiscountForProduct-RUN" );
        if(Objects.isNull(discount) ||
            Objects.isNull(discount.getNrOfProducts()) ||
            Objects.isNull(discount.getSpecialPrice()) ||
            Objects.isNull(discount.getStartDateStr()) ||
            Objects.isNull(discount.getForNrDays())){

            // I'm in this situation when I want to enter the product but I don't care about the discount
            // the call is made when the product is created
            log.info( "addDiscountForProduct- discount not created, no data present!" );
            return;
        }
        checkFiledsDiscount(discount);
        checkProductExists(new String[] {skus} );

        discount.setStartDate( ServiceUtil.getDateByStringDate( discount.getStartDateStr() ) );
        discount.setEndDate( ServiceUtil.addDaysToDate( discount.getStartDate(), discount.getForNrDays()  ) );
        // add discount in existing product
        allProducts.get(skus).setDiscount( discount );
        log.info( "addDiscountForProduct - Added Discount" );
    }


    public Product getProduct(String skus) {
        log.info( "getProduct-RUN" );
        checkProductExists(new String[] {skus});

        Product product = allProducts.get(skus);
        log.info( "getProduct-end Product: {}" + product  );
        return product;
    }



    private void checkProductExists(String[] skusList) {
      for(String skus : skusList) {
          if (!allProducts.containsKey(skus)) {
              String messageError = "Not exist product with SKUS: " + skus;
              log.error("addSpecialPriceForPeriod-Error: {}", messageError);
              throw new ApiRequestException(messageError);
          }
       }
    }

    /**
     * method "getCashier" the method calculates all the products received as parameters A, B, C, D if a certain
     * product has the discount ex: "D" ( You have 2 products for 10p ) will be applied whenever this occurs,
     * so if I entered A, D, D, D, D, D (D has the discount) the discount will be
     * applied twice and once the full price is paid, the same goes for all discounted products
     * But the discount is only valid if it is within the range of the discount period
     *
     * DataStart and Days
     *
     * @param listSkus
     * @return
     */
    public AmountToBePaid getCashier(String[] listSkus ) {

        AmountToBePaid amountToBePaid = new AmountToBePaid();
        log.info( "getCashier-RUN skus: {}", listSkus );
         checkProductExists(listSkus);
        amountToBePaid.getTotalAmount().setScale(2, RoundingMode.HALF_UP);

        for(String skus : listSkus){
            Product product =  allProducts.get( skus );
            product.getUnitPrice().setScale(2, RoundingMode.HALF_UP);

            if(!amountToBePaid.getProductBuyed().containsKey(skus) ) {
                ProductBuyed productResponse = ProductBuyed.builder()
                                .skus( skus )
                                .description( product.getDescription() )
                                .price( product.getUnitPrice() )
                                .quantity( new AtomicInteger(1)  )
                                .build();
                amountToBePaid.getProductBuyed().put(skus, productResponse );
                amountToBePaid.setTotalAmount( amountToBePaid.getTotalAmount().add( product.getUnitPrice() ) );
                log.info( " TotalAmount: {}", amountToBePaid.getTotalAmount() );
            }else{
                amountToBePaid.getProductBuyed().get(skus).getQuantity().incrementAndGet();
                amountToBePaid.setTotalAmount( amountToBePaid.getTotalAmount().add( product.getUnitPrice() ) );
                ProductBuyed amountProduct = amountToBePaid.getProductBuyed().get(skus);

                if( product.discountIsApplicable( new Date() ) &&
                    product.getDiscount().getNrOfProducts().intValue() == amountProduct.getQuantity().intValue()  ){
                    log.info( " Discount product:  {}", product.getDiscount() );
                    // remove previous amount, because this product has discount for a specific number articles
                    BigDecimal removeAmount = product.getUnitPrice().multiply( new BigDecimal(amountProduct.getQuantity().intValue()).setScale(2, RoundingMode.HALF_UP) );
                    amountToBePaid.setTotalAmount( amountToBePaid.getTotalAmount().subtract ( removeAmount ) );
                    //add new amount with Discount
                    amountToBePaid.setTotalAmount( amountToBePaid.getTotalAmount().add( product.getDiscount().getSpecialPrice().setScale(2, RoundingMode.HALF_UP ) ) );
                    // This product is already discounted, I change them the key not to consider
                    // it if there are other products with the same discount
                    amountProduct.setQuantity( new AtomicInteger(product.getDiscount().getNrOfProducts()) );
                    amountProduct.setPrice( product.getDiscount().getSpecialPrice() );
                    amountProduct.setDescription( product.getDescription() +
                                           ", Discounted product, Number of products : " +
                                           product.getDiscount().getNrOfProducts() +" Price: " + product.getDiscount().getSpecialPrice() );
                    amountToBePaid.getProductBuyed().put(  createDiscountedProductKey(amountToBePaid.getProductBuyed(),skus ) , amountProduct );
                    // remove the previous key, because I can have other products with the same key (but not discounted)
                    amountToBePaid.getProductBuyed().remove( skus );
                }
                log.info( " TotalAmount: {}", amountToBePaid.getTotalAmount() );
            }
        }
        return amountToBePaid;
    }

    public String createDiscountedProductKey(HashMap<String , ProductBuyed> mapCashier, String skus ) {
        String DISCOUNT = "_Discount";
        int nrOfElements = 1;
        for (Map.Entry<String, ProductBuyed> element : mapCashier.entrySet()) {
            if(element.getKey().startsWith(skus+DISCOUNT)){
                nrOfElements++;
            }
        }
        return skus + DISCOUNT + nrOfElements;
    }

    private void chechFieldsProduct(Product product) {
        if(Objects.isNull(product.getSkus()) || "".equals(product.getSkus()) ||
            Objects.isNull(product.getDescription()) || "".equals(product.getDescription()) ||
            Objects.isNull(product.getUnitPrice() ) || (product.getUnitPrice().compareTo(ServiceUtil.bigDecimalZero) != 1) ||
            Objects.isNull(product.getUnitPrice() ) || (product.getUnitPrice().compareTo(ServiceUtil.bigDecimalZero) != 1)  ){

            String messageError = "All the filed are mandatory, discount is optional ";
            log.error( "addProduct-ParamInput: {}", messageError );
            throw new ApiRequestException( messageError );
        }
    }

    private void checkFiledsDiscount(Discount discount) {
        if(Objects.isNull(discount.getNrOfProducts() ) || discount.getNrOfProducts().intValue() < 2 ||
          Objects.isNull(discount.getSpecialPrice()) || "".equals(discount.getSpecialPrice()) ||
          Objects.isNull(discount.getStartDateStr() ) || "".equals(discount.getStartDateStr()) ||
          Objects.isNull(discount.getForNrDays() ) || discount.getForNrDays().intValue() < 1  ){

            String messageError = "All the filed are mandatory, ForNrDays minimum: 1, NrOfProducts minimum 2";
            log.error( "checkFiledsDiscount-ParamInput: {}", messageError );
            throw new ApiRequestException( messageError );
        }

    }


    public HashMap<String, Product> getAllProducts() {
        return allProducts;
    }
}