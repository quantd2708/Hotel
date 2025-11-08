package model;

import java.io.Serializable;

public class UsedService implements Serializable {
    private int id;
    private int quantity;
    private float price;
    private float sellOff;
    private Service service;
    // Không cần BookedRoom ở đây để tránh tham chiếu vòng
    
    public UsedService() {
        super();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getSellOff() {
        return sellOff;
    }

    public void setSellOff(float sellOff) {
        this.sellOff = sellOff;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}

