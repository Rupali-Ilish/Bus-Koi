import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class AdminDashboard extends JFrame {
    private JPanel AdminDashboardPanel;
    private JTabbedPane tabbedPane1;
    private JTable BusTable, RouteTable;
    private JButton refreshButton, logoutButton, addBusButton, clearAddBusFieldButton, deleteBusButton;
    private JButton addRouteButton, deleteRouteButton, updateBusButton;
    private JTextField AddBusNameTextField, AddBusRouteNameTextField, AddBusCurrentStopOrderTextField;
    private JTextField DeleteBusNameTextField, addRouteNameTextField, addRouteNumOfStopTextField, addRouteStopNamesTextField;
    private JTextField deleteRouteNameTextField, updateBusNameTextField, updateBusRouteNameTextField, updateBusStopOrderTextField;
    private JComboBox routeNameComboBox;
    private JTextField updateRouteStopOrderTextField, updateRouteStopNameTextField;
    private JButton updateRouteRemoveRouteButton, updateRouteAddRouteButton;

    Connection conn;

    AdminDashboard() {
        setTitle("Admin Dashboard - Bus Koi?");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setContentPane(AdminDashboardPanel);

        conn = DBConnection.getConnection();
        refreshButton.addActionListener(e -> viewAllBuses());
        logoutButton.addActionListener(e -> logout());
        viewAllBuses();

        clearAddBusFieldButton.addActionListener(e -> clearAddBusField());
        addBusButton.addActionListener(e -> addBus());

        deleteBusButton.addActionListener(e -> deleteBus());

        addRouteButton.addActionListener(e -> addRoute());

        deleteRouteButton.addActionListener(e -> deleteRoute());

        updateBusButton.addActionListener(e -> updateBus());

        readyComboBox();

        updateRouteAddRouteButton.addActionListener(e -> updateRouteAddStop());
        updateRouteRemoveRouteButton.addActionListener(e -> updateRouteRemoveStop());

        setVisible(true);
    }

    private void viewAllBuses() {
        String[] column = {"Bus Name", "Route Name", "Route Details", "Current Stop"};
        Object[][] data = {};
        DefaultTableModel BusTableModel = new DefaultTableModel(data, column);
        BusTable.setModel(BusTableModel);
        BusTable.setShowGrid(true);
        BusTable.setGridColor(Color.BLACK);

        BusTable.getTableHeader().setFont(new Font("Fira Code", Font.BOLD, 16));
        BusTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        BusTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        BusTable.getColumnModel().getColumn(2).setPreferredWidth(400);
        BusTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        BusTable.setFont(new Font("Fira Code", Font.PLAIN, 14));
        BusTable.setRowHeight(30);

        String sql_bus = "SELECT buses.bus_name, buses.route_id, routes.route_name, buses.current_stop_order " +
                "FROM buses " +
                "LEFT JOIN routes on buses.route_id=routes.route_id ";
        try {
            PreparedStatement bus_stmt = conn.prepareStatement(sql_bus);
            ResultSet rs = bus_stmt.executeQuery();

            while (rs.next()) {
                String bus_name = rs.getString("bus_name");
                String route_name = rs.getString("route_name");
                if (route_name == null) {
                    route_name = "Not Assigned";
                }
                int route_id = rs.getInt("route_id");
                String route_details = getRouteStops(route_id);
                int current_stop_order = rs.getInt("current_stop_order");
                String current_stop = getStopName(route_id, current_stop_order);

                BusTableModel.addRow(new String[]{bus_name, route_name, route_details, current_stop});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading bus routes from database");
        }
        viewAllRoutes();
    }

    private String getRouteStops(int route_id) {
        StringBuilder route_stops = new StringBuilder();
        String query = "SELECT stop_name FROM stops WHERE route_id=? ORDER BY stop_order";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, route_id);
            ResultSet rs = ps.executeQuery();
            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    route_stops.append(" -> ");
                }
                route_stops.append(rs.getString("stop_name"));
                first = false;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading routes from database");
        }
        return route_stops.toString();
    }

    private String getStopName(int route_id, int stop_order) {
        String query = "SELECT stop_name FROM stops WHERE route_id=? AND stop_order=?";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, route_id);
            ps.setInt(2, stop_order);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("stop_name");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading stop name from database");
        }
        return "";
    }

    private void viewAllRoutes() {
        String[] column = {"Route Name", "Route Details"};
        Object[][] data = {};
        DefaultTableModel RouteTableModel = new DefaultTableModel(data, column);
        RouteTable.setModel(RouteTableModel);
        RouteTable.setShowGrid(true);
        RouteTable.setGridColor(Color.BLACK);

        RouteTable.getTableHeader().setFont(new Font("Fira Code", Font.BOLD, 16));
        RouteTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        RouteTable.getColumnModel().getColumn(1).setPreferredWidth(500);
        RouteTable.setFont(new Font("Fira Code", Font.PLAIN, 14));
        RouteTable.setRowHeight(30);

        String routeSql = "SELECT * FROM routes ";
        try {
            PreparedStatement ps = conn.prepareStatement(routeSql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String route_name = rs.getString("route_name");
                int route_id = rs.getInt("route_id");
                String route_details = getRouteStops(route_id);

                RouteTableModel.addRow(new String[]{route_name, route_details});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading routes from database");
        }
    }

    private void logout() {
        dispose();
        new LoginRegisterForm();
    }

    private void addBus() {
        String bus_name = AddBusNameTextField.getText().trim();
        String route_name = AddBusRouteNameTextField.getText().trim();
        String current_stop_order = AddBusCurrentStopOrderTextField.getText().trim();

        if (bus_name.isEmpty() || route_name.isEmpty() || current_stop_order.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
        }

        try {
            String sql_routeId = "SELECT route_id FROM routes WHERE route_name=?";
            PreparedStatement psRoute = conn.prepareStatement(sql_routeId);
            psRoute.setString(1, route_name);
            ResultSet rs = psRoute.executeQuery();
            if (rs.next()) {
                int route_id = rs.getInt("route_id");
                int stop_order = Integer.parseInt(current_stop_order);

                String sql = "INSERT INTO buses(bus_name, route_id, current_stop_order) VALUES(?,?,?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, bus_name);
                ps.setInt(2, route_id);
                ps.setInt(3, stop_order);
                int rows = ps.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Bus added successfully");
                    clearAddBusField();
                    viewAllBuses();
                } else {
                    JOptionPane.showMessageDialog(this, "Error adding bus");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Route not found with given name");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Route id and Stop order must be numbers");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding bus " + e);
        }
    }

    private void clearAddBusField() {
        AddBusNameTextField.setText("");
        AddBusRouteNameTextField.setText("");
        AddBusCurrentStopOrderTextField.setText("");
    }

    private void deleteBus() {
        String bus_name = DeleteBusNameTextField.getText().trim();
        if (bus_name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a bus name");
            return;
        }
        String sql = "DELETE FROM buses WHERE bus_name=?";
        try {
            PreparedStatement delete_bus = conn.prepareStatement(sql);
            delete_bus.setString(1, bus_name);
            int rows = delete_bus.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Bus deleted successfully");
                DeleteBusNameTextField.setText("");
                viewAllBuses();
            } else {
                JOptionPane.showMessageDialog(this, "Bus not found");
                DeleteBusNameTextField.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting bus from database");
        }
    }

    private void addRoute() {
        String route_name = addRouteNameTextField.getText().trim();
        String num_of_stops_str = addRouteNumOfStopTextField.getText().trim();
        String stops_input = addRouteStopNamesTextField.getText().trim();

        if (route_name.isEmpty() || num_of_stops_str.isEmpty() || stops_input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return;
        }

        try {
            int num_of_stops = Integer.parseInt(num_of_stops_str);
            String[] stop_names = stops_input.split(",");

            if (stop_names.length != num_of_stops) {
                JOptionPane.showMessageDialog(this, "Number of stop names doesn't match the count provided");
                return;
            }
            // Insert route name
            String insertRouteSql = "INSERT INTO routes(route_name) VALUES(?)";
            PreparedStatement insertRoute = conn.prepareStatement(insertRouteSql, Statement.RETURN_GENERATED_KEYS);
            // the additional "RETURN_GENERATED_KEYS" helps to get auto-generated key next time
            insertRoute.setString(1, route_name);
            int rows = insertRoute.executeUpdate();
            if (rows > 0) {
                ResultSet rs = insertRoute.getGeneratedKeys(); // returns the auto-generated key from the db table
                if (rs.next()) {
                    int route_id = rs.getInt(1);
                    // Insert each stop
                    String insertStopSql = "INSERT INTO stops(route_id, stop_name,stop_order) VALUES(?,?,?)";
                    PreparedStatement insertStop = conn.prepareStatement(insertStopSql);

                    for (int i = 0; i < stop_names.length; i++) {
                        insertStop.setInt(1, route_id);
                        insertStop.setString(2, stop_names[i]);
                        insertStop.setInt(3, i + 1);
                        insertStop.addBatch(); // queues the insertions
                    }
                    insertStop.executeBatch(); // executes all insertions at once

                    JOptionPane.showMessageDialog(this, "Route and stops added successfully");
                    clearAddRouteFields();
                    viewAllRoutes();
                    readyComboBox();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error adding route");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Number of stops must be a number");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding route: " + e.getMessage());
        }
    }

    private void clearAddRouteFields() {
        addRouteNameTextField.setText("");
        addRouteNumOfStopTextField.setText("");
        addRouteStopNamesTextField.setText("");
    }

    private void deleteRoute() {
        String route_name = deleteRouteNameTextField.getText().trim();
        if (route_name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a route name.");
            return;
        }

        String getRouteIDSql = "SELECT route_id FROM routes WHERE route_name=?";
        try {
            PreparedStatement getRouteID = conn.prepareStatement(getRouteIDSql);
            getRouteID.setString(1, route_name);
            ResultSet rs = getRouteID.executeQuery();
            if (rs.next()) {
                int route_id = rs.getInt("route_id");

                // null the buses
                String nullifyBusSql = "UPDATE buses SET route_id = NULL, current_stop_order = NULL WHERE route_id = ?";
                PreparedStatement nullifyBus = conn.prepareStatement(nullifyBusSql);
                nullifyBus.setInt(1, route_id);
                nullifyBus.executeUpdate();

                // delete stops first
                String deleteStopSql = "DELETE FROM stops WHERE route_id=?";
                PreparedStatement deleteStop = conn.prepareStatement(deleteStopSql);
                deleteStop.setInt(1, route_id);
                int rows = deleteStop.executeUpdate();
                if (rows > 0) {
                    // delete route now
                    String deleteRouteSql = "DELETE FROM routes WHERE route_id=?";
                    PreparedStatement deleteRoute = conn.prepareStatement(deleteRouteSql);
                    deleteRoute.setInt(1, route_id);
                    int rows2 = deleteRoute.executeUpdate();
                    if (rows2 > 0) {
                        JOptionPane.showMessageDialog(this, "Route deleted successfully");
                        deleteRouteNameTextField.setText("");
                        viewAllBuses();
                        readyComboBox();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error deleting route");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Route not found with given name");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting route from database");
        }
    }

    private void updateBus() {
        String bus_name = updateBusNameTextField.getText().trim();
        String route_name = updateBusRouteNameTextField.getText().trim();
        String stop_order = updateBusStopOrderTextField.getText().trim();
        if (bus_name.isEmpty() || route_name.isEmpty() || stop_order.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return;
        }

        try {
            int newStopOrder = Integer.parseInt(stop_order);

            // route_id from route_name
            String getRouteIdSQL = "SELECT route_id FROM routes WHERE route_name = ?";
            PreparedStatement psRoute = conn.prepareStatement(getRouteIdSQL);
            psRoute.setString(1, route_name);
            ResultSet rs = psRoute.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Route name not found.");
                return;
            }

            int newRouteId = rs.getInt("route_id");

            // update bus
            String sql = "UPDATE buses SET route_id = ?, current_stop_order = ? WHERE bus_name = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, newRouteId);
                ps.setInt(2, newStopOrder);
                ps.setString(3, bus_name);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Bus updated successfully!");
                    clearUpdateBusField();
                    viewAllBuses();
                } else {
                    JOptionPane.showMessageDialog(this, "Bus not found.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Stop Order must be a number.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void clearUpdateBusField() {
        updateBusNameTextField.setText("");
        updateBusRouteNameTextField.setText("");
        updateBusStopOrderTextField.setText("");
    }

    private void readyComboBox() {
        try {
            String sql = "SELECT route_name FROM routes";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            routeNameComboBox.removeAllItems(); // Clear old items
            while (rs.next()) {
                routeNameComboBox.addItem(rs.getString("route_name"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load routes");
            e.printStackTrace();
        }
    }

    private boolean isStopOrderValidToAdd(String route_name, int stop_order) {
        try {
            String route_idSQL = "SELECT route_id FROM routes WHERE route_name = ?";
            PreparedStatement psRoute = conn.prepareStatement(route_idSQL);
            psRoute.setString(1, route_name);
            ResultSet rs = psRoute.executeQuery();

            if (!rs.next()) {
                return false;
            }

            int route_id = rs.getInt("route_id");

            String OrderSql = "SELECT stop_order FROM stops WHERE route_id = ?";
            PreparedStatement psStop = conn.prepareStatement(OrderSql);
            psStop.setInt(1, route_id);
            ResultSet rsStop = psStop.executeQuery();

            Set<Integer> exists = new HashSet<>();
            while (rsStop.next()) {
                exists.add(rsStop.getInt("stop_order"));
            }

            for (int i = 1; i < stop_order; i++) {
                if (!exists.contains(i)) {
                    return false; // missing a previous stop order
                }
            }
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load routes");
            return false;
        }
    }

    private void updateRouteAddStop() {
        String route_name = routeNameComboBox.getSelectedItem().toString();
        String stop_order_str = updateRouteStopOrderTextField.getText().trim();
        String stop_name = updateRouteStopNameTextField.getText().trim();

        try {
            if (route_name.isEmpty() || stop_order_str.isEmpty() || stop_name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all the fields");
                return;
            }
            int stop_order = Integer.parseInt(stop_order_str);

            // check if previous stop_order are available.
            if (!isStopOrderValidToAdd(route_name, stop_order)) {
                JOptionPane.showMessageDialog(this, "Invalid stop order. Make sure all previous stop orders (1 to " + (stop_order - 1) + ") exist.");
                return;
            }

            updateRoute(route_name, stop_order, stop_name, "add");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Stop Order must be a number.");
        }
    }

    private void updateRouteRemoveStop() {
        String route_name = routeNameComboBox.getSelectedItem().toString();
        String stop_order_str = updateRouteStopOrderTextField.getText().trim();
        String stop_name = updateRouteStopNameTextField.getText().trim();

        try {
            if (route_name.isEmpty() || stop_order_str.isEmpty() || stop_name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all the fields");
                return;
            }
            int stop_order = Integer.parseInt(stop_order_str);
            updateRoute(route_name, stop_order, stop_name, "remove");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Stop Order must be a number.");
        }
    }

    private void updateRoute(String route_name, int stop_order, String stop_name, String action) {
        try {
            // get route_id from route_name
            String get_routeIdSQL = "SELECT route_id FROM routes WHERE route_name = ?";
            PreparedStatement psRoute = conn.prepareStatement(get_routeIdSQL);
            psRoute.setString(1, route_name);
            ResultSet rs = psRoute.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Route not found");
                return;
            }

            int route_id = rs.getInt("route_id");

            // for add button
            if (action.equals("add")) {
                // making stop_order = stop_order + 1 for every other stops
                String shift_plusOneSql = "UPDATE stops SET stop_order = stop_order + 1 WHERE route_id = ? AND stop_order>=?";
                PreparedStatement psShift = conn.prepareStatement(shift_plusOneSql);
                psShift.setInt(1, route_id);
                psShift.setInt(2, stop_order);
                psShift.executeUpdate(); // no need for row check, what if adding at last stop

                // now inserting new stop
                String insertSql = "INSERT INTO stops(route_id, stop_order, stop_name) VALUES(?, ?, ?)";
                PreparedStatement psInsert = conn.prepareStatement(insertSql);
                psInsert.setInt(1, route_id);
                psInsert.setInt(2, stop_order);
                psInsert.setString(3, stop_name);
                int rowsInsert = psInsert.executeUpdate();
                if (rowsInsert > 0) {
                    JOptionPane.showMessageDialog(this, "Stop added successfully");
                    clearUpdateRouteField();
                }

            }
            // for remove button
            else if (action.equals("remove")) {
                // delete the stop
                String deleteSql = "DELETE FROM stops WHERE route_id = ? AND stop_order = ?";
                PreparedStatement psDelete = conn.prepareStatement(deleteSql);
                psDelete.setInt(1, route_id);
                psDelete.setInt(2, stop_order);
                int rowsDelete = psDelete.executeUpdate();
                if (rowsDelete > 0) {
                    // making stop_order = stop_order - 1 for every other stops remaining after this
                    String shift_minusOne = "UPDATE stops SET stop_order = stop_order - 1 WHERE route_id = ? AND stop_order > ?";
                    PreparedStatement psShift = conn.prepareStatement(shift_minusOne);
                    psShift.setInt(1, route_id);
                    psShift.setInt(2, stop_order);
                    psShift.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Stop removed successfully");
                    clearUpdateRouteField();
                } else {
                    JOptionPane.showMessageDialog(this, "No stop found");
                }
            }
            viewAllBuses();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating route");
        }
    }

    private void clearUpdateRouteField() {
        updateRouteStopOrderTextField.setText("");
        updateRouteStopNameTextField.setText("");
    }

}