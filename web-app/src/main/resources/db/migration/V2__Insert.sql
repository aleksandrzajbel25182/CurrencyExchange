INSERT INTO currencies (id, charcode,fullname)
VALUES
    (1, 'USD', 'US Dollar'),
    (2, 'RUB', 'Russian Ruble'),
    (3, 'EUR', 'Euro'),
    (4, 'AUD', 'Australian Dollar'),
    (5, 'GEL', 'Lari'),
    (6, 'DKK', 'Danish Krone'),
    (7, 'NOK', 'Norwegian  Krone'),
    (8, 'SEK', 'Swedish Krona'),
    (9, 'JPY', 'Yen'),
    (10, 'CNY', 'Yuan Renminbi');

INSERT INTO exchangerates (id, basecurrencyid, targetcurrencyid, rate, date)
VALUES (1,2, 1, 88.1205, '2024-07-05'),
       (2,2, 3, 94.9836, '2024-07-05'),
       (3,2, 4, 59.190, '2024-07-05'),
       (4,2, 5, 31.4761, '2024-07-05'),
       (5,2, 6, 12.7099, '2024-07-05'),
       (6,2, 7, 82.7345, '2024-07-05'),
       (7,2, 8, 83.3700, '2024-07-05'),
       (8,2, 9, 0.0, '2024-07-05'),
       (9,2, 10, 0.0, '2024-07-05');