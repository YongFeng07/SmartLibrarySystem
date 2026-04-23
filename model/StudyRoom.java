package model;

import java.util.Objects;

public class StudyRoom {
    private String roomId;
    private String roomName;
    private int capacity;
    private String equipment;
    private boolean isAvailable;

    public StudyRoom(String roomId, String roomName, int capacity, String equipment) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
        this.equipment = equipment;
        this.isAvailable = true;
    }

    public String getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public int getCapacity() { return capacity; }
    public String getEquipment() { return equipment; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StudyRoom that = (StudyRoom) obj;
        return Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }

    @Override
    public String toString() {
        return roomId + "," + roomName + "," + capacity + "," + equipment + "," + isAvailable;
    }
}