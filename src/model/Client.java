package model;

import java.io.Serializable;

public class Client implements Serializable {
    private int id;
    // Đổi tên 'name' thành 'fullName' để khớp với CSDL
    private String fullName; 
    private String idCard;
    private String address;
    private String tel;
    private String email;
    private String note;

    public Client() {
        super();
    }

    public Client(String fullName, String idCard, String address, String tel, String email, String note) {
        super();
        this.fullName = fullName;
        this.idCard = idCard;
        this.address = address;
        this.tel = tel;
        this.email = email;
        this.note = note;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Đổi tên getter/setter
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

