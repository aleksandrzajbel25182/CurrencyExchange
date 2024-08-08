CREATE UNIQUE INDEX idx_charcode ON currencies (charcode);
CREATE UNIQUE INDEX idx_pairs ON exchangerates (basecurrencyid,targetcurrencyid,date);
CREATE UNIQUE INDEX idx_url_pairs ON subscriptions (url,basecurrencyid,targetcurrencyid);
