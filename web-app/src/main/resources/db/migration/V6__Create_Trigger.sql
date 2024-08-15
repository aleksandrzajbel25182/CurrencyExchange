CREATE FUNCTION update_subscription()
    RETURNS TRIGGER AS
$$
BEGIN
UPDATE subscriptions
SET status = 'not sent',
    rate   = NEW.rate,
    date   = NEW.date

WHERE baseCurrencyId = NEW.baseCurrencyId
  AND targetCurrencyId = NEW.targetCurrencyId
  AND rate <> NEW.rate;

RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER update_subscription
    AFTER INSERT OR
UPDATE
    ON exchangerates
    FOR EACH ROW
    EXECUTE FUNCTION update_subscription();