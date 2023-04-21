package com.dailycodebuffer.OrderService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dailycodebuffer.OrderService.external.decoder.CustomErrorDecoder;

import feign.codec.ErrorDecoder;

@Configuration
public class FeignConfig {
	
	@Bean
	ErrorDecoder errorDecoder() {
		return new CustomErrorDecoder();
		
	}

}
