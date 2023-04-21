package com.dailycodebuffer.OrderService.entity;

import java.time.Instant;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="ORDER_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@Column(name="PRODUCT_ID")
	private long productId;
	@Column(name="QUANTITY")
	private long quantity;
	@Column(name="ORDER_DATE")
	private Instant orderDate;
	@Column(name="ORDER_STATUS")
	private String orderStatus;
	@Column(name="TOTAL_AMOUNT")
	private long amount;
	

}
