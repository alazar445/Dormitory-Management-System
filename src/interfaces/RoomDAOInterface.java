package interfaces;

import model.Room;
import java.sql.SQLException;
import java.util.List;

public interface RoomDAOInterface {

    // Get all rooms
    List<Room> getAllRooms() throws SQLException;

    // Insert a new room
    boolean insertRoom(Room room) throws SQLException;

    // Delete a room by ID
    boolean deleteRoom(int roomId) throws SQLException;
}