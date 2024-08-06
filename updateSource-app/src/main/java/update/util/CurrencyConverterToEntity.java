package update.util;

import com.entities.Currency;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencyConverterToEntity implements CurrencyMapToEntity<Currency> {

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
