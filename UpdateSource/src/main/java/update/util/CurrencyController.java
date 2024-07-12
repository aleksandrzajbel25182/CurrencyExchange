package update.util;

import exchangerate.model.Currency;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import update.dto.ExchageRateDto;

public class CurrencyController implements CurrencyMapToEntity<Currency> {

  @Override
  public List<Currency> toEntity(HashMap<String, String> charCodeAndNameMap) {
    List<Currency> currencies = new ArrayList<>();
    for(Map.Entry<String, String> entry: charCodeAndNameMap.entrySet()){
      Currency currency = new Currency();
      currency.setCode(entry.getKey());
      currency.setFullName(entry.getValue());
      currencies.add(currency);
    }
    return currencies;
  }
}
