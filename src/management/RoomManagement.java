package management;

import dao.RoomDAO;
import main.dormap;
import model.Room;
import model.RoomStudentLookup;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class RoomManagement extends JFrame {
    private RoomDAO dao;
    private JTable table;
    private DefaultTableModel model;

    public RoomManagement() {
        setTitle("Room Management System");
        setSize(900, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        dao = new RoomDAO();

        model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        model.setColumnIdentifiers(new String[]{
                "Room ID", "Capacity", "Gender", "Status", "Dorm ID"
        });

        table = new JTable(model);
        loadRooms();

        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        JTextField tfCapacity = new JTextField();
        JTextField tfGender = new JTextField();
        JTextField tfStatus = new JTextField();
        JTextField tfDormId = new JTextField();
        JButton btnAdd = new JButton("Add Room");
        JButton btnDelete = new JButton("Delete Selected Room");

        JPanel goBackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton goBackBtn = new JButton("Back to Dashboard");
        JButton roomLook = new JButton("Room Look");


        formPanel.add(new JLabel("Capacity:")); formPanel.add(tfCapacity);
        formPanel.add(new JLabel("Gender:")); formPanel.add(tfGender);
        formPanel.add(new JLabel("Status:")); formPanel.add(tfStatus);
        formPanel.add(new JLabel("Dorm ID:")); formPanel.add(tfDormId);
        formPanel.add(btnAdd); formPanel.add(btnDelete);

        btnAdd.addActionListener(e -> {
            try {
                int capacity = Integer.parseInt(tfCapacity.getText());
                int dormId = Integer.parseInt(tfDormId.getText());

                if (!dao.dormExists(dormId)) {
                    JOptionPane.showMessageDialog(this, "Dorm ID does not exist.");
                    return;
                }

                Room room = new Room(0, capacity, tfGender.getText(), tfStatus.getText(), dormId);

                if (dao.insertRoom(room)) {
                    loadRooms();
                    JOptionPane.showMessageDialog(this, "Room added successfully!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for Capacity and Dorm ID.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        goBackBtn.addActionListener(e -> {
            this.setVisible(false);
            new dormap.DashboardFrame();
        });

        btnDelete.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                int roomId = (int) model.getValueAt(selected, 0);
                try {
                    if (dao.deleteRoom(roomId)) {
                        model.removeRow(selected);
                        JOptionPane.showMessageDialog(this, "model.Room deleted successfully.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting room: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a room to delete.");
            }
        });
        goBackPanel.add(goBackBtn);
        goBackPanel.add(roomLook);

        roomLook.addActionListener(e -> {
            RoomStudentLookup.showStudentsInRoom(); // correct usage
        });
        add(goBackPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);
    }

    private void loadRooms() {
        try {
            model.setRowCount(0);
            for (Room room : dao.getAllRooms()) {
                model.addRow(new Object[]{
                        room.getRoomId(),
                        room.getCapacity(),
                        room.getGenderType(),
                        room.getStatus(),
                        room.getDormId()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + e.getMessage());
        }
    }

}
