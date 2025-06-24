import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StudentDashboard extends JFrame {
    private JComboBox destinationComboBox;
    private JButton refreshButton;
    private JTable resultTable;
    private JPanel studentDashboardPanel;
    private JButton logoutButton;
    private DefaultTableModel resultTableModel;

    Connection conn;

    public StudentDashboard() {
        setTitle("Student Dashboard - Bus Koi?");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setContentPane(studentDashboardPanel);

        conn = DBConnection.getConnection();

        loadStops();

        destinationComboBox.addActionListener(e -> {
            loadBusData();
        });
        refreshButton.addActionListener(e -> {
            loadBusData();
        });
        logoutButton.addActionListener(e -> {
            logout();
        });

        // auto refresh every 10 sec
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() ->
                    loadBusData());
        }, 0, 10, TimeUnit.SECONDS);

        table();

        setVisible(true);
    }

    private void loadStops() {
        destinationComboBox.removeAllItems();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT stop_name FROM stops");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                destinationComboBox.addItem(rs.getString("stop_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBusData() {
        resultTableModel.setRowCount(0);
        String destination = (String) destinationComboBox.getSelectedItem();

        if (destination == null || destination.isEmpty()) {
            System.out.println("No destination selected");
            return;
        }

//        String query = "SELECT b.bus_name, r.route_name, s1.stop_name AS current_stop, s2.stop_name AS next_stop, (s2.stop_order - s1.stop_order) AS eta "
//                + "FROM buses b "
//                + "JOIN routes r ON b.route_id = r.route_id "
//                + "JOIN stops s1 ON b.route_id = s1.route_id AND b.current_stop_order = s1.stop_order "
//                + "JOIN stops s2 ON b.route_id = s2.route_id AND s2.stop_name = ? "
//                + "WHERE b.current_stop_order < s2.stop_order";

        String query = "SELECT b.bus_name, r.route_name, s1.stop_name AS current_stop, s2.stop_name AS next_stop, " +
                "(SELECT stop_order FROM stops WHERE route_id = r.route_id AND stop_name = ?) - b.current_stop_order AS eta " +
                "FROM buses b " +
                "JOIN routes r ON b.route_id = r.route_id " +
                "JOIN stops s1 ON b.route_id = s1.route_id AND b.current_stop_order = s1.stop_order " +
                "LEFT JOIN stops s2 ON s2.route_id = b.route_id AND s2.stop_order = b.current_stop_order + 1 " +
                "WHERE EXISTS(SELECT 1 FROM stops WHERE route_id = b.route_id AND stop_name = ? AND stop_order > b.current_stop_order)";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, destination);
            stmt.setString(2, destination);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bus_name = rs.getString("bus_name");
                String route_name = rs.getString("route_name");
                String current_stop = rs.getString("current_stop");
                String next_stop = rs.getString("next_stop");
                int eta = rs.getInt("eta") * 10;

                if (next_stop == null) {
                    next_stop = "-";
                }
                resultTableModel.addRow(new Object[]{bus_name, route_name, current_stop, next_stop, eta + " sec"});
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void table() {
        String[] column = {"Bus Name", "Route Name", "Current Stop", "Next Stop", "Estimated Time"};
        Object[][] data = {};
        resultTableModel = new DefaultTableModel(data, column);
        resultTable.setModel(resultTableModel);
        resultTable.setShowGrid(true);
        resultTable.setGridColor(Color.BLACK);

        resultTable.getTableHeader().setFont(new Font("Fira Code", Font.BOLD, 16));
        resultTable.setFont(new Font("Fira Code", Font.PLAIN, 14));
        resultTable.setRowHeight(30);
    }

    private void logout() {
        dispose();
        new LoginRegisterForm();
    }
}
