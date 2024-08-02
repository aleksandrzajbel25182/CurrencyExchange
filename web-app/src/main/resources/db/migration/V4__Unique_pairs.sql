ALTER TABLE exchangerates add constraint unique_pairs unique(basecurrencyid,targetcurrencyid);
CREATE UNIQUE INDEX idx_charcode ON currencies (charcode);