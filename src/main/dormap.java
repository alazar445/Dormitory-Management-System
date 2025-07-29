package main;

import management.RoomManagement;
import management.StudentManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



import abstracts.DBConfigBaseAbs;  // <-- Import the abstract class

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class dormap {
    // Session management
    static class Session {
        private static boolean loggedIn = false;
        public static void login() { loggedIn = true; }
        public static void logout() { loggedIn = false; }
        public static boolean isLoggedIn() { return loggedIn; }
    }

    // Login window
    static class LoginFrame extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;




        public LoginFrame() {
            setTitle("Dorm Login");
            setSize(350, 200);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel userLabel = new JLabel("Username:");
            usernameField = new JTextField(15);
            JLabel passLabel = new JLabel("Password:");
            passwordField = new JPasswordField(15);
            JButton loginBtn = new JButton("Login");


            gbc.gridx = 0; gbc.gridy = 0;
            add(userLabel, gbc);
            gbc.gridx = 1;
            add(usernameField, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            add(passLabel, gbc);
            gbc.gridx = 1;
            add(passwordField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            add(loginBtn, gbc);
//            gbc.gridy = 3; // next row
//            add(generateReportButton, gbc);

            loginBtn.addActionListener(e -> {
                String user = usernameField.getText();
                String pass = new String(passwordField.getPassword());
                if (user.equals("admin") && pass.equals("admin123")) {
                    Session.login();
                    dispose();
                    new DashboardFrame();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.");
                }
            });

            setVisible(true);
        }
    }

    // Dashboard window
    public static class DashboardFrame extends JFrame {
        public DashboardFrame() {
            if (!Session.isLoggedIn()) {
                JOptionPane.showMessageDialog(null, "Access Denied. Please login first.");
                new LoginFrame();
                dispose();
                return;
            }
            setTitle("Dashboard");
            setSize(400, 300);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            JLabel welcomeLabel = new JLabel("Welcome, Admin!", SwingConstants.CENTER);
            add(welcomeLabel, BorderLayout.NORTH);

            JPanel menuPanel = new JPanel(new GridLayout(4, 1));
            JButton studentsBtn = new JButton("Manage Students");
            JButton roomsBtn = new JButton("Manage Rooms");
            JButton maintenanceBtn = new JButton("Maintenance Logs");
            JButton logoutBtn = new JButton("Logout");

            JButton generateReportButton = new JButton("Generate Maintenance Report");

            generateReportButton.addActionListener(e -> {
                String filePath = "Maintenance_Report.txt"; // or choose path with JFileChooser
                util.MaintenanceReportGenerator.generateReport(filePath);
                JOptionPane.showMessageDialog(null, "Report saved to: " + filePath);
            });



            menuPanel.add(studentsBtn);
            menuPanel.add(roomsBtn);
            menuPanel.add(maintenanceBtn);

            menuPanel.add(logoutBtn);
            menuPanel.add(generateReportButton);

            add(menuPanel, BorderLayout.CENTER);





            studentsBtn.addActionListener(e -> {
                this.setVisible(false);             // optional: hide dashboard
                new StudentManagement().setVisible(true);
            });


            roomsBtn.addActionListener(e -> {
                this.setVisible(false);
                new RoomManagement().setVisible(true);
            });

            maintenanceBtn.addActionListener(e -> {
                setVisible(false);
                new MaintenanceGUI();
            });
            logoutBtn.addActionListener(e -> {
                Session.logout();
                dispose();
                new LoginFrame();
            });

            setVisible(true);
        }
    }

    // Maintenance GUI
    static class MaintenanceGUI extends JFrame {
        JTextField txtRoomId, txtReportedBy;
        JTextArea txtDesc;
        JComboBox<String> statusBox, filterStatusBox;
        JTable table;
        DefaultTableModel model;

        public MaintenanceGUI() {
            setTitle("Maintenance Request Management");
            setLayout(new BorderLayout());
            setSize(900, 600);
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            // Input panel
            JPanel inputPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JPanel goBackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton goBackBtn = new JButton("Back to Dashboard");
            goBackPanel.add(goBackBtn);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            inputPanel.add(goBackPanel, gbc);

            txtRoomId = new JTextField(15);
            txtDesc = new JTextArea(3, 15);
            txtReportedBy = new JTextField(15);
            statusBox = new JComboBox<>(new String[]{"Pending", "In Progress", "Resolved"});

            String[] labels = {"Room ID:", "Description:", "Reported By:", "Status:"};
            Component[] fields = {txtRoomId, txtDesc, txtReportedBy, statusBox};
            gbc.gridwidth = 1;
            for (int i = 0; i < labels.length; i++) {
                gbc.gridx = 0; gbc.gridy = i + 1; gbc.insets = new Insets(5, 5, 5, 5);
                inputPanel.add(new JLabel(labels[i]), gbc);
                gbc.gridx = 1;
                inputPanel.add(fields[i], gbc);
            }

            JButton addBtn = new JButton("Add Request");
            JButton refreshBtn = new JButton("Refresh Table");
            JButton updateBtn = new JButton("Update Status");
            JButton deleteBtn = new JButton("Delete Request");

            gbc.gridx = 0; gbc.gridy = 5;
            inputPanel.add(addBtn, gbc);
            gbc.gridx = 1;
            inputPanel.add(refreshBtn, gbc);
            gbc.gridx = 2;
            inputPanel.add(updateBtn, gbc);
            gbc.gridx = 0; gbc.gridy = 6;
            inputPanel.add(deleteBtn, gbc);
            deleteBtn.addActionListener(e -> deleteRequest()); //Nahom edit:
            add(inputPanel, BorderLayout.NORTH);


            model = new DefaultTableModel() {
                public boolean isCellEditable(int row, int column) { return false; }
            };
            model.setColumnIdentifiers(new String[]{
                    "Request ID", "Room ID", "Description", "Status", "Date Reported", "Reported By", "Resolution Notes"
            });

            JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            table = new JTable(model);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            JScrollPane sc = new JScrollPane(table);

            tablePanel.add(sc);
            add(tablePanel, BorderLayout.CENTER);

            JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            filterStatusBox = new JComboBox<>(new String[]{"All", "Pending", "In Progress", "Resolved"});
            JButton filterBtn = new JButton("Filter");
            filterPanel.add(new JLabel("Filter by Status:"));
            filterPanel.add(filterStatusBox);
            filterPanel.add(filterBtn);
            add(filterPanel, BorderLayout.SOUTH);

            // Listeners
            goBackBtn.addActionListener(e -> {
                this.setVisible(false);
                new DashboardFrame();
            });
            addBtn.addActionListener(e -> addMaintenanceRequest());
            refreshBtn.addActionListener(e -> loadRequests(null));
            updateBtn.addActionListener(e -> updateSelectedRequest());
            filterBtn.addActionListener(e -> {
                String status = (String) filterStatusBox.getSelectedItem();
                loadRequests("All".equals(status) ? null : status);
            });

            loadRequests(null);
            setVisible(true);
        }

        void addMaintenanceRequest() {
            try (Connection con = DBConnection.getConnection()) {
                LocalDate today = LocalDate.now();
                Date sqlDate = Date.valueOf(today);
                String roomIdStr = txtRoomId.getText().trim();
                String reportedByStr = txtReportedBy.getText().trim();
                String description = txtDesc.getText().trim();
                String status = (String) statusBox.getSelectedItem();

                if (roomIdStr.isEmpty() || reportedByStr.isEmpty() || description.isEmpty() || status == null) {
                    JOptionPane.showMessageDialog(this, "Please fill in all the fields.");
                    return;
                }
//
                try {
                    int roomId = Integer.parseInt(roomIdStr);
                    int reportedBy = Integer.parseInt(reportedByStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "IDs must be numeric.");
                    return;
                }
                int roomId = Integer.parseInt(roomIdStr);
                int reportedBy = Integer.parseInt(reportedByStr);

                // Check room exists
                PreparedStatement checkRoom = con.prepareStatement("SELECT 1 FROM Room WHERE Room_ID = ?");
                checkRoom.setInt(1, roomId);
                if (!checkRoom.executeQuery().next()) {
                    JOptionPane.showMessageDialog(this, "Room ID not found.");
                    return;
                }
                // Check student exists
                PreparedStatement checkStudent = con.prepareStatement("SELECT 1 FROM Student WHERE Student_ID = ?");
                checkStudent.setInt(1, reportedBy);
                if (!checkStudent.executeQuery().next()) {
                    JOptionPane.showMessageDialog(this, "Student ID not found.");
                    return;
                }

                // Insert; Resolution_Notes default NULL
                PreparedStatement stmt = con.prepareStatement(
                        "INSERT INTO Maintenance_Request (Room_ID, Description, Date_Reported, Reported_By, Status) VALUES (?, ?, ?, ?, ?)"
                );
                stmt.setInt(1, roomId);
                stmt.setString(2, description);
                stmt.setDate(3, sqlDate);
                stmt.setInt(4, reportedBy);
                stmt.setString(5, status);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Request added.");
                loadRequests(null);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }

        void loadRequests(String filterStatus) {
            try (Connection con = DBConnection.getConnection()) {
                model.setRowCount(0);
                String baseSql = "SELECT Request_ID, Room_ID, Description, Status, Date_Reported, Reported_By, Resolution_Notes "
                        + "FROM Maintenance_Request";
                PreparedStatement stmt;
                if (filterStatus == null) {
                    stmt = con.prepareStatement(baseSql);
                } else {
                    stmt = con.prepareStatement(baseSql + " WHERE Status = ?");
                    stmt.setString(1, filterStatus);
                }
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    // Get IDs and other fields as strings:
                    String reqIdStr = rs.getString("Request_ID");
                    String roomIdStr = rs.getString("Room_ID");
                    String desc = rs.getString("Description");
                    String status = rs.getString("Status");
                    // Date: convert to string for display
                    Date dateRep = rs.getDate("Date_Reported");
                    String dateRepStr = (dateRep != null) ? dateRep.toString() : null;
                    String repByStr = rs.getString("Reported_By");
                    String resolution = rs.getString("Resolution_Notes"); // may be null

                    model.addRow(new Object[]{
                            reqIdStr, roomIdStr, desc, status, dateRepStr, repByStr, resolution
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage());
            }
        }

        void updateSelectedRequest() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a row to update.");
                return;
            }
            // IDs are stored as strings in the model
            String requestIdStr = model.getValueAt(row, 0).toString();
            int requestId;
            try {
                requestId = Integer.parseInt(requestIdStr.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Request ID: " + requestIdStr);
                return;
            }
            String currentStatus = model.getValueAt(row, 3).toString();
            String[] statuses = {"Pending", "In Progress", "Resolved"};
            String newStatus = (String) JOptionPane.showInputDialog(
                    this, "New status:", "Update Status",
                    JOptionPane.PLAIN_MESSAGE, null, statuses, currentStatus
            );
            if (newStatus == null || newStatus.equals(currentStatus)) {
                return;
            }

            try (Connection con = DBConnection.getConnection()) {
                if ("Resolved".equals(newStatus)) {
                    String notes = JOptionPane.showInputDialog(this, "Enter resolution notes:");
                    if (notes == null) {
                        return;
                    }
                    PreparedStatement stmt = con.prepareStatement(
                            "UPDATE Maintenance_Request SET Status = ?, Resolution_Notes = ? WHERE Request_ID = ?"
                    );
                    stmt.setString(1, newStatus);
                    stmt.setString(2, notes);
                    stmt.setInt(3, requestId);
                    stmt.executeUpdate();
                } else {
                    PreparedStatement stmt = con.prepareStatement(
                            "UPDATE Maintenance_Request SET Status = ?, Resolution_Notes = NULL WHERE Request_ID = ?"
                    );
                    stmt.setString(1, newStatus);
                    stmt.setInt(2, requestId);
                    stmt.executeUpdate();
                }
                loadRequests(null);
                JOptionPane.showMessageDialog(this, "Status updated.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage());
            }
        }

        void deleteRequest() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a row to delete.");
                return;
            }
            String requestIdStr = model.getValueAt(row, 0).toString();
            int requestId;
            try {
                requestId = Integer.parseInt(requestIdStr.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Request ID: " + requestIdStr);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this request?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement stmt = con.prepareStatement(
                        "DELETE FROM Maintenance_Request WHERE Request_ID = ?"
                );
                stmt.setInt(1, requestId);
                stmt.executeUpdate();
                loadRequests(null);
                JOptionPane.showMessageDialog(this, "Request deleted.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);


    }
}

// main.DBConnection class in same file or separate file
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