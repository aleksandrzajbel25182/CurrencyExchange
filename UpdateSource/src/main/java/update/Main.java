package update;


import io.github.cdimascio.dotenv.Dotenv;
import java.time.LocalDate;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;
import update.Source.CBRFSource;

public class Main {

  public static void main(String[] args) {

    CBRFSource cbrfSource = new CBRFSource();
    Updater updater = new Updater(getDataSource(), cbrfSource, LocalDate.now());
    updater.updateExchageRate();
  }

  public static DataSource getDataSource() {
    DataSource dataSource = null;
    PGSimpleDataSource ds = new PGSimpleDataSource();
    Dotenv dotenv = Dotenv.configure()
        .directory("UpdateSource/")
        .filename(".env")
        .load();
    try {
      //Загружаем драйвер
      Class.forName(dotenv.get("DATABASE_DRIVER"));
      ds.setServerName(dotenv.get(("SERVER_NAME")));
      ds.setDatabaseName(dotenv.get(("DATABASE_NAME")));
      ds.setUser(dotenv.get(("DATABASE_USER")));
      ds.setPassword(dotenv.get(("DATABASE_PASSWORD")));
      ds.setReWriteBatchedInserts(true);
      System.out.println("The connection is established");
      dataSource = ds;

    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    return dataSource;
  }

}