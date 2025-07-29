package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class RoomStudentLookup {

    private static final String URL = "jdbc:sqlserver://localhost;databaseName=DormManegment01;encrypt=true;trustServerCertificate=true";
    private static final String USER = "THE";
    private static final String PASSWORD = "abcdefghijklmnopqerds";

    // Method to get database connection
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method to get student names assigned to a specific room
    private static List<String> getStudentNamesByRoomId(int roomId) throws SQLException {
        List<String> names = new ArrayList<>();
        String sql = "SELECT s.Full_Name FROM Student s " +
                "JOIN Assignment a ON s.Student_ID = a.Student_ID " +
                "WHERE a.Room_ID = ? AND a.Status = 'Active'";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("Full_Name"));
                }
            }
        }

        return names;
    }

    // GUI Method
    public static void showStudentsInRoom() {
        String input = JOptionPane.showInputDialog("Enter Room ID:");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int roomId = Integer.parseInt(input.trim());
            List<String> studentNames = getStudentNamesByRoomId(roomId);

            if (studentNames.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No active students found in Room ID: " + roomId);
            } else {
                StringBuilder result = new StringBuilder("Students in Room " + roomId + ":\n\n");
                for (String name : studentNames) {
                    result.append("• ").append(name).append("\n");
                }
                JOptionPane.showMessageDialog(null, result.toString());
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number for Room ID.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error:\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        showStudentsInRoom();
    }
}
