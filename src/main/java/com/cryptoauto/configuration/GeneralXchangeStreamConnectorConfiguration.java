package com.cryptoauto.configuration;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
public class GeneralXchangeStreamConnectorConfiguration extends XchangeStreamConnectorConfiguration {
    //default
    Set<Instrument> currencyPairs = Collections.emptySet();

    TimedSemaphore semaphore = new TimedSemaphore(1, TimeUnit.SECONDS, 10);

    GeneralXchangeStreamConnectorConfiguration(Class<? extends StreamingExchange> exchangeClass, String providerName, Optional<Set<Instrument>> inputCurrencyPairs) {
        super(exchangeClass, providerName);
        if (inputCurrencyPairs.isPresent()) {
            this.currencyPairs = inputCurrencyPairs.get();
        }

        ProductSubscription.ProductSubscriptionBuilder subscriptionBuilder = ProductSubscription.create();
        currencyPairs.forEach(currencyPair -> {
            subscriptionBuilder.addOrderbook((CurrencyPair) currencyPair);
            subscriptionBuilder.addTrades((CurrencyPair) currencyPair);
            subscriptionBuilder.addTicker((CurrencyPair) currencyPair);
        });

        ProductSubscription subscription = subscriptionBuilder.build();

        streamExchange.connect(subscription).blockingAwait();

        currencyPairs.forEach(currencyPair -> {
            try {
                semaphore.acquire();
//                ((BinanceStreamingExchange) streamExchange).enableLiveSubscription();
                tradePairFluxMap.computeIfAbsent(currencyPair, s -> getStreamingMarketTradeService(currencyPair));
                orderBookFluxMap.computeIfAbsent(currencyPair, s -> getStreamingMarketOrderbookService(currencyPair));
                tickerPairFluxMap.computeIfAbsent(currencyPair, s -> getStreamingMarketTickerService(currencyPair));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
