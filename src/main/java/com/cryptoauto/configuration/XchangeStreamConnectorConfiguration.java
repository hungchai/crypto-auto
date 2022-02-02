package com.cryptoauto.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.instrument.Instrument;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.BackpressureStrategy;
import lombok.Getter;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;


public abstract class XchangeStreamConnectorConfiguration {
    @Getter
    protected StreamingExchange streamExchange;

    @Getter
    protected Exchange apiExchange;

    @Getter
    protected Class<? extends StreamingExchange> exchangeClass;

    @Getter
    protected String providerName;

    @Getter
    protected Map<Instrument, Flux<Trade>> tradePairFluxMap = new HashMap<>();

    @Getter
    protected Map<Instrument, Flux<OrderBook>> orderBookFluxMap = new HashMap<>();

    @Getter
    protected Map<Instrument, Flux<Ticker>> tickerPairFluxMap = new HashMap<>();

    XchangeStreamConnectorConfiguration(Class<? extends StreamingExchange> exchangeClass, String providerName) {
        this.exchangeClass = exchangeClass;
        this.providerName = providerName;
        streamExchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeClass);
        apiExchange = ExchangeFactory.INSTANCE.createExchange(exchangeClass);
    }

    Flux<Trade> getStreamingMarketTradeService(Instrument currencyPair) {
        return RxJava2Adapter.observableToFlux(streamExchange.getStreamingMarketDataService().getTrades(currencyPair),
                BackpressureStrategy.BUFFER);
    }

    Flux<OrderBook> getStreamingMarketOrderbookService(Instrument currencyPair) {
        return RxJava2Adapter.observableToFlux(streamExchange.getStreamingMarketDataService().getOrderBook(currencyPair),
                BackpressureStrategy.BUFFER);
    }

    Flux<Ticker> getStreamingMarketTickerService(Instrument currencyPair) {
        return RxJava2Adapter.observableToFlux(streamExchange.getStreamingMarketDataService().getTicker(currencyPair),
                BackpressureStrategy.BUFFER);
    }

    public OrderBook getOrderDepth(Instrument currencyPair) throws IOException {
        return apiExchange.getMarketDataService().getOrderBook((Instrument)currencyPair);
    }

    public Trades getTrades(Instrument currencyPair) throws IOException {
        return apiExchange.getMarketDataService().getTrades((Instrument)currencyPair);
    }

    public Ticker getTicker(Instrument currencyPair) throws IOException {
        return apiExchange.getMarketDataService().getTicker((Instrument)currencyPair);
    }

    public List<Instrument> getExchangeInstruments() throws IOException {
        return apiExchange.getExchangeInstruments();
    }
}
