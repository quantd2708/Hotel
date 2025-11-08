package model;

import java.io.Serializable;

// Lớp này kế thừa Room để lấy các thuộc tính cơ bản
// và thêm các thuộc tính dẫn xuất (tính toán)
public class RoomStat extends Room implements Serializable {
    
    private int totalDay;
    private float totalIncome;

    public RoomStat() {
        super();
    }

    public int getTotalDay() {
        return totalDay;
    }

    public void setTotalDay(int totalDay) {
        this.totalDay = totalDay;
    }

    public float getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(float totalIncome) {
        this.totalIncome = totalIncome;
    }
}

