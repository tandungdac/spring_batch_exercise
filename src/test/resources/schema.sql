CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    is_expired BOOLEAN,
    is_valid BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS campaigns (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    budget FLOAT NOT NULL,
    type VARCHAR(255) NOT NULL,
    account_id BIGINT,
    is_valid BOOLEAN NOT NULL DEFAULT true,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE IF NOT EXISTS ad_groups (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    budget FLOAT NOT NULL,
    type VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    click INT,
    view INT,
    campaign_id BIGINT,
    is_valid BOOLEAN NOT NULL DEFAULT true,
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id)
);

CREATE TABLE IF NOT EXISTS import_campaigns (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    budget FLOAT NOT NULL,
    type VARCHAR(255) NOT NULL,
    account_id BIGINT,
    is_valid BOOLEAN NOT NULL DEFAULT true,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE IF NOT EXISTS import_ad_groups (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    budget FLOAT NOT NULL,
    type VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    click INT,
    view INT,
    campaign_id BIGINT,
    is_valid BOOLEAN NOT NULL DEFAULT true,
    FOREIGN KEY (campaign_id) REFERENCES import_campaigns(id)
);
