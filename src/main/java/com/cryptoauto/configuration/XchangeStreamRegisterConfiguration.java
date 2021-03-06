package com.cryptoauto.configuration;

import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.ftx.FtxStreamingExchange;
import info.bitrich.xchangestream.gemini.GeminiStreamingExchange;
import info.bitrich.xchangestream.huobi.HuobiStreamingExchange;
import info.bitrich.xchangestream.kraken.KrakenStreamingExchange;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Configuration()
@Getter
@Slf4j
public class XchangeStreamRegisterConfiguration {
    Map<String, Class<? extends StreamingExchange>> streamClassMap = Map.of(
            "Binance", BinanceStreamingExchange.class,
            "Ftx", FtxStreamingExchange.class,
            "Coinbasepro", CoinbaseProStreamingExchange.class,
            "Kraken", KrakenStreamingExchange.class,
            "Huobi", HuobiStreamingExchange.class,
            "Bitstamp", BitstampStreamingExchange.class,
            "Gemini", GeminiStreamingExchange.class,
            "Bitfinex", BitfinexStreamingExchange.class
            );

    HashMap<String, XchangeStreamConnectorConfiguration> registeredConnector = new HashMap<>();

    Set registerPair = Set.of(
            new CurrencyPair("BTC", "USD"),
            new CurrencyPair("GALA", "USD"),
            new CurrencyPair("ETH", "USD"),
            new CurrencyPair("BCH", "USD"),
            new CurrencyPair("ATOM", "USD"),
            new CurrencyPair("TRX", "USD"),
            new CurrencyPair("BNB", "USD"),
            new CurrencyPair("SHIB", "USD"),
            new CurrencyPair("ALGO", "USD"),
            new CurrencyPair("UNI", "USD"),
            new CurrencyPair("FTM", "USD"),
            new CurrencyPair("OAX", "BTC"),
            new CurrencyPair("SOL", "USD"),
            new CurrencyPair("SAND", "USD"),
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
            new CurrencyPair("SOL", "USDT"),
            new CurrencyPair("SAND", "USDT"));


    XchangeStreamRegisterConfiguration(){
        Flux.fromIterable(streamClassMap.entrySet())
                .map(streamingExchange -> {
                    XchangeStreamConnectorConfiguration connector;
                    if (BinanceStreamingExchange.class.equals(streamingExchange.getValue())) {
                        connector = new BinanceXchangeStreamConnectorConfiguration(streamingExchange.getKey(), Optional.ofNullable(registerPair));
                    } else {
                        connector = new GeneralXchangeStreamConnectorConfiguration(streamingExchange.getValue(), streamingExchange.getKey(), Optional.ofNullable(registerPair));
                    }
                    return connector;
                }).doOnNext(connector -> {
                    registeredConnector.put(connector.getProviderName(), connector);
                })
                .subscribe();
    }
}
