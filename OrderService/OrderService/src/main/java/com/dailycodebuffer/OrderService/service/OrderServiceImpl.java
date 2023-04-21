package com.dailycodebuffer.OrderService.service;

import java.time.Instant;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dailycodebuffer.OrderService.entity.Order;
import com.dailycodebuffer.OrderService.exception.CustomException;
import com.dailycodebuffer.OrderService.external.client.PaymentService;
import com.dailycodebuffer.OrderService.external.client.ProductService;
import com.dailycodebuffer.OrderService.external.request.PaymentRequest;
import com.dailycodebuffer.OrderService.external.response.PaymentResponse;
import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.model.OrderResponse;
import com.dailycodebuffer.OrderService.model.OrderResponse.ProductDetails;
import com.dailycodebuffer.OrderService.repository.OrderRepository;
import com.dailycodebuffer.ProductService.model.ProductResponse;

import feign.Response;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public long placeOrder(OrderRequest orderRequest) {
		log.info("Placing Order Request:{}",orderRequest);
		
		productService.reduceQuantity(orderRequest.getProductId(),orderRequest.getQuantity() );
		log.info("Creating Order with Status CREATED");
		
		Order order=Order.builder()
				.amount(orderRequest.getTotalAmount())
				.orderStatus("CREATED")
				.productId(orderRequest.getProductId())
				.orderDate(Instant.now())
				.quantity(orderRequest.getQuantity()).build();
		order=orderRepository.save(order);
		
		log.info("Calling Payment Service to complete the Payment ");
		
		PaymentRequest paymentRequest=PaymentRequest.builder()
				.orderId(order.getId())
				.paymentMode(orderRequest.getPaymentMode())
				.amount(orderRequest.getTotalAmount())
				.build();
		
		String orderStatus =null;
		try {
			paymentService.doPayment(paymentRequest);
			log.info("Payment Done SuccessFully.Changing the Order Status to Placed");
			orderStatus="PLACED";
			
		}catch(Exception e) {
			log.error("Error Occured in Payment.Changing the Status to Payment Failed");
			orderStatus="Failed";
			
		}
		
		order.setOrderStatus(orderStatus);
		orderRepository.save(order);
		
		log.info("Order Place Successfully with Order Id:{}",order.getId());
		return order.getId();
	}

	@Override
	public OrderResponse getOrderDetails(long orderId) {
		log.info("Get Order Details for OrderId:{}",orderId);
		
		Order order=orderRepository.findById(orderId)
				.orElseThrow(()->new CustomException("Order not Found for the order id:"+orderId,
						"NOT_FOUND",404));
		
		log.info("Invoking Product Service to fetch the product for id: {}",order.getProductId());
		ProductResponse productResponse=restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(),ProductResponse.class);
		
		
		log.info("Getting Payment Information from the payment Service");
		PaymentResponse paymentResponse=restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/"+order.getId(),PaymentResponse.class);
		
		
		
		OrderResponse.ProductDetails productDetails=OrderResponse.ProductDetails.builder()
				.productName(productResponse.getProductName())
				.productId(productResponse.getProductId())
				.quantity(productResponse.getQuantity())
				.price(productResponse.getPrice())
				.build();
		
		OrderResponse.PaymentDetails paymentDetails=OrderResponse.PaymentDetails.builder()
				.paymentId(paymentResponse.getOrderId())
				.paymentStatus(paymentResponse.getStatus())
				.paymentDate(paymentResponse.getPaymentDate())
				.paymentMode(paymentResponse.getPaymentMode())
				.build();
		
		OrderResponse orderResponse=OrderResponse.builder()
				.orderId(order.getId())
				.orderStatus(order.getOrderStatus())
				.amount(order.getAmount())
				.orderDate(order.getOrderDate())
				.productDetails(productDetails)
				.paymentDetails(paymentDetails)
				.build();
		
		return orderResponse;
	}

}
