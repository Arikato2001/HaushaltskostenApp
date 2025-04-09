import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;


class TransaktionsManager {
    public static int addTransaktion(double betrag, String typ, String kategorie, String beschreibung) {
        int generierteID = -1;
        try (Connection conn = DatenbankManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO t_Transaktionen (betrag, typ, kategorie_id, zeitstempel, beschreibung) " +
                             "VALUES (?, ?, (SELECT id FROM t_Kategorien WHERE name = ?), ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            Timestamp zeitstempel = new Timestamp(System.currentTimeMillis());

            pstmt.setDouble(1, betrag);
            pstmt.setString(2, typ);
            pstmt.setString(3, kategorie);
            pstmt.setTimestamp(4, zeitstempel);
            pstmt.setString(5, beschreibung);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                generierteID = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generierteID;
    }



    public static double berechneSaldo() {
        double einnahmen = 0, ausgaben = 0;
        try (Connection conn = DatenbankManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT typ, SUM(betrag) AS summe FROM t_transaktionen GROUP BY typ")) {
            while (rs.next()) {
                if ("Einnahme".equals(rs.getString("typ"))) {
                    einnahmen = rs.getDouble("summe");
                } else {
                    ausgaben = rs.getDouble("summe");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return einnahmen - ausgaben;
    }
}