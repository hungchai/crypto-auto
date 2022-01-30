package com.cryptoauto.job;

import java.util.Map;

import javax.annotation.PostConstruct;

import com.cryptoauto.configuration.XchangeStreamConnectorConfiguration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@ComponentScan(basePackages = "com.cryptoauto.configuration")
@AllArgsConstructor
@Slf4j
public class EngineJob {

	final private Map<String, XchangeStreamConnectorConfiguration> xchangeStreamConnectorMap;

	@PostConstruct
    private void run() {
	
    }
	
}
