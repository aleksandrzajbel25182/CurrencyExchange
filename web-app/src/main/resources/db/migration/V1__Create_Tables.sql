CREATE TABLE currencies
(
    id       SERIAL PRIMARY KEY,
    charCode     VARCHAR(10)  NOT NULL,
    fullname VARCHAR(100) NOT NULL
);

CREATE TABLE exchangeRates
(
    id SERIAL PRIMARY KEY,
    baseCurrencyId   INT NOT NULL REFERENCES currencies (ID),
    targetCurrencyId INT NOT NULL REFERENCES currencies (ID),
    rate DECIMAL NOT NULL,
    date DATE NOT NULL
);
