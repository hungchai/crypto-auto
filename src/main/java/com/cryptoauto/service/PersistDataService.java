package com.cryptoauto.service;

import com.cryptoauto.model.Trade.TradeBuilder;
import com.cryptoauto.repository.TradeRepository;

import org.knowm.xchange.dto.marketdata.Trade;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

@Service
@AllArgsConstructor
@Slf4j
public class PersistDataService {
	final TradeRepository tradeRepository;

	public Flux<com.cryptoauto.model.Trade> persistTradeData(String provider, Flux<Trade> tradeFlux) {
		return tradeFlux
				.onBackpressureBuffer()
				.subscribeOn(Schedulers.boundedElastic())
				.log()
				.concatMap(xChangetrade -> {
					com.cryptoauto.model.Trade trade = toTradeModel(xChangetrade, provider);
					log.info(trade.toString());
					tradeRepository.save(trade);
					return Flux.just(trade);
				})
				.log()
				.onErrorResume(t -> {
					log.error("Cannot save to db, exception:" ,t);
					return Flux.empty();
				});
	}

	public com.cryptoauto.model.Trade toTradeModel(Trade trade, String provider) {
		TradeBuilder tb = com.cryptoauto.model.Trade.builder();

		tb.instrument(trade.getInstrument().toString())
				.makerOrderId(trade.getMakerOrderId())
				.takerOrderId(trade.getTakerOrderId())
				.tradeId(trade.getId())
				.originalAmount(trade.getOriginalAmount())
				.price(trade.getPrice())
				.type(trade.getType().toString())
				.provider(provider)
				.tradeTimestamp(trade.getTimestamp().toInstant());

		return tb.build();
	}
}