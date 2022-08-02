package com.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice(annotations = RestController.class)
//@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler( value = {ApiRequestException.class} )
    public final ResponseEntity<Object> handleApiRequestException(ApiRequestException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiException apiException =  new ApiException(
                e.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException,badRequest);
    }

    @ExceptionHandler ( Exception.class )
    public final ResponseEntity<Object> handleAllExceptions( Exception e )
    {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiException apiException =  new ApiException(
                e.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException,badRequest);
    }

}
