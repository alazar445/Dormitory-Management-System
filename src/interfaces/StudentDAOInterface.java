package interfaces;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import model.*;

public interface StudentDAOInterface {
    boolean isStudentAssignedToRoom(int studentId) throws SQLException;

    List<Student> getAllStudents() throws SQLException;

    boolean addStudent(Student student) throws SQLException;

    boolean updateStudent(Student student) throws SQLException;

    boolean deleteStudent(int studentId) throws SQLException;

    boolean assignRoom(int studentId, int roomId, Date checkInDate) throws SQLException;

    boolean unassignRoomByStudentId(int studentId, Date checkOutDate) throws SQLException;
}