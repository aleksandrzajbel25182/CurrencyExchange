/*
 * NotificationSenderService.java        1.0 2024/08/15
 */
package com.services;


import com.entities.ExchangeRate;
import com.entities.Subscription;
import com.interfaces.NotificationSender;
import com.notification.Notification;
import com.notification.NotificationTransport;
import com.repository.SubscriptionsRepository;
import com.util.JsonConvert;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
public class NotificationSenderService {

  private NotificationTransport notificationTransport;

  private NotificationSender sender;

  public NotificationSenderService(NotificationSender sender) {
    this.sender = sender;

  }

  public void sendNotification() {
    List<Future<Boolean>> futureResults = sender.send();
    for (Future<Boolean> future : futureResults) {
      try {
        Boolean result = future.get();
        if (result) {
          System.out.println("The notification has been sent successfully");
        } else {
          System.out.println("An error occurred when sending the notification");
        }
      } catch (InterruptedException | ExecutionException e) {
      }
    }
  }
}

