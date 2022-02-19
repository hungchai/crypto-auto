package com.cryptoauto.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "trade")
@Data // Lombok: adds getters and setters
@Builder
@AllArgsConstructor
@ToString
public class Trade {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	private String provider;

	@CreationTimestamp
	private Instant createDate;

	@UpdateTimestamp
	private Instant updateDate;
}
