package exchangerate.utils;

import exchangerate.model.ExchangeRate;
import exchangerate.repository.ExchangeRateRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeService {

  private ExchangeRateRepository exchangeRateRepository;

  public ExchangeService(ExchangeRateRepository exchangeRateRepository) {
    this.exchangeRateRepository = exchangeRateRepository;
  }

  public BigDecimal getRate(String from, String to) {

    // 1. There is a currency pair AB in the `ExchangeRates` table - we take its rate
    ExchangeRate exchangeRate = exchangeRateRepository.finByCode(from, to);
    if (exchangeRate != null) {
      return exchangeRate.getRate();
    }
    // 2. In the `ExchangeRates` table, there is a currency pair BA - take its rate,
    //    and count the reverse to get AB
    ExchangeRate reverseExchangeRate = exchangeRateRepository.finByCode(to, from);
    if (reverseExchangeRate != null) {
      return new BigDecimal(1).divide(reverseExchangeRate.getRate(), 2, RoundingMode.HALF_UP);
    }

    // 3. In the `ExchangeRates` table, there are currency pairs RUB-A and RUB-B
    //    calculate the AB rate from these rates
    ExchangeRate exchangeRateRubToA = exchangeRateRepository.finByCode("RUB", from);
    ExchangeRate exchangeRateRubToB = exchangeRateRepository.finByCode("RUB", to);

    if (exchangeRateRubToA != null && exchangeRateRubToB != null) {

      BigDecimal rateA = exchangeRateRubToA.getRate();
      BigDecimal rateB = exchangeRateRubToB.getRate();
      BigDecimal intermediateResult = rateA.divide(rateB, 2, RoundingMode.HALF_UP);
      BigDecimal result = BigDecimal.ONE.divide(intermediateResult, 2, RoundingMode.HALF_UP);
      return result;
    }

    return null;
  }
}
