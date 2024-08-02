CREATE UNIQUE INDEX idx_charcode ON currencies (charcode);
CREATE UNIQUE INDEX idx_pairs ON exchangerates (basecurrencyid,targetcurrencyid,date);
