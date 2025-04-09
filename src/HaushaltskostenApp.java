import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HaushaltskostenApp {
    private JFrame frame;
    private JTextField txtBetrag, txtBeschreibung;
    private JComboBox<String> cmbTyp, cmbKategorie;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblSaldo;

    public HaushaltskostenApp() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Haushaltskosten App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Betrag (€):"));
        txtBetrag = new JTextField();
        panel.add(txtBetrag);

        panel.add(new JLabel("Typ:"));
        cmbTyp = new JComboBox<>(new String[]{"Einnahme", "Ausgabe"});
        panel.add(cmbTyp);

        panel.add(new JLabel("Kategorie:"));
        cmbKategorie = new JComboBox<>(KategorienManager.getAllKategorien().toArray(new String[0]));
        panel.add(cmbKategorie);

        panel.add(new JLabel("Beschreibung:"));
        txtBeschreibung = new JTextField();
        panel.add(txtBeschreibung);

        JButton btnSpeichern = new JButton("Speichern");
        btnSpeichern.addActionListener(this::addTransaktion);
        panel.add(btnSpeichern);

        lblSaldo = new JLabel("Saldo: " + TransaktionsManager.berechneSaldo() + " €");
        panel.add(lblSaldo);

        frame.add(panel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Betrag", "Typ", "Kategorie_ID", "Zeitstempel", "Beschreibung"}, 0);
        table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        ladeTransaktionen();

        frame.setVisible(true);
    }

    private void addTransaktion(ActionEvent e) {
        try {
            double betrag = Double.parseDouble(txtBetrag.getText());
            String typ = (String) cmbTyp.getSelectedItem();
            String kategorie = (String) cmbKategorie.getSelectedItem();
            String beschreibung = txtBeschreibung.getText();

            int neueID = TransaktionsManager.addTransaktion(betrag, typ, kategorie, beschreibung);

            String datum = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            model.addRow(new Object[]{neueID, betrag, typ, kategorie, datum, beschreibung});
            lblSaldo.setText("Saldo: " + TransaktionsManager.berechneSaldo() + " €");
            JOptionPane.showMessageDialog(frame, "Transaktion gespeichert!");

            // Optional: Felder leeren
            txtBetrag.setText("");
            txtBeschreibung.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Ungültiger Betrag!", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ladeTransaktionen() {
        try (Connection conn = DatenbankManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT t.id, t.betrag, t.typ, k.name AS kategorie, t.zeitstempel, t.beschreibung FROM t_transaktionen t JOIN t_kategorien k ON t.Kategorie_ID = k.id")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getDouble("betrag"), rs.getString("typ"), rs.getString("kategorie"), rs.getString("zeitstempel"), rs.getString("beschreibung")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HaushaltskostenApp::new);
    }
}