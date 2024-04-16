CREATE TABLE currencies
(
    id       INT PRIMARY KEY,
    code     VARCHAR(10)  NOT NULL,
    fullname VARCHAR(100) NOT NULL,
    sign     VARCHAR(10)  NOT NULL
);

CREATE TABLE ExchangeRates
(
    id INT PRIMARY KEY,
    baseCurrencyId   INT NOT NULL REFERENCES CurrencyExchanger.currencies (ID),
    targetCurrencyId INT NOT NULL REFERENCES CurrencyExchanger.currencies (ID),
    rate DECIMAL NOT NULL
);