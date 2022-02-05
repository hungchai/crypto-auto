
ALTER TABLE trade SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'instrument',
    timescaledb.compress_orderby = 'trade_timestamp desc'
    );

SELECT add_compression_policy('trade', INTERVAL '3 months');


SELECT * FROM timescaledb_information.compression_settings;
SELECT * FROM timescaledb_information.jobs;
SELECT * FROM timescaledb_information.job_stats;