package com.dailycodebuffer.OrderService.controller;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.lang.CharSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

import com.dailycodebuffer.OrderService.OrderServiceConfig;
import com.dailycodebuffer.OrderService.entity.Order;
import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.model.OrderResponse;
import com.dailycodebuffer.OrderService.model.PaymentMode;
import com.dailycodebuffer.OrderService.repository.OrderRepository;
import com.dailycodebuffer.OrderService.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes= {OrderServiceConfig.class})
public class OrderControllerTest {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private MockMvc mockMvc;
	
	@RegisterExtension
	static WireMockExtension wireMockServer
	                         = WireMockExtension.newInstance()
	                         .options(WireMockConfiguration
	                        .wireMockConfig()
	                        .port(8080))
	                         .build();
	
	private ObjectMapper objectMapper
	                   =new ObjectMapper()
	                   .findAndRegisterModules()
	                   .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	@BeforeEach
	void setup() throws IOException   {
		getProductDetailsResponse();
		doPayment();
		getPaymentDetails();
		reduceQuantity();
		
	}
	
	private void reduceQuantity() {
		
		wireMockServer.stubFor(WireMock.put(WireMock.urlMatching("/product/reduceQuantity/.*"))
				.willReturn(WireMock.aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type",
								MediaType.APPLICATION_JSON_VALUE)));
	
		
	}

	private void getPaymentDetails() throws IOException {
		wireMockServer.stubFor(WireMock.get(WireMock.urlMatching("/payment/.*"))
				.willReturn(WireMock.aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type",
								MediaType.APPLICATION_JSON_VALUE)
						.withBody(
								StreamUtils.copyToString(
										OrderControllerTest.class
										.getClassLoader()
										.getResourceAsStream("Mock/GetPayment.json"),
										Charset.defaultCharset()
										)
								)));

		
	}

	

	private void doPayment() {
		
		wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/payment"))
				.willReturn(WireMock.aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type",
								MediaType.APPLICATION_JSON_VALUE)));

		
	}

	private void getProductDetailsResponse() throws IOException {
		//GET/product/1
		
		wireMockServer.stubFor(WireMock.get("/product/1")
				.willReturn(WireMock.aResponse()
				.withStatus(HttpStatus.OK.value())
				.withHeader("Content-Type",MediaType.APPLICATION_JSON_VALUE)
				.withBody(StreamUtils.copyToString(OrderControllerTest.class
						.getClassLoader()
						.getResourceAsStream("Mock/GetProduct.json"),
						Charset.defaultCharset()
						))));
				
					
		

		
	}

	@Test
	public void test_whenPlaceOrder_DoPayment_Success() throws  Exception {
		//First Place Order
		//Get Order By Order Id from Db and check
		//Check Output
		
		OrderRequest orderRequest= getMockOrderRequest();
		MvcResult mvcResult
		  =  mockMvc.perform(MockMvcRequestBuilders.post("/order/placeOrder")
				  .with(SecurityMockMvcRequestPostProcessors.jwt()
			      .authorities(new SimpleGrantedAuthority("Customer")))
				  .contentType(MediaType.APPLICATION_JSON_VALUE)
				  .content(objectMapper.writeValueAsString(orderRequest))
				  ).andExpect(MockMvcResultMatchers.status().isOk())
		           .andReturn();
		
		String orderId=mvcResult.getResponse().getContentAsString();
		
		Optional<Order> order= orderRepository.findById(Long.valueOf(orderId));
		
		Assertions.assertTrue(order.isPresent());
		
		Order o=order.get();
		assertEquals(Long.parseLong(orderId),o.getId());
		assertEquals("PLACED",o.getOrderStatus());
		assertEquals(orderRequest.getTotalAmount(),o.getAmount());
		assertEquals(orderRequest.getQuantity(),o.getQuantity());
	}

	private OrderRequest getMockOrderRequest() {
	
		return OrderRequest.builder()
				.productId(1)
		        .paymentMode(PaymentMode.CASH)
		        .quantity(200)
		        .totalAmount(200)
				.build();
	}
	
	
	public void test_WhenPlacedOrderWithWrongAccess_thenthrows403() throws JsonProcessingException, Exception {
		
		OrderRequest orderRequest= getMockOrderRequest();
		MvcResult mvcResult
		  =  mockMvc.perform(MockMvcRequestBuilders.post("/order/placeOrder")
				  .with(SecurityMockMvcRequestPostProcessors.jwt()
			      .authorities(new SimpleGrantedAuthority("Admin")))
				  .contentType(MediaType.APPLICATION_JSON_VALUE)
				  .content(objectMapper.writeValueAsString(orderRequest))
				  ).andExpect(MockMvcResultMatchers.status().isForbidden())
		           .andReturn();
		
		
		
	}
	
	@Test
	public void test_WhenGetOrder_Success() throws Exception {
		
		MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/order/1")
				.with(SecurityMockMvcRequestPostProcessors.jwt()
					      .authorities(new SimpleGrantedAuthority("Admin")))
						  .contentType(MediaType.APPLICATION_JSON_VALUE))
				           .andExpect(MockMvcResultMatchers.status().isForbidden())
				           .andReturn();
		
		
		String actualResponse=mvcResult.getResponse().getContentAsString();
		Order order= orderRepository.findById(1L).get();
		String expectedResponse=getOrderResponse(order);
		
		assertEquals(expectedResponse,actualResponse);
				
	}
	
	@Test
	public void testWhen_GetOrder_Order_NotFound() throws Exception {
		
		MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/order/2")
				.with(SecurityMockMvcRequestPostProcessors.jwt()
					      .authorities(new SimpleGrantedAuthority("Admin")))
						  .contentType(MediaType.APPLICATION_JSON_VALUE))
				           .andExpect(MockMvcResultMatchers.status().isNotFound())
				           .andReturn();
		
	}

	private String getOrderResponse(Order order) throws JsonMappingException, JsonProcessingException, IOException {
		OrderResponse.PaymentDetails paymentDetails
		              =objectMapper.readValue
		              (StreamUtils.copyToString
		            	         (OrderControllerTest.class.getClassLoader()
		            	        		 .getResourceAsStream("Mock/GetPayment.json"),
		            	        		 Charset.defaultCharset()),
		            	        		 OrderResponse.PaymentDetails.class);
		
		paymentDetails.setPaymentStatus("SUCCESS");		
		
		OrderResponse.ProductDetails productDetails
		     =objectMapper.readValue
		     (StreamUtils.copyToString
		    		 (OrderControllerTest.class.getClassLoader()
		    				 .getResourceAsStream("Mock/GetProduct.json"),
		    				 Charset.defaultCharset()
		    				 ),OrderResponse.ProductDetails.class);
		
		OrderResponse orderResponse
		              =OrderResponse.builder()
		              .paymentDetails(paymentDetails)
		              .productDetails(productDetails)
		              .orderStatus(order.getOrderStatus())
		              .orderDate(order.getOrderDate())
		              .amount(order.getAmount())
		              .orderId(order.getId())
		              .build();
		
		return objectMapper.writeValueAsString(orderResponse);
		            	        		 
		  
	}

}
