package com.dailycodebuffer.ProductService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.dailycodebuffer.ProductService.model.ErrorResponse;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{

	
	@ExceptionHandler(ProductServiceCustomException.class)
	public ResponseEntity<ErrorResponse> handleProductServiceException( ProductServiceCustomException exception){
		
		
		return new ResponseEntity<>(new ErrorResponse().builder().errormessage(exception.getMessage())
				.errorcode(exception.getErrorCode()).build(),HttpStatus.NOT_FOUND);
		
	}
	
}
