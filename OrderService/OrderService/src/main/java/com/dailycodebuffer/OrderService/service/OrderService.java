package com.dailycodebuffer.OrderService.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.model.OrderResponse;
import com.dailycodebuffer.OrderService.repository.OrderRepository;

public interface OrderService {
	


	long placeOrder(OrderRequest orderRequest);

	OrderResponse getOrderDetails(long orderId);

}
