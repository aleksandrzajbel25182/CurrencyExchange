/*
 * CBRFSource.java        1.0 2024/08/01
 */
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

/**
 * Implementation of {@link CurrencyExchangeRateSource} for fetching exchange rates from the Central
 * Bank of the Russian Federation (CBRF) <a href ="https://cbr.ru/scripts/XML_daily.asp">XML
 * API</a>.
 *
 * @see CurrencyExchangeRateSource
 * @author Александр Зайбель
 * @version 1.0
 */
public class CBRFSource implements CurrencyExchangeRateSource {

  private static final DateTimeFormatter dayOfMonthFormatter
      = DateTimeFormatter.ofPattern("dd");
  private static final DateTimeFormatter monthFormatter
      = DateTimeFormatter.ofPattern("MM");

  /**
   * Retrieves exchange rates from CBRF API for a specific date.
   *
   * @param date the date for which exchange rates are requested.
   * @return list of ExchangeRateDto objects representing exchange rates for the given date.
   * @throws RuntimeException if there is an error parsing or retrieving data from the CBRF API.
   */
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
      if (valute.item(i).getNodeType() == Node.ELEMENT_NODE) {
        exchangeRateDto.add(createExchangeRateDto(valute.item(i), dateCBRF));
      }
    }

    return exchangeRateDto;
  }

  /**
   * Builds and returns an XML Document from the specified URL.
   *
   * @param url the URL from which to fetch the XML Document.
   * @return the Document object representing the XML fetched from the URL.
   * @throws RuntimeException if there is an error parsing the XML or connecting to the URL.
   */
  private Document buildDocument(String url) {
    Document document;
    try {
      DocumentBuilder documentBuilder =
          DocumentBuilderFactory.newInstance().newDocumentBuilder();
      document = documentBuilder.parse(url);
      return document;
    } catch (IOException | SAXException | ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates an ExchangeRateDto object from a Node representing a currency exchange rate.
   *
   * @param node the XML Node containing the currency exchange rate information.
   * @param date the date for which the exchange rate is applicable.
   * @return an ExchangeRateDto object populated with data from the XML Node.
   */
  private ExchangeRateDto createExchangeRateDto(Node node, LocalDate date) {
    ExchangeRateDto exchangeRate = new ExchangeRateDto();
    exchangeRate.setDate(date);
    exchangeRate.setBaseCurrencyCode("RUB");
    exchangeRate.setTargetCurrencyCode(getValueTag(node, "CharCode"));
    exchangeRate.setRate(convertToRate(node));
    exchangeRate.setName(getValueTag(node, "Name"));

    return exchangeRate;
  }

  /**
   * Retrieves the value of a specific XML tag from a Node.
   *
   * @param nodeElement The XML Node containing the desired tag.
   * @param tag the name of the tag whose value is to be retrieved.
   * @return the String value of the specified tag within the Node.
   */
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

  /**
   * Converts the value of the RUB exchange rate to any other currency
   *
   * @param node the XML Node containing the exchange rate information.
   * @return the exchange rate value as a Double.
   */
  private Double convertToRate(Node node) {
    double rate = Double.parseDouble(getValueTag(node, "Value")
        .replace(',', '.'));
    double unitRate = Double.parseDouble(getValueTag(node, "VunitRate")
        .replace(',', '.'));
    int nominal = Integer.parseInt(getValueTag(node, "Nominal"));
    Double convertToRate;

    if (nominal > 1) {
      convertToRate = 1 / unitRate;
    } else {
      convertToRate = nominal / rate;
    }
    return convertToRate;
  }
}