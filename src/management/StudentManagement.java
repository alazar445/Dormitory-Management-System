package management;

import dao.StudentDAO;
import main.dormap;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StudentManagement extends JFrame {
    private StudentDAO dao;
    private JTable table;
    private DefaultTableModel model;

    public StudentManagement() {
        setTitle("Student Management System");
        setSize(1000, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        dao = new StudentDAO();
       model = new DefaultTableModel() {
          public boolean isCellEditable(int row, int column) { return false; }
     };

        model.setColumnIdentifiers(new String[]{
                "Student ID", "Full Name", "Gender", "Department", "Contact Info", "Enrollment Year", "Status", "Sponsorship"
        });

        table = new JTable(model);

        loadStudents();

        JPanel buttonsPanel = new JPanel();

        JButton btnAdd = new JButton("Add Student");
        JButton btnEdit = new JButton("Edit Selected");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnAssignRoom = new JButton("Assign to Room");
        JButton btnUnassignRoom = new JButton("Unassign from Room");
        JPanel goBackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton goBackBtn = new JButton("Back to Dashboard");
        goBackPanel.add(goBackBtn);
        add(goBackPanel,  BorderLayout.NORTH);
        buttonsPanel.add(btnAdd);
        buttonsPanel.add(btnEdit);
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(btnAssignRoom);
        buttonsPanel.add(btnUnassignRoom);

        btnAdd.addActionListener(e -> openStudentForm(null));

        btnEdit.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                Student student = getStudentFromTable(selected);
                openStudentForm(student);
            } else {
                JOptionPane.showMessageDialog(this, "Select a student to edit.");
            }
        });

        goBackBtn.addActionListener(e -> {
            this.setVisible(false);
            new dormap.DashboardFrame();
        });
        btnDelete.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                int studentId = (int) model.getValueAt(selected, 0);
                try {
                    if (dao.deleteStudent(studentId)) {
                        model.removeRow(selected);
                        JOptionPane.showMessageDialog(this, "Student deleted.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a student to delete.");
            }
        });
        btnAssignRoom.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                int studentId = (int) model.getValueAt(selected, 0);
                try {
                    if (dao.isStudentAssignedToRoom(studentId)) {
                        JOptionPane.showMessageDialog(this, "Student is already assigned to a room.");
                        return;
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error checking assignment: " + ex.getMessage());
                    return;
                }

                String roomIdStr = JOptionPane.showInputDialog(this, "Enter model.Room ID to assign:");
                if (roomIdStr != null) {
                    try {
                        int roomId = Integer.parseInt(roomIdStr);
                        if (dao.assignRoom(studentId, roomId, Date.valueOf(LocalDate.now()))) {
                            JOptionPane.showMessageDialog(this, "model.Student assigned to room successfully.");
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error assigning room: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Enter a valid model.Room ID number.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a student to assign.");
            }
        });
        btnUnassignRoom.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                int studentId = (int) model.getValueAt(selected, 0);
                String checkOutDateStr = JOptionPane.showInputDialog(this, "Enter Check-Out Date (YYYY-MM-DD):");
                if (checkOutDateStr != null) {
                    try {
                        Date checkOutDate = Date.valueOf(checkOutDateStr);
                        if (dao.unassignRoomByStudentId(studentId, checkOutDate)) {
                            JOptionPane.showMessageDialog(this, "model.Student unassigned from room.");
                        } else {
                            JOptionPane.showMessageDialog(this, "model.Student is not assigned to any active room.");
                        }
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, "Enter a valid date format (YYYY-MM-DD).");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error unassigning room: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a student to unassign.");
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private Student getStudentFromTable(int row) {
        return new Student(
                (int) model.getValueAt(row, 0),
                (String) model.getValueAt(row, 1),
                (String) model.getValueAt(row, 2),
                (String) model.getValueAt(row, 3),
                (String) model.getValueAt(row, 4),
                (int) model.getValueAt(row, 5),
                (String) model.getValueAt(row, 6),
                (String) model.getValueAt(row, 7)
        );
    }

    private void loadStudents() {
        try {
            model.setRowCount(0);
            List<Student> students = dao.getAllStudents();
            for (Student s : students) {
                model.addRow(new Object[]{
                        s.getStudentId(),
                        s.getFullName(),
                        s.getGender(),
                        s.getDepartment(),
                        s.getContactInfo(),
                        s.getEnrollmentYear(),
                        s.getCurrentStatus(),
                        s.getSponsorshipStatus()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }

    private void openStudentForm(Student student) {
        JDialog dialog = new JDialog(this, student == null ? "Add model.Student" : "Edit model.Student", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(9, 2));

        JTextField tfFullName = new JTextField(student == null ? "" : student.getFullName());
        JComboBox<String> cbGender = new JComboBox<>(new String[]{"Male", "Female"});
        if (student != null) cbGender.setSelectedItem(student.getGender());
        JTextField tfDepartment = new JTextField(student == null ? "" : student.getDepartment());
        JTextField tfContact = new JTextField(student == null ? "" : student.getContactInfo());
        JTextField tfEnrollYear = new JTextField(student == null ? "" : String.valueOf(student.getEnrollmentYear()));
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Active", "Graduated", "Withdrawn"});
        if (student != null) cbStatus.setSelectedItem(student.getCurrentStatus());
        JComboBox<String> cbSponsorship = new JComboBox<>(new String[]{"Government", "Self"});
        if (student != null) cbSponsorship.setSelectedItem(student.getSponsorshipStatus());

        dialog.add(new JLabel("Full Name:")); dialog.add(tfFullName);
        dialog.add(new JLabel("Gender:")); dialog.add(cbGender);
        dialog.add(new JLabel("Department:")); dialog.add(tfDepartment);
        dialog.add(new JLabel("Contact Info:")); dialog.add(tfContact);
        dialog.add(new JLabel("Enrollment Year:")); dialog.add(tfEnrollYear);
        dialog.add(new JLabel("Status:")); dialog.add(cbStatus);
        dialog.add(new JLabel("Sponsorship:")); dialog.add(cbSponsorship);

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        dialog.add(btnSave);
        dialog.add(btnCancel);

        btnSave.addActionListener(e -> {
            try {
                String fullName = tfFullName.getText().trim();
                String gender = (String) cbGender.getSelectedItem();
                String dept = tfDepartment.getText().trim();
                String contact = tfContact.getText().trim();
                int enrollYear = Integer.parseInt(tfEnrollYear.getText().trim());
                String status = (String) cbStatus.getSelectedItem();
                String sponsorship = (String) cbSponsorship.getSelectedItem();

                if (fullName.isEmpty() || dept.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields.");
                    return;
                }

                if(enrollYear< 2010){
                    JOptionPane.showMessageDialog(dialog, "Please fill appropriate year.");
                    return;
                }

                if (student == null) {
                    Student newStudent = new Student(0, fullName, gender, dept, contact, enrollYear, status, sponsorship);
                    if (dao.addStudent(newStudent)) {
                        JOptionPane.showMessageDialog(dialog, "model.Student added.");
                        loadStudents();
                        dialog.dispose();
                    }
                } else {
                    student.setFullName(fullName);
                    student.setGender(gender);
                    student.setDepartment(dept);
                    student.setContactInfo(contact);
                    student.setEnrollmentYear(enrollYear);
                    student.setCurrentStatus(status);
                    student.setSponsorshipStatus(sponsorship);

                    if (dao.updateStudent(student)) {
                        JOptionPane.showMessageDialog(dialog, "model.Student updated.");
                        loadStudents();
                        dialog.dispose();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Enrollment year must be a number.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving student: " + ex.getMessage());
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
}
