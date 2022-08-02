package com.product.service;


    //public class ProductServiceTest {
    //private ProductService productService;

import com.product.dto.AmountToBePaid;
import com.product.dto.Discount;
import com.product.dto.Product;
import com.product.exception.ApiRequestException;
import com.product.utils.ServiceUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.is;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class ProductServiceTest {

    @Spy
    @InjectMocks
    private ProductService productService;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void setUp() {
        populateProducts();
    }
    private void populateProducts() {
        Product cookies = Product.builder()
                .skus("A")
                .description("Cookies")
                .unitPrice(new BigDecimal(5))
                .build();

        Product candies = Product.builder()
                .skus("B")
                .description("candies")
                .unitPrice(new BigDecimal(5))
                .build();

        Discount toothpasteDiscount =  Discount.builder()
                .nrOfProducts(  Integer.parseInt ("2")  )
                .specialPrice(  new BigDecimal (5 )  )
                .startDate( new Date() )
                .endDate( ServiceUtil.addDaysToDate( new Date(),7 ) )
                .forNrDays( Integer.parseInt( "7") )// for 1 week
                .build();

        Product toothpaste = Product.builder()
                .skus("C")
                .description("toothpaste")
                .unitPrice(new BigDecimal(3))
                .discount( toothpasteDiscount )
                .build();

        productService.getAllProducts().put("A", cookies);
        productService.getAllProducts().put("B", candies);
        productService.getAllProducts().put("C", toothpaste);
    }


    @Test
    public void getCashier(){

        AmountToBePaid result = productService.getCashier( new String[] { "A", "B", "C", "C", "A", "C"  } );
        BigDecimal expectTotalAmount = new BigDecimal(23.00).setScale( 2, RoundingMode.HALF_UP);

        assertNotNull(result);
        assertEquals(result.getTotalAmount(), expectTotalAmount );
        assertEquals(result.getProductBuyed().get("A").getPrice(), new BigDecimal(5) );
        assertEquals(result.getProductBuyed().get("B").getQuantity().intValue(), new AtomicInteger(1).intValue() );
        assertEquals(result.getProductBuyed().get("C").getDescription(), "toothpaste" );

    }

    @Test(expected = ApiRequestException.class)
    public void getCashier_ProductNotFound () {

        productService.getCashier(  new String[] {"X", "Z", "Q" } );

        doThrow(ApiRequestException.class)
                .when(productService)
                .getCashier ( any(String[].class)  );
    }

    @Test
    public void addProduct() {
        Product product = Product.builder()
                .skus("F")
                .description("FFFFFFF")
                .unitPrice( new BigDecimal(2))
                .build();

        productService.addProduct(product);
        assertThat( productService.getAllProducts().get("F").getDescription() ,
                is( product.getDescription() ));
    }

    @Test
    public void addDiscountForProduct() {

        Product product = Product.builder()
                .skus("M")
                .description("MMMMMM")
                .unitPrice( new BigDecimal(2))
                .build();
        productService.getAllProducts().put("M", product );

        Discount discount = Discount.builder()
                .startDateStr("05/09/2022")
                .forNrDays(1)
                .nrOfProducts(3)
                .specialPrice( new BigDecimal (5) )
                .build();

        productService.addDiscountForProduct(discount, "M" );

        assertThat( productService.getAllProducts().get("M").getDiscount().getNrOfProducts().intValue() ,
                is( discount.getNrOfProducts().intValue() ));
    }


    @Test
    public void setPriceToProduct() {

        Product product = Product.builder()
                .skus("L")
                .description("LLLLLLL")
                .unitPrice( new BigDecimal(2))
                .build();
        productService.getAllProducts().put("L", product);

        productService.setPriceToProduct ( new BigDecimal(5) ,"L");

        assertThat( productService.getAllProducts().get("L").getUnitPrice() ,
                is( product.getUnitPrice() ));
    }

    @Test(expected = ApiRequestException.class)
    public void setPriceToProduct_PriceNotFound () {

        productService.setPriceToProduct ( new BigDecimal(0) ,"A");

        doThrow(ApiRequestException.class)
                .when(productService)
                .setPriceToProduct ( any(BigDecimal.class), any(String.class) );

    }


    @Test(expected = ApiRequestException.class)
    public void setPriceToProduct_ProductNotFound () {

        productService.setPriceToProduct ( new BigDecimal(3) ,"X");

        doThrow(ApiRequestException.class)
                .when(productService)
                .setPriceToProduct ( any(BigDecimal.class), any(String.class) );

    }

    @Test(expected = ApiRequestException.class)
    public void addProduct_ParamsMissing () {

        Product product = Product.builder()
                .description ("FFFFFFF")
                .unitPrice( new BigDecimal(2))
                .build();

        productService.addProduct ( product );

        doThrow(ApiRequestException.class)
                .when(productService)
                .addProduct ( any(Product.class)  );
    }

    @Test(expected = ApiRequestException.class)
    public void addDiscountForProduct_ProductNotFound () {

        Discount discount = Discount.builder()
                .startDateStr("05/09/2022")
                .forNrDays(1)
                .nrOfProducts(3)
                .specialPrice( new BigDecimal (5) )
                .build();

        productService.addDiscountForProduct( discount, "X" );

        doThrow(ApiRequestException.class)
                .when(productService)
                .addDiscountForProduct ( any(Discount.class) , any(String.class) );
    }


}
