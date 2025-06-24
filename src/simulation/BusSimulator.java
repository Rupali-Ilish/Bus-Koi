import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.sql.DriverManager.getConnection;

public class BusSimulator {
    private static final int move_interval_seconds = 10;
    private static final int reset_interval_seconds = 60;

    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public static void startSimulation() {
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("bus move started");
            moveBusForward();
        }, 0, move_interval_seconds, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("reset started");
            resetBuses();
        }, reset_interval_seconds, reset_interval_seconds, TimeUnit.SECONDS);
    }


    public static void moveBusForward() {
        Connection con = DBConnection.getConnection();

        String getBuses = "SELECT bus_id, route_id, current_stop_order FROM buses";

        try {
            PreparedStatement ps = con.prepareStatement(getBuses);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int bus_id = rs.getInt("bus_id");
                int route_id = rs.getInt("route_id");
                int current_stop_order = rs.getInt("current_stop_order");

                String countStops = "SELECT count(*) FROM stops WHERE route_id = " + route_id;
                PreparedStatement ps_stops = con.prepareStatement(countStops);
                ResultSet rs_stops = ps_stops.executeQuery(); // returns the resultSet with cursor placed before 1st row
                rs_stops.next(); // moves the cursor to 1st row (only row)

                int total_stops = rs_stops.getInt(1);

                int next_stop = current_stop_order < total_stops ? current_stop_order + 1 : current_stop_order;

                String updateBus = "UPDATE buses SET current_stop_order = ? WHERE bus_id = ?";
                PreparedStatement ps_update = con.prepareStatement(updateBus);
                ps_update.setInt(1, next_stop);
                ps_update.setInt(2, bus_id);

                int row = ps_update.executeUpdate();
                if (row <= 0) {
                    JOptionPane.showMessageDialog(null, "Can't update buses");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.println("Can't move bus forward");
            return;
        }
    }

    public static void resetBuses() {
        Connection con = DBConnection.getConnection();
        String resetBuses = "UPDATE buses SET current_stop_order = 0";

        try {
            PreparedStatement ps = con.prepareStatement(resetBuses);
            int row = ps.executeUpdate();
            if (row <= 0) {
                JOptionPane.showMessageDialog(null, "Can't reset buses");
            }

        } catch (Exception e) {
            System.out.println("Can't reset buses");
        }
    }

    public static void stopSimulation() {
        scheduler.shutdownNow();
    }
}
