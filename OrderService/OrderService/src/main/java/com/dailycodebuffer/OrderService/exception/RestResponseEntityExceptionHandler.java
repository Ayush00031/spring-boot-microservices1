package com.dailycodebuffer.OrderService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{

	
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<com.dailycodebuffer.OrderService.external.response.ErrorResponse> handleCustomException( CustomException exception){
		
		
		return new ResponseEntity<>(new com.dailycodebuffer.OrderService.external.response.ErrorResponse().builder().errormessage(exception.getMessage())
				.errorcode(exception.getErrorCode()).build(),HttpStatus.valueOf(exception.getStatus()));
		
	}
	
}
