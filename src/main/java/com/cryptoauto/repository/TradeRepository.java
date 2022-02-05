package com.cryptoauto.repository;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.cryptoauto.model.Trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface TradeRepository extends JpaRepository<Trade, Long> {

    @Query("select m from Trade m")
    Stream<Trade> findAllAsStream();
}
