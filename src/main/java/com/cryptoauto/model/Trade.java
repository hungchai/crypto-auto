package com.cryptoauto.model;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Entity
@Table(name = "trade")
@Data // Lombok: adds getters and setters
@Builder
public class Trade {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

	@NonNull
	private String tradeId;

	private String makerOrderId;

	private String takerOrderId;

	private String type;

	private BigDecimal originalAmount;
	
	@NonNull
	private BigDecimal price;

	@NonNull
	private String instrument;

	@NonNull
	private Instant tradeTimestamp;

	@NonNull
	@CreationTimestamp
	private Instant createTimestamp;

	@NonNull
	@UpdateTimestamp
	private Instant updateTimestamp;
}
