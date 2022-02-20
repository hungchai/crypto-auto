package com.cryptoauto.configuration;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;
import org.springframework.context.annotation.Configuration;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.ProductSubscription;

@Getter
@Slf4j
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
            new CurrencyPair("OAX", "BTC"),
            new CurrencyPair("SAND", "USDT"));

    final TimedSemaphore semaphore = new TimedSemaphore(2, TimeUnit.SECONDS, 1);

    BinanceXchangeStreamConnectorConfiguration() {
        this("Binance",Optional.empty());
    }

    BinanceXchangeStreamConnectorConfiguration(String providerName, Optional<Set<Instrument>> currencyPairs) {
        super(BinanceStreamingExchange.class, providerName);

        if (currencyPairs.isPresent()) {
            this.currencyPairs = currencyPairs.get();
        }

        ProductSubscription subscription = ProductSubscription.create()
                .addTrades(CurrencyPair.BTC_USDT)
                .addOrderbook(CurrencyPair.BTC_USDT).build();

        streamExchange.connect(subscription).blockingAwait();

        this.currencyPairs.forEach(currencyPair -> {
            try {
                semaphore.acquire();
                ((BinanceStreamingExchange) streamExchange).enableLiveSubscription();
                tradePairFluxMap.computeIfAbsent(currencyPair, s -> getStreamingMarketTradeService(currencyPair));
                orderBookFluxMap.computeIfAbsent(currencyPair, s -> getStreamingMarketOrderbookService(currencyPair));
                tickerPairFluxMap.computeIfAbsent(currencyPair, s -> getStreamingMarketTickerService(currencyPair));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
