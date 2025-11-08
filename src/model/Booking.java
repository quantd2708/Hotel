package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Booking implements Serializable {
    private int id;
    private Date bookedDate;
    private float saleoff;
    private String note;
    private User creator;
    private Client client;
    private ArrayList<BookedRoom> bookedRoom;

    public Booking() {
        super();
        // Quan trọng: Khởi tạo danh sách để tránh NullPointerException
        bookedRoom = new ArrayList<BookedRoom>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(Date bookedDate) {
        this.bookedDate = bookedDate;
    }

    public float getSaleoff() {
        return saleoff;
    }

    public void setSaleoff(float saleoff) {
        this.saleoff = saleoff;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ArrayList<BookedRoom> getBookedRoom() {
        return bookedRoom;
    }

    public void setBookedRoom(ArrayList<BookedRoom> bookedRoom) {
        this.bookedRoom = bookedRoom;
    }

    // Phương thức trợ giúp
    public float getTotal() {
        float total = 0;
        if(bookedRoom != null) {
            for(BookedRoom br : bookedRoom) {
                // Logic tính toán từ getBookingOfRoom
                long diffInMillies = Math.abs(br.getCheckout().getTime() - br.getCheckin().getTime());
                long diffInDays = java.util.concurrent.TimeUnit.DAYS.convert(diffInMillies, java.util.concurrent.TimeUnit.MILLISECONDS);
                
                // Logic DATEDIFF của SQL thường là (ngày cuối - ngày đầu)
                // Nếu checkin/checkout trong cùng 1 ngày, vẫn tính là 1 ngày
                if(diffInDays == 0) diffInDays = 1;

                total += (br.getPrice() - br.getSaleoff()) * diffInDays;
            }
        }
        // Áp dụng giảm giá chung của booking
        total = total * (1 - this.saleoff / 100);
        return total;
    }
}

