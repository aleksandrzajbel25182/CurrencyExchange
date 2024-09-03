/*
 * NotificationSender.java        1.0 2024/08/14
 */
package com.interfaces;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Represents a component responsible for sending notifications.
 *
 * <p> The `NotificationSender' interface defines a contract for sending notifications to users or
 * other interested parties. The implementation of this interface should manage the actual
 * notification delivery process.
 * </p>
 *
 * @author Александр Зайбель
 * @version 1.0
 */
public interface NotificationSender {

  /**
   * Sends a notification.
   * <p> This method is responsible for the actual delivery of the
   * notification. During implementation, it is necessary to ensure that the notification is
   * successfully sent to the intended recipient(s).</p>
   */
  List<Future<Boolean>> send();

}
