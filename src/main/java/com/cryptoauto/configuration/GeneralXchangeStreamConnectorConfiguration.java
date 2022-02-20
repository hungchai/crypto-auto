package com.cryptoauto.configuration;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;
import reactor.core.publisher.Flux;

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
                Flux trade = getStreamingMarketTradeService(currencyPair);
                Flux order = getStreamingMarketOrderbookService(currencyPair);
                Flux ticket = getStreamingMarketTickerService(currencyPair);

                tradePairFluxMap.computeIfAbsent(currencyPair, s -> trade);
                orderBookFluxMap.computeIfAbsent(currencyPair, s -> order);
                tickerPairFluxMap.computeIfAbsent(currencyPair, s -> ticket);
            } catch (Exception e) {
                log.warn("cannot register ", e);
            }
        });
    }
}
