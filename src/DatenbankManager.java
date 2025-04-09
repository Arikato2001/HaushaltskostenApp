import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DatenbankManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/haushaltskosten";
    private static final String BENUTZER = "root";
    private static final String PASSWORT = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, BENUTZER, PASSWORT);
    }
}