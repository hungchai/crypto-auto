package com.cryptoauto.job;

import java.util.Map;

import javax.annotation.PostConstruct;

import com.cryptoauto.configuration.XchangeStreamConnectorConfiguration;
import com.cryptoauto.service.PersistDataService;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@ComponentScan(basePackages = "com.cryptoauto.configuration")
@AllArgsConstructor
@Slf4j
public class EngineJob {

    final private Map<String, XchangeStreamConnectorConfiguration> xchangeStreamConnectorMap;
    final private PersistDataService persistDataService;

    @PostConstruct
    private void run() {
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
                                log.info("Instrument {}", entry.getKey().toString());
                                return persistDataService.persistTradeData(exchangeProvider, entry.getValue());
                            });
                })
                .subscribe();
    }

}
