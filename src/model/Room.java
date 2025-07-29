package model;

public class Room {
    private int roomID;
    private int capacity;
    private String genderType;
    private String status;
    private int dormId;

    public Room(int roomId, int capacity, String genderType, String status, int dormId) {
        this.roomID=roomId;
        this.capacity = capacity;
        this.genderType = genderType;
        this.status = status;
        this.dormId = dormId;
    }

    // Getters
    public int getRoomId() { return roomID; }
    public int getCapacity() { return capacity; }
    public String getGenderType() { return genderType; }
    public String getStatus() { return status; }
    public int getDormId() { return dormId; }
}
