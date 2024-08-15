/*
 * NotificationSenderService.java        1.0 2024/08/15
 */
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

/**
 * * The `NotificationSenderService` class is responsible for sending notifications to subscribers.
 * It retrieves the list of subscriptions from the `SubscriptionsRepository`, creates a notification
 * for each subscription, and sends the notification to the corresponding URL using an HTTP POST
 * request.
 *
 * @author Александр Зайбель
 * @version 1.0
 */
public class NotificationSenderService implements NotificationSender {

  private SubscriptionsRepository subscriptionsRepository;

  private DataSource dataSource;

  /**
   * Constructs a new `NotificationSenderService` instance with the given `DataSource`.
   *
   * @param dataSource the `DataSource` instance used to interact with the data source
   */
  public NotificationSenderService(DataSource dataSource) {
    this.dataSource = dataSource;
    subscriptionsRepository = new SubscriptionsRepository(dataSource);
  }

  /**
   * Sends the notifications to the subscribers. This method retrieves the list of subscriptions
   * with the "not send" status, creates a notification for each subscription, and sends the
   * notification to the corresponding URL.
   */
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

