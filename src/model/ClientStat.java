package model;

import java.io.Serializable;

// Lớp này kế thừa Client và thêm các thuộc tính thống kê
public class ClientStat extends Client implements Serializable {
    
    private int totalStayedDays;
    private float totalPayment;

    public ClientStat() {
        super();
    }

    public int getTotalStayedDays() {
        return totalStayedDays;
    }

    public void setTotalStayedDays(int totalStayedDays) {
        this.totalStayedDays = totalStayedDays;
    }

    public float getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(float totalPayment) {
        this.totalPayment = totalPayment;
    }
}

