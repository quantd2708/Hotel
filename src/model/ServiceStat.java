package model;

import java.io.Serializable;

// Lớp này kế thừa Service và thêm các thuộc tính thống kê
public class ServiceStat extends Service implements Serializable {
    
    private int totalQuantity;
    private float totalRevenue; // <-- Trường này

    public ServiceStat() {
        super();
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    // ============ BẠN ĐANG THIẾU CÁC HÀM NÀY ============
    public float getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(float totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    // ============ KẾT THÚC PHẦN THIẾU ============
}