DROP MATERIALIZED VIEW IF EXISTS price_summary_15m;

CREATE MATERIALIZED VIEW price_summary_15m
    WITH (timescaledb.continuous) AS
SELECT instrument,
       time_bucket('15 mins', trade_timestamp) AS fifteen_min,
       avg(price),
       max(price),
       min(price),
    first(price, trade_timestamp),
    last(price, trade_timestamp)
FROM trade
GROUP BY instrument, fifteen_min;


SELECT add_continuous_aggregate_policy('price_summary_15m',
                                       start_offset => null,
                                       end_offset => INTERVAL '15 minutes',
                                       schedule_interval => INTERVAL '15 minutes');

