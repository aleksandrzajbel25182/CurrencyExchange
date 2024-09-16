# Content 
- About the project 
- Technical specification 
- Technology stack\
- Requirements 
- Setting up .env
- Launching the application 
- Using the app
# About the project: 
---
CURRENCY EXCHANGE RATE It acts as a pet project for mastering work with Servlets, Docker, FlywayDB and improving skills with Java.
# Specification :
___
REST API for describing currencies and exchange rates. Allows you to view, calculate the conversion of arbitrary amounts from one currency to another, subscribe to a certain exchange rate. Currency updates should take place through the parsing of the Central Bank of the Russian Federation website. 
A web interface is not intended for the project.

**Optional**:
- Be sure to set up docker-compose
- Use the flyway service
- Keep a history of currency exchange rate changes

**Requirements**:
- Use third-party libraries to a minimum
- Develop a web application in java servlets
# Technology stack:
___
Java
REST API,
Servlets,
JDBC,
HikariCP,
PostgreSql, 
Lombok,
Gson,
Slf4J,
Docker,
FlywayDB

# Requirements: 
___
To run the project, you need to install [Docker](https://www.docker.com/)
Версия Java 17

# Setting up .env
___
`DATABASE_USER = user`  
`DATABASE_PASSWORD = password`
`DATABASE_URL = jdbc:postgresql://pgsql:5432/currencyExchangedb`  
`DATABASE_DRIVER = org.postgresql.Driver`  
`FLYWAY_DB_URL = jdbc:postgresql://localhost:5432/currencyExchangedb`  
`SERVER_NAME = localhost:5432`  
`DATABASE_NAME = NameDataBase`  
`DB_SUPPORTS_UPSERT = true`

Replace user,password,NameDataBase with your own data.
`DB_SUPPORTS_UPSERT` is a feauture flag that replaces the upgrade options. 
The default update is done via `Upsert`([INSERT...ON CONFLICT](https://www.postgresqltutorial.com/postgresql-tutorial/postgresql-upsert/)).  If `DB_SUPPORTS_UPSERT` is set to false, the update algorithm will work:
1.Generate a charCode map
2.For this array, request currency identifiers from the database
3.Extract the courses from the database according to the received ids for the date specified
4.Divide it into lists for updating and adding
# Launching the application:
___
1. Assemble the docker-compose image
``` 
$ docker-compose build
```
2. Launch the docker-compose
```
$ docker-compose up -d
```
3. Migrate data to the database using flyway
```
$ maven -pl web-app flyway:migrate
```
4. Create a war archive
```
$ mvn -pl web-app install
```
5. Open the UpdateSource-app module and run the Main class
# Using the app
### API Endpoints
___
#### GET /currencies
Returns a list of all currencies in the database.
#### GET /currency?code=USD
Returns the currency record for the given currency code.
#### GET /exchangeRates
Returns a list of all exchange rates in the database.
#### GET /exchangeRate/USDRUB
Returns the exchange rate record for the given currency pair. The currency pair is specified by consecutive currency codes in the request address.
#### GET /exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT
Calculates the transfer of a certain amount of funds from one currency to another. The API attempts to get the exchange rate in one of three scenarios:

1. There is a currency pair AB in the ExchangeRates table - it takes its rate
2. There is a BA currency pair in the ExchangeRates table - it takes its rate and calculates the reverse to get AB
3. There are USD-A and USD-B currency pairs in the ExchangeRates table - it calculates the AB rate from these rates
#### POST/exchangeRates/subscription?url=URL&base=BASE_CURRENCY_CODE&target=TARGET_CURRENCY_CODE
Subscribes to exchange rate notifications
