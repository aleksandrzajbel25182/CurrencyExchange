CREATE TABLE subscriptions
(
    id SERIAL PRIMARY KEY,
    url VARCHAR(255)  NOT NULL,
    baseCurrencyId INT NOT NULL REFERENCES currencies (ID),
    targetCurrencyId INT NOT NULL REFERENCES currencies (ID),
    rate DECIMAL NOT NULL,
    date DATE NOT NULL ,
    status VARCHAR(15)
);

