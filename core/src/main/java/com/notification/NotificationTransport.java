package com.notification;

import com.entities.ExchangeRate;
import com.entities.Subscription;
import com.interfaces.NotificationSender;
import com.repository.SubscriptionsRepository;
import com.util.JsonConvert;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.sql.DataSource;

public class NotificationTransport implements NotificationSender {

  private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

  private SubscriptionsRepository subscriptionsRepository;

  private DataSource dataSource;

  public NotificationTransport(DataSource dataSource) {
    this.dataSource = dataSource;
    subscriptionsRepository = new SubscriptionsRepository(this.dataSource);
  }

  @Override
  public List<Future<Boolean>> send() {
    List<Future<Boolean>> futureResults = null;
    Connection connection = null;
    int batchSize = 20;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);

      List<Subscription> subscriptions;
      do {
        subscriptions = subscriptionsRepository.getUnsentSubscriptionsWithLock(
            "not sent", batchSize, connection);
        HashMap<String, List<String>> notificationUrlToJson = createNotification(subscriptions);
        futureResults = sendNotificationsAsync(notificationUrlToJson);
        List<String> urlSentOKList = processNotificationResults(notificationUrlToJson,
            futureResults);
        updateSubscriptionStatus(subscriptions, urlSentOKList, connection);

        connection.commit();
      } while (subscriptions.size() == batchSize);
    } catch (SQLException e) {
      try {
        if (connection != null) {
          connection.rollback();
        }
      } catch (SQLException ex) {
        throw new RuntimeException("Error rolling back transaction", ex);
      }
      throw new RuntimeException("Error processing send operation", e);
    } finally {
      try {
        if (connection != null) {
          connection.rollback();
        }
      } catch (SQLException e) {
        throw new RuntimeException("Error rolling back transaction in finally block", e);
      }
    }
    return futureResults;
  }

  private List<Future<Boolean>> sendNotificationsAsync(
      HashMap<String, List<String>> notificationUrlToJson) {
    List<Future<Boolean>> futureResults = new ArrayList<>();

    Set<String> urlKeys = notificationUrlToJson.keySet();

    for (String url : urlKeys) {
      for (String notification : notificationUrlToJson.get(url)) {
        Future<Boolean> futureResult = EXECUTOR.submit(() -> sendNotification(url, notification));
        futureResults.add(futureResult);
      }
    }
    EXECUTOR.shutdown();
    return futureResults;
  }

  private boolean sendNotification(String url, String notification) {
    try {
      URL apiUrl = new URL(url);
      HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      try (OutputStream outputStream = connection.getOutputStream()) {
        outputStream.write(notification.getBytes());
      }
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        return true;
      } else {
        System.err.println("Error sending notification. HTTP Response Code: " + responseCode);
        return false;
      }
    } catch (IOException e) {
      System.err.println("Error sending notification: " + e.getMessage());
      return false;
    }
  }

  private List<String> processNotificationResults(
      HashMap<String, List<String>> notificationUrlToJson,
      List<Future<Boolean>> futureResults) {
    List<String> urlSentOKList = new ArrayList<>();

    int i = 0;
    for (Map.Entry<String, List<String>> entry : notificationUrlToJson.entrySet()) {
      try {
        if (futureResults.get(i).get()) {
          urlSentOKList.add(entry.getKey());
        }
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
      i++;
    }
    return urlSentOKList;
  }

  private void updateSubscriptionStatus(List<Subscription> subscriptions,
      List<String> urlSentOKList, Connection con) {
    for (Subscription subscription : subscriptions) {
      if (urlSentOKList.contains(subscription.getUrl())) {
        subscription.setStatus("sent");
      }
    }
    subscriptionsRepository.upsert(subscriptions, con);
  }

  private HashMap<String, List<String>> createNotification(List<Subscription> subscriptions) {

    HashMap<String, List<String>> notificationUrlToJson = new HashMap<>();
    for (Subscription subscription : subscriptions) {
      String url = subscription.getUrl();
      String notificationJson = JsonConvert.jsonConvert(new Notification(
          "Updated currency pair",
          new ExchangeRate(
              subscription.getId(),
              subscription.getBaseCurrencyId(),
              subscription.getTargetCurrencyId(),
              subscription.getRate(),
              subscription.getDate())));

      if (notificationUrlToJson.containsKey(url)) {
        notificationUrlToJson.get(url).add(notificationJson);
      } else {
        List<String> notifications = new ArrayList<>();
        notifications.add(notificationJson);
        notificationUrlToJson.put(url, notifications);
      }
    }
    return notificationUrlToJson;
  }

}
