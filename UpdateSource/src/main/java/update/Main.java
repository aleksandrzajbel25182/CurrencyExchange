package update;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    // User name
    String name = "postgres";
    // password
    String password = "root";
    PGSimpleDataSource ds = new PGSimpleDataSource() ;

    try {
      //Загружаем драйвер
      Class.forName("org.postgresql.Driver");
      ds.setServerName( "localhost:5432" );
      ds.setDatabaseName( "currencyExchangedb" );
      ds.setUser(name);
      ds.setPassword(password);
      System.out.println("The connection is established");
      dataSource = ds;
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    return dataSource;
  }

}