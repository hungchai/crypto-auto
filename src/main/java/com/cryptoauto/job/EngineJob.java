package com.cryptoauto.job;

import java.util.Map;

import javax.annotation.PostConstruct;

import com.cryptoauto.configuration.XchangeStreamConnectorConfiguration;
import com.cryptoauto.configuration.XchangeStreamRegisterConfiguration;
import com.cryptoauto.service.PersistDataService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@ComponentScan(basePackages = "com.cryptoauto.configuration")
@RequiredArgsConstructor
@Slf4j
public class EngineJob {

    private Map<String, XchangeStreamConnectorConfiguration> xchangeStreamConnectorMap;
    final private XchangeStreamRegisterConfiguration xchangeStreamRegisterConfiguration;
    final private PersistDataService persistDataService;

    @PostConstruct
    private void run() {
        xchangeStreamConnectorMap = xchangeStreamRegisterConfiguration.getRegisteredConnector();
        Flux.fromIterable(xchangeStreamConnectorMap.keySet())
                .map(xchangeStreamConnectorMap::get)
                .flatMap(t -> {
                    var m = t.getTradePairFluxMap();
                    return Flux.zip(Flux.just(t.getProviderName()), Flux.just(m.entrySet()));
                })
                .flatMap(e -> {
                    String exchangeProvider = e.getT1();
                    var entrySet = e.getT2();
                    return Flux.fromIterable(entrySet)
                            .flatMap(entry -> {
                                log.info("exchangeProvider {}, Instrument {}", exchangeProvider, entry.getKey().toString());
                                return persistDataService.persistTradeData(exchangeProvider, entry.getValue());
                            });
                })
                .subscribe();
    }

}
