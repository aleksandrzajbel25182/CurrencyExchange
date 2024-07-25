package update.Source;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import update.dto.ExchangeRateDto;

public class CBRFSource implements CurrencyExchangeRateSource {

  private static final DateTimeFormatter dayOfMonthFormatter
      = DateTimeFormatter.ofPattern("dd");
  private static final DateTimeFormatter monthFormatter
      = DateTimeFormatter.ofPattern("MM");


  @Override
  public List<ExchangeRateDto> get(LocalDate date) {
    List<ExchangeRateDto> exchangeRateDto = new ArrayList<>();
    String day = date.format(dayOfMonthFormatter);
    String month = date.format(monthFormatter);
    int year = date.getYear();

    Document doc = buildDocument(String.format(
        "https://cbr.ru/scripts/XML_daily.asp?date_req=%s/%s/%d",
        day,
        month,
        year));

    Node root = doc.getFirstChild();

    String dateInString = root
        .getAttributes()
        .item(0)
        .getNodeValue()
        .replace('.', '-');

    LocalDate dateCBRF = LocalDate.parse(dateInString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    NodeList valute = doc.getElementsByTagName("Valute");

    for (int i = 0; i < valute.getLength(); i++) {
      if (valute.item(i) != null) {
        exchangeRateDto.add(createExchangeRateDto(valute.item(i), dateCBRF));
      }
    }

    return exchangeRateDto;
  }

  private Document buildDocument(String url) {
    Document document = null;
    try {
      DocumentBuilder documentBuilder =
          DocumentBuilderFactory.newInstance().newDocumentBuilder();
      document = documentBuilder.parse(url);
    } catch (IOException | SAXException | ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
    return document;
  }


  private ExchangeRateDto createExchangeRateDto(Node node, LocalDate date) {
    ExchangeRateDto exchageRate = new ExchangeRateDto();
    exchageRate.setDate(date);
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      exchageRate.setBaseCurrencyCode("RUB");
      exchageRate.setTargetCurrencyCode(getValueTag(node, "CharCode"));
      exchageRate.setRate(convertToValue(node));

    }
    return exchageRate;
  }

  private String getValueTag(Node nodeElement, String tag) {
    Element element = (Element) nodeElement;
    String value = element
        .getElementsByTagName(tag)
        .item(0)
        .getChildNodes()
        .item(0)
        .getNodeValue();
    return value;
  }

  private Double convertToValue(Node node) {
    Double value = Double.parseDouble(getValueTag(node, "Value")
        .replace(',', '.'));
    Integer nominal = Integer.parseInt(getValueTag(node, "Nominal"));
    Double newValue = null;

    if (nominal > 1) {
      newValue = (1 / (value / nominal));
    } else {
      newValue = nominal / value;
    }
    return newValue;
  }

}
