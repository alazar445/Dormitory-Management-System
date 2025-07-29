package dao;

import interfaces.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import model.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class StudentDAO implements StudentDAOInterface {
    private static final String URL = "jdbc:sqlserver://localhost;databaseName=DormManegment01;encrypt=true;trustServerCertificate=true";
    private static final String USER = "THE";
    private static final String PASSWORD = "abcdefghijklmnopqerds";

    public boolean isStudentAssignedToRoom(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Assignment WHERE Student_ID = ? AND Status = 'Active'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public StudentDAO() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Fetch all students
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM Student";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("Student_ID"),
                        rs.getString("Full_Name"),
                        rs.getString("Gender"),
                        rs.getString("Department"),
                        rs.getString("Contact_Info"),
                        rs.getInt("Enrollment_Year"),
                        rs.getString("Current_Status"),
                        rs.getString("SponsorshipStatus")
                ));
            }
        }
        return students;
    }

    // Add a new student
    public boolean addStudent(Student student) throws SQLException {
        String query = "INSERT INTO Student (Full_Name, Gender, Department, Contact_Info, Enrollment_Year, Current_Status, SponsorshipStatus) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, student.getFullName());
            ps.setString(2, student.getGender());
            ps.setString(3, student.getDepartment());
            ps.setString(4, student.getContactInfo());
            ps.setInt(5, student.getEnrollmentYear());
            ps.setString(6, student.getCurrentStatus());
            ps.setString(7, student.getSponsorshipStatus());

            return ps.executeUpdate() > 0;
        }
    }

    // Update student
    public boolean updateStudent(Student student) throws SQLException {
        String query = "UPDATE Student SET Full_Name=?, Gender=?, Department=?, Contact_Info=?, Enrollment_Year=?, Current_Status=?, SponsorshipStatus=? WHERE Student_ID=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, student.getFullName());
            ps.setString(2, student.getGender());
            ps.setString(3, student.getDepartment());
            ps.setString(4, student.getContactInfo());
            ps.setInt(5, student.getEnrollmentYear());
            ps.setString(6, student.getCurrentStatus());
            ps.setString(7, student.getSponsorshipStatus());
            ps.setInt(8, student.getStudentId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete student
    public boolean deleteStudent(int studentId) throws SQLException {
        String query = "DELETE FROM Student WHERE Student_ID = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    // Assign student to a room
    public boolean assignRoom(int studentId, int roomId, Date checkInDate) throws SQLException {
        if (!roomHasCapacity(roomId)) {
            throw new SQLException("Room is full or does not exist.");
        }
        if (!genderMatches(studentId, roomId)) {
            throw new SQLException("Gender mismatch.");
        }
        String query = "INSERT INTO Assignment (Student_ID, Room_ID, Check_In_Date, Status) VALUES (?, ?, ?, 'Active')";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, studentId);
            ps.setInt(2, roomId);
            ps.setDate(3, checkInDate);
            return ps.executeUpdate() > 0;
        }
    }



    public boolean unassignRoomByStudentId(int studentId, Date checkOutDate) throws SQLException {
        String query = """
        UPDATE Assignment
        SET Check_Out_Date = ?, Status = 'Past'
        WHERE Student_ID = ? AND Status = 'Active'
    """;
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, checkOutDate);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }


    // Additional methods for validation

    private boolean roomHasCapacity(int roomId) throws SQLException {
        String capQuery = "SELECT Capacity FROM Room WHERE Room_ID = ?";
        String countQuery = "SELECT COUNT(*) AS assigned FROM Assignment WHERE Room_ID = ? AND Status = 'Active'";
        try (Connection con = getConnection();
             PreparedStatement psCap = con.prepareStatement(capQuery);
             PreparedStatement psCount = con.prepareStatement(countQuery)) {
            psCap.setInt(1, roomId);
            ResultSet rsCap = psCap.executeQuery();
            if (!rsCap.next()) return false;
            int capacity = rsCap.getInt("Capacity");

            psCount.setInt(1, roomId);
            ResultSet rsCount = psCount.executeQuery();
            rsCount.next();
            int assigned = rsCount.getInt("assigned");

            return assigned < capacity;
        }
    }

    private boolean genderMatches(int studentId, int roomId) throws SQLException {
        String query = "SELECT s.Gender, r.Gender_Type FROM Student s, Room r WHERE s.Student_ID = ? AND r.Room_ID = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, studentId);
            ps.setInt(2, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String studentGender = rs.getString("Gender");
                String roomGender = rs.getString("Gender_Type");
                return studentGender.equalsIgnoreCase(roomGender);
            }
            return false;
        }
    }
}


