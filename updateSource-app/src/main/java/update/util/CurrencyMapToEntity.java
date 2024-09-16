package update.util;

import java.util.HashMap;
import java.util.List;

public interface CurrencyMapToEntity<T>{
    List<T> toEntity(HashMap<String,String>charCodeAndNameMap);
}
