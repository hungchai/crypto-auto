create table trade
(
    id BIGSERIAL,
	trade_id varchar(255),
	maker_order_id varchar(255),
	taker_order_id varchar(255),
    "type" varchar(255),
    original_amount numeric(36,16),
    price numeric(36,16),
    instrument varchar(255),
    trade_timestamp timestamptz,
	provider varchar(255),
    update_date timestamptz,
    create_date timestamptz default current_timestamp
);
CREATE INDEX idx_trade_provider
    ON trade (provider);

CREATE INDEX idx__trade_instrument
    ON trade (instrument);

SELECT create_hypertable('trade', 'trade_timestamp');

