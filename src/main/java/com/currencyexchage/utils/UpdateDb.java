package com.currencyexchage.utils;

import static com.currencyexchage.utils.ConnectionPool.config;

import com.currencyexchage.model.ExchangeRate;
import com.currencyexchage.repository.ExchangeRateRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;

public class UpdateDb {

  private List<ExchangeRate> exchangeRateList;
  private ExchangeRateRepository exchangeRateRepository;
  private Date currentDate;

  private DataSource dataSource;

  private JsonParser jsParser;

  public UpdateDb(DataSource dataSource){
    this.jsParser = new JsonParser();
    this.dataSource = dataSource;
    this.exchangeRateRepository = new ExchangeRateRepository(dataSource);
    exchangeRateList = new ArrayList<>();
  }

  public void update(){

    try {
      currentDate = jsParser.parserDate();



      exchangeRateList = exchangeRateRepository.get();




    } catch (IOException e) {
     e.printStackTrace();
    }

  }


}
