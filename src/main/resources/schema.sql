CREATE TABLE import_campaigns (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    budget FLOAT NOT NULL,
    type VARCHAR(255) NOT NULL,
    account_id BIGINT,
    is_valid BOOLEAN NOT NULL DEFAULT false,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE import_ad_groups (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    budget FLOAT NOT NULL,
    type VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    click INT,
    view INT,
    campaign_id BIGINT,
    is_valid BOOLEAN NOT NULL DEFAULT false,
    FOREIGN KEY (campaign_id) REFERENCES import_campaigns(id)
);
