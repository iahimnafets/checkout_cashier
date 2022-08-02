package com.product.controller;


import com.product.dto.Response;
import com.product.dto.Discount;
import com.product.dto.Product;
import com.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping( "/api/product" )
public class ProductController
{

    private final ProductService productService;


    @Operation( summary =  "Add new Product" )
    @PutMapping( value = "/add/product" )
    public ResponseEntity<Response> addProduct( @Valid
             @RequestBody final Product productRequest){

        productService.addProduct(productRequest);

        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .status(HttpStatus.OK)
                        .message("Product added correctly!")
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }
    @Operation( summary =  "Update price for product!" )
    @PutMapping( value = "/update/price" )
    public ResponseEntity<Response> addSpecialPriceForPeriod ( @RequestParam( name = "pricePerUnit" , required = true ) final BigDecimal pricePerUnit,
                                                               @RequestParam( name = "skus" , required = true ) final String skus ){

        productService.setPriceToProduct( pricePerUnit,  skus );

        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .status(HttpStatus.OK)
                        .message("The price for product was updated correctly")
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @Operation( summary =  "Update product ( with discount ) for a certain period with a special price, startDateStr format is: dd/MM/yyyy " )
    @PutMapping( value = "/add/discount" )
    public ResponseEntity<Response> addSpecialPriceForPeriod ( @RequestBody final Discount discount,
                                                               @RequestParam( name = "skus" , required = true ) final String skus ){

        productService.addDiscountForProduct( discount,  skus);

        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .status(HttpStatus.OK)
                        .message("Discount was inserted correctly for product:"+ skus )
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @Operation( summary =  "Input different articles for example: A, B, C, A, A, A, B " )
    @GetMapping( value = "/cashier" )
    public ResponseEntity<Response> cashier(@RequestParam( name = "skus" , required = true ) final String... skus) {

        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .status(HttpStatus.OK)
                        .data(Map.of("cashier", productService.getCashier(skus) ))
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @Operation( summary =  "Return product with whole details " )
    @GetMapping( value = "/detail" )
    public ResponseEntity<Response> getProduct(@RequestParam( name = "skus" , required = true ) final String skus) {

        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .status(HttpStatus.OK)
                        .data(Map.of("product", productService.getProduct(skus) ))
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }


}
