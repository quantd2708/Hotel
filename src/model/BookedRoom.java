package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class BookedRoom implements Serializable {
    private int id;
    private Date checkin;
    private Date checkout;
    private float price;
    private float saleoff;
    private boolean isChecked; // Tên trong DB là isCheckin
    private Room room;
    private ArrayList<UsedService> usedServices;

    public BookedRoom() {
        super();
        usedServices = new ArrayList<UsedService>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCheckin() {
        return checkin;
    }

    public void setCheckin(Date checkin) {
        this.checkin = checkin;
    }

    public Date getCheckout() {
        return checkout;
    }

    public void setCheckout(Date checkout) {
        this.checkout = checkout;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getSaleoff() {
        return saleoff;
    }

    public void setSaleoff(float saleoff) {
        this.saleoff = saleoff;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ArrayList<UsedService> getUsedServices() {
        return usedServices;
    }

    public void setUsedServices(ArrayList<UsedService> usedServices) {
        this.usedServices = usedServices;
    }
}

