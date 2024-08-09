package com.services;


import com.entities.Subscriptions;
import com.interfaces.NotificationSender;
import com.repository.SubscriptionsRepository;
import com.util.JsonConvert;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public class NotificationSenderService implements NotificationSender {

  private SubscriptionsRepository subscriptionsRepository;

  private DataSource dataSource;

  public NotificationSenderService(DataSource dataSource) {
    this.dataSource = dataSource;
    subscriptionsRepository = new SubscriptionsRepository(dataSource);
  }

  @Override
  public void send() {

    HashMap<String, String> notificationUrlToJson = new HashMap<>();
    List<Subscriptions> subscriptions = subscriptionsRepository.getByStatus();

    for (Subscriptions subscription : subscriptions) {
      notificationUrlToJson.put(subscription.getUrl(),
          JsonConvert.jsonConvert(new Notification(
              "Обновленная валютная пара",
              subscription.getBaseCurrencyId(),
              subscription.getTargetCurrencyId(),
              subscription.getRate(),
              subscription.getDate()
          )));
    }

    try {
      for (Map.Entry<String, String> notification : notificationUrlToJson.entrySet()) {
        URL apiUrl = new URL(notification.getKey());
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();

        outputStream.write(notification.getValue().getBytes());
        outputStream.flush();

        int responseCode = connection.getResponseCode();

        System.out.println("Server response: " + responseCode);

        outputStream.close();
        connection.disconnect();
      }
    } catch (Exception e) {
      e.printStackTrace();

    }
  }
}

