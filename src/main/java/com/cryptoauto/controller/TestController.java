package com.cryptoauto.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;
// import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

import java.io.IOException;
import java.util.Map;

import com.cryptoauto.configuration.XchangeStreamConnectorConfiguration;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class TestController {
    final private Map<String, XchangeStreamConnectorConfiguration> xchangeStreamConnectorMap;

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("hello");
    }

    @GetMapping(produces = APPLICATION_NDJSON_VALUE, path = "/stream/trade/{currencyPair}")
    public Flux<Trade> getTradeStream(@PathVariable String currencyPair) {

        XchangeStreamConnectorConfiguration s = xchangeStreamConnectorMap.get("binanceStreamConnector");
        String[] ccyPairAry = currencyPair.split("_");
        Flux<Trade> d = s.getTradePairFluxMap().get(new CurrencyPair(ccyPairAry[0], ccyPairAry[1]));

        return d;
    }

    @GetMapping(produces = APPLICATION_NDJSON_VALUE, path = "/stream/ticker/{currencyPair}")
    public Flux<Ticker> getTickerStream(@PathVariable String currencyPair) {

        XchangeStreamConnectorConfiguration s = xchangeStreamConnectorMap.get("binanceStreamConnector");
        String[] ccyPairAry = currencyPair.split("_");
        Flux<Ticker> d = s.getTickerPairFluxMap().get(new CurrencyPair(ccyPairAry[0], ccyPairAry[1]));

        return d;
    }

    @GetMapping(produces = APPLICATION_NDJSON_VALUE, path = "/stream/orderbook/{currencyPair}")
    public Flux<OrderBook> getOrderBookStream(@PathVariable String currencyPair) {

        XchangeStreamConnectorConfiguration s = xchangeStreamConnectorMap.get("binanceStreamConnector");
        String[] ccyPairAry = currencyPair.split("_");
        Flux<OrderBook> d = s.getOrderBookFluxMap().get(new CurrencyPair(ccyPairAry[0], ccyPairAry[1]));

        return d;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/orderbook/{currencyPair}")
    public OrderBook getOrderBook(@PathVariable String currencyPair) throws IOException {

        XchangeStreamConnectorConfiguration s = xchangeStreamConnectorMap.get("binanceStreamConnector");
        String[] ccyPairAry = currencyPair.split("_");
        return s.getOrderDepth(new CurrencyPair(ccyPairAry[0], ccyPairAry[1]));
    }

}
