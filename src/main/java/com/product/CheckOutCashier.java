package com.product;

import com.product.dto.Discount;
import com.product.dto.Product;
import com.product.service.ProductService;
import com.product.utils.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.util.Date;

@ComponentScan( basePackages = { "com.product" } )
@SpringBootApplication
@Slf4j
public class CheckOutCashier {

	/**
     * Mihai Stefan
	 * @param args˙
	 */
	public static void main(String[] args) {
		SpringApplication.run(CheckOutCashier.class, args);
	}

	@Bean
	CommandLineRunner run(ProductService productService) {
		log.info("Add some products in memory ");
		return args -> {

			// Add product without special price
			Product cookies = Product.builder()
							.skus("A")
							.description("Cookies")
							.unitPrice(new BigDecimal(2.22))
							.build();

			Product shoes = Product.builder()
					.skus("B")
					.description("Shoes")
					.unitPrice(new BigDecimal(34.22))
					.build();

			productService.addProduct( cookies );
			productService.addProduct( shoes );


			// This product is with discount
			Discount eggDiscount =  Discount.builder()
					.nrOfProducts(  Integer.parseInt ("4") )
					.specialPrice(  new BigDecimal (8 )  )
					.startDateStr( ServiceUtil.getStrDateByDate( new Date() )  )
					.forNrDays( Integer.parseInt( "7") ) // for 1 week
					.build();

			Product egg = Product.builder()
					.skus("C")
					.description("egg")
					.unitPrice(new BigDecimal(2.50))
					.discount( eggDiscount  )
					.build();

			productService.addProduct( egg );

			Discount glovesDiscount =  Discount.builder()
					.nrOfProducts(  Integer.parseInt ("2")  )
					.specialPrice(  new BigDecimal (17 )  )
					.startDateStr( "03/08/2022" )   // ServiceUtil.getStrDateByDate( new Date() )
					.endDate( ServiceUtil.addDaysToDate( new Date(),7 ) )
					.forNrDays( Integer.parseInt( "7") )// for 1 week
					.build();

			Product gloves = Product.builder()
					.skus("D")
					.description("gloves")
					.unitPrice(new BigDecimal(10))
					.discount( glovesDiscount  )
					.build();

			productService.addProduct( gloves );
	   };
	}

}
