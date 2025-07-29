package dao;
import model.*;
import main.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import interfaces.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import abstracts.DBConfigBaseAbs;


public class RoomDAO implements RoomDAOInterface {

    // Get all rooms
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT Room_ID, Capacity, Gender_Type, Status, Dorm_ID FROM Room";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("Room_ID"),
                        rs.getInt("Capacity"),
                        rs.getString("Gender_Type"),
                        rs.getString("Status"),
                        rs.getInt("Dorm_ID")
                );
                rooms.add(room);
            }
        }
        return rooms;
    }

    // Check if Dormitory ID exists
    public boolean dormExists(int dormId) throws SQLException {
        // Query the Dormitory table
        String sql = "SELECT 1 FROM Dormitory WHERE Dorm_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dormId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Insert room
    public boolean insertRoom(Room room) throws SQLException {
        String sql = "INSERT INTO Room (Capacity, Gender_Type, Status, Dorm_ID) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, room.getCapacity());
            ps.setString(2, room.getGenderType());
            ps.setString(3, room.getStatus());
            ps.setInt(4, room.getDormId());
            return ps.executeUpdate() > 0;
        }
    }

    // Delete room by Room_ID
    public boolean deleteRoom(int roomId) throws SQLException {
        String sql = "DELETE FROM Room WHERE Room_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;
        }
    }

}

class DBConnection extends DBConfigBaseAbs {
    public static Connection getConnection() throws SQLException {
        DBConnection db = new DBConnection();

        try {
            db.loadConfig("dbconfig.txt");
        } catch (IOException e) {
            e.printStackTrace();
            throw new SQLException("Failed to load DB config.");
        }

        return DriverManager.getConnection(db.url, db.username, db.password);
    }
}