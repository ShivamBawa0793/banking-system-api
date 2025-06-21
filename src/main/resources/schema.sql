CREATE TABLE IF NOT EXISTS ACCOUNTS (
    id VARCHAR(255) not NULL,
    creation_timestamp BIGINT,
    balance INT DEFAULT 0,
    total_outgoing INT DEFAULT 0,
    PRIMARY KEY ( id )
);