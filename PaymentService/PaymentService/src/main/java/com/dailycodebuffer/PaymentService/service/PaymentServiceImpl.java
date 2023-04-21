package com.dailycodebuffer.PaymentService.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dailycodebuffer.PaymentService.entity.TransactionDetails;
import com.dailycodebuffer.PaymentService.model.PaymentMode;
import com.dailycodebuffer.PaymentService.model.PaymentRequest;
import com.dailycodebuffer.PaymentService.model.PaymentResponse;
import com.dailycodebuffer.PaymentService.repository.TransactionDetailsRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService{

	@Autowired
	private TransactionDetailsRepository transactionsDetailsRepository;
	
	@Override
	public long doPayment(PaymentRequest paymentRequest) {
		log.info("Recording Payment Details:{}",paymentRequest);
		
		TransactionDetails transactionsDetails=
				TransactionDetails.builder()
				.paymentDate(Instant.now())
				.paymentMode(paymentRequest.getPaymentMode().name())
				.paymentStatus("SUCESS")
				.orderId(paymentRequest.getOrderId())
				.refrenceNumber(paymentRequest.getRefrenceNumber())
				.amount(paymentRequest.getAmount())
				.build();
		
		transactionsDetailsRepository.save(transactionsDetails);
		log.info("Transactoin Completed with id:{}",transactionsDetails.getId());
		
		return transactionsDetails.getId();
	}

	@Override
	public PaymentResponse getPaymentDetailsByOrderId(String orderId) {
	
		log.info("Getting Payment Details for the Order Id:{}",orderId);
		
		TransactionDetails transactionDetails=transactionsDetailsRepository.findByOrderId(Long.valueOf(orderId));
		
		PaymentResponse paymentResponse=PaymentResponse.builder()
				.paymentId(transactionDetails.getId())
				.paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
				.paymentDate(transactionDetails.getPaymentDate())
				.orderId(transactionDetails.getOrderId())
				.status(transactionDetails.getPaymentStatus())
				.amount(transactionDetails.getAmount())
				.build();
		
		return paymentResponse;
	}

}
