package com.cryptoauto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoautoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoautoApplication.class, args);
	}

}
