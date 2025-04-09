import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class KategorienManager {
    public static void addKategorie(String name) {
        try (Connection conn = DatenbankManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO t_Kategorien (name) VALUES (?)")) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllKategorien() {
        List<String> kategorien = new ArrayList<>();
        try (Connection conn = DatenbankManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM t_Kategorien")) {
            while (rs.next()) {
                kategorien.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kategorien;
    }
}