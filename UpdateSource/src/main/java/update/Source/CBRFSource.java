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
import update.dto.CurrencyDto;
import update.dto.ExchageRateDto;

public class CBRFSource implements CurrencyExchangeRateSource {

  private static final DateTimeFormatter dayOfMonthFormatter
      = DateTimeFormatter.ofPattern("dd");
  private static final DateTimeFormatter monthFormatter
      = DateTimeFormatter.ofPattern("MM");


  @Override
  public ExchageRateDto get(LocalDate date) {
    ExchageRateDto exchageRateDto = new ExchageRateDto();
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
    List<CurrencyDto> rates = new ArrayList<>();

    for (int i = 0; i < valute.getLength(); i++) {
      rates.add(createValute(valute.item(i), dateCBRF));
    }
    exchageRateDto.setCurrencies(rates);

    return exchageRateDto;
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


  private CurrencyDto createValute(Node node, LocalDate date) {
    CurrencyDto curency = new CurrencyDto();
    curency.setDate(date);
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      curency.setCharCode(getValueTag(node, "CharCode"));
      curency.setNominal(Integer.parseInt(getValueTag(node, "Nominal")));
      curency.setName(getValueTag(node, "Name"));
      curency.setValue(Double
          .parseDouble(getValueTag(node, "Value")
              .replace(',', '.')));
      curency.setVunitRate(Double
          .parseDouble(getValueTag(node, "VunitRate")
              .replace(',', '.')));

    }
    return curency;
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

//  private Double toParseDouble(String str) {
//    String newStr = str.replace(',', '.');
//    return Double.parseDouble(str);
//  }

}
