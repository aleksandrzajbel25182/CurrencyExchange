package exchangerate.service;


import com.entities.ExchangeRate;
import com.repository.ExchangeRateRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {

  private ExchangeRateRepository exchangeRateRepository;

  public ExchangeService(ExchangeRateRepository exchangeRateRepository) {
    this.exchangeRateRepository = exchangeRateRepository;
  }

  public BigDecimal getRate(String from, String to) {

    // 1. There is a currency pair AB in the `ExchangeRates` table - we take its rate
    Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCode(from, to);
    if (exchangeRate.isPresent()) {
      return exchangeRate.get().getRate();
    }
    // 2. In the `ExchangeRates` table, there is a currency pair BA - take its rate,
    //    and count the reverse to get AB
    Optional<ExchangeRate> reverseExchangeRate = exchangeRateRepository.findByCode(to, from);
    if (reverseExchangeRate.isPresent()) {
      return new BigDecimal(1).divide(reverseExchangeRate.get().getRate(), 2, RoundingMode.HALF_UP);
    }

    // 3. In the `ExchangeRates` table, there are currency pairs RUB-A and RUB-B
    //    calculate the AB rate from these rates
    Optional<ExchangeRate> exchangeRateRubToA = exchangeRateRepository.findByCode("RUB", from);
    Optional<ExchangeRate> exchangeRateRubToB = exchangeRateRepository.findByCode("RUB", to);

    if (exchangeRateRubToA.isPresent() && exchangeRateRubToB.isPresent()) {

      BigDecimal rateA = exchangeRateRubToA.get().getRate();
      BigDecimal rateB = exchangeRateRubToB.get().getRate();
      BigDecimal intermediateResult = rateA.divide(rateB, 2, RoundingMode.HALF_UP);
      BigDecimal result = BigDecimal.ONE.divide(intermediateResult, 2, RoundingMode.HALF_UP);
      return result;
    }

    return null;
  }
}
