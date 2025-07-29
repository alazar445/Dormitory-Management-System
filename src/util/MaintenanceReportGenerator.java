package util;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class MaintenanceReportGenerator {

    public static void generateReport(String filePath) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=DormManegment01;encrypt=true;trustServerCertificate=true";
        String username = "THE";
        String password = "abcdefghijklmnopqerds";


        String query = "SELECT Request_ID, Room_ID, Description, Date_Reported, Reported_By, Status, Resolution_Notes FROM Maintenance_Request";

        try (
                Connection conn = DriverManager.getConnection(url, username, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                FileWriter writer = new FileWriter(filePath)
        ) {
            writer.write("=== Maintenance Request Report ===\n\n");
            while (rs.next()) {
                writer.write("Request ID       : " + rs.getInt("Request_ID") + "\n");
                writer.write("Room ID          : " + rs.getInt("Room_ID") + "\n");
                writer.write("Description      : " + rs.getString("Description") + "\n");
                writer.write("Date Reported    : " + rs.getDate("Date_Reported") + "\n");
                writer.write("Reported By      : " + rs.getString("Reported_By") + "\n");
                writer.write("Status           : " + rs.getString("Status") + "\n");
                writer.write("Resolution Notes : " + rs.getString("Resolution_Notes") + "\n");
                writer.write("----------------------------------------\n");
            }
            writer.write("\nReport generated successfully.");
            System.out.println("Maintenance report generated at: " + filePath);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
