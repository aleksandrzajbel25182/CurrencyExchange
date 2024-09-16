package update;


import com.interfaces.NotificationSender;
import com.notification.NotificationTransport;
import com.services.NotificationSenderService;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;
import update.source.CBRFSource;

public class Main {

  private static Dotenv dotenv;

  private static DataSource dataSource;

  public static void main(String[] args) {
    dotenv = Dotenv.configure()
        .directory("./")
        .filename(".env")
        .load();
    dataSource = getDataSource();
    CBRFSource cbrfSource = new CBRFSource();
    boolean featureFlag = Boolean.parseBoolean(dotenv.get("DB_SUPPORTS_UPSERT"));

    Updater updater = new Updater(dataSource, cbrfSource, LocalDate.now(), featureFlag);
    updater.updateExchageRate();
    System.out.println("The database has been updated");


    NotificationSender notificationSender = new NotificationTransport(dataSource);
    NotificationSenderService notificationSenderService = new NotificationSenderService(notificationSender);
    notificationSenderService.sendNotification();

    System.out.println("Notification sent");

  }

  public static DataSource getDataSource() {
    DataSource dataSource = null;
    PGSimpleDataSource ds = new PGSimpleDataSource();
    try {
      Class.forName(dotenv.get("DATABASE_DRIVER"));
      ds.setServerName(dotenv.get(("SERVER_NAME")));
      ds.setDatabaseName(dotenv.get(("DATABASE_NAME")));
      ds.setUser(dotenv.get(("DATABASE_USER")));
      ds.setPassword(dotenv.get(("DATABASE_PASSWORD")));
      ds.setReWriteBatchedInserts(true);
      System.out.println("The connection is established");
      dataSource = ds;
      return dataSource;
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}