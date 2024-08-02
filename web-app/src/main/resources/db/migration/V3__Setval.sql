SELECT SETVAL((SELECT PG_GET_SERIAL_SEQUENCE('"currencies"', 'id')),
              (SELECT (MAX("id") + 1) FROM "currencies"), FALSE);

