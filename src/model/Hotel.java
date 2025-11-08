package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Hotel implements Serializable {
    private int id;
    private String name;
    private String address;
    private int starLevel;
    private String description;
    private ArrayList<Room> listRoom;

    public Hotel() {
        super();
        listRoom = new ArrayList<Room>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(int starLevel) {
        this.starLevel = starLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Room> getListRoom() {
        return listRoom;
    }

    public void setListRoom(ArrayList<Room> listRoom) {
        this.listRoom = listRoom;
    }
}

