package update;


import io.github.cdimascio.dotenv.Dotenv;
import java.time.LocalDate;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;
import update.Source.CBRFSource;

public class Main {

  private static Dotenv dotenv;

  public static void main(String[] args) {
    dotenv = Dotenv.configure()
        .directory("./")
        .filename(".env")
        .load();
    CBRFSource cbrfSource = new CBRFSource();
    boolean featureFlag = Boolean.parseBoolean(dotenv.get("DB_SUPPORTS_UPSERT"));
    Updater updater = new Updater(getDataSource(), cbrfSource, LocalDate.now(), featureFlag);
    updater.updateExchageRate();
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