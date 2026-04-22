package model;

public class StudyRoom {
    private String roomId;
    private String roomName;
    private int capacity;
    private String equipment; // e.g., "Projector, Whiteboard"
    private boolean isAvailable;
    
    public StudyRoom(String roomId, String roomName, int capacity, String equipment) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
        this.equipment = equipment;
        this.isAvailable = true;
    }
    
    // Getters
    public String getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public int getCapacity() { return capacity; }
    public String getEquipment() { return equipment; }
    public boolean isAvailable() { return isAvailable; }
    
    // Setters
    public void setAvailable(boolean available) { isAvailable = available; }
    
    @Override
    public String toString() {
        return roomId + "," + roomName + "," + capacity + "," + equipment + "," + isAvailable;
    }
}