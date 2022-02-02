package com.cryptoauto.configuration;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;
import org.springframework.context.annotation.Configuration;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.ProductSubscription;

@Configuration("binanceStreamConnector")
public class BinanceXchangeStreamConnectorConfiguration extends XchangeStreamConnectorConfiguration {
    Set<Instrument> currencyPairs = Set.of(
            new CurrencyPair("BTC", "USDT"),
            new CurrencyPair("GALA", "USDT"),
            new CurrencyPair("ETH", "USDT"),
            new CurrencyPair("BCH", "USDT"),
            new CurrencyPair("ATOM", "USDT"),
            new CurrencyPair("TRX", "USDT"),
            new CurrencyPair("BNB", "USDT"),
            new CurrencyPair("SHIB", "USDT"),
            new CurrencyPair("ALGO", "USDT"),
            new CurrencyPair("UNI", "USDT"),
            new CurrencyPair("FTM", "USDT"),
            new CurrencyPair("SAND", "USDT"));

    final TimedSemaphore semaphore = new TimedSemaphore(2, TimeUnit.SECONDS, 1);

    BinanceXchangeStreamConnectorConfiguration() {
        super(BinanceStreamingExchange.class, "Binance");

        // First, we need to subscribe to at least one currency pair at connection time
        // Note: at connection time, the live subscription is disabled
        // List<Instrument> instrumentList = super.getExchangeInstruments();
        // currencyPairs = instrumentList.stream().collect(Collectors.toSet());

        ProductSubscription subscription = ProductSubscription.create()
                .addTrades(CurrencyPair.BTC_USDT)
                .addOrderbook(CurrencyPair.BTC_USDT).build();

        streamExchange.connect(subscription).blockingAwait();

        currencyPairs.forEach(currencyPair -> {
            try {
                semaphore.acquire();
                ((BinanceStreamingExchange) streamExchange).enableLiveSubscription();
                tradePairFluxMap.computeIfAbsent(currencyPair, s -> getStreamingMarketTradeService(currencyPair));
                orderBookFluxMap.computeIfAbsent(currencyPair, s -> getStreamingMarketOrderbookService(currencyPair));
                tickerPairFluxMap.computeIfAbsent(currencyPair, s -> getStreamingMarketTickerService(currencyPair));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
