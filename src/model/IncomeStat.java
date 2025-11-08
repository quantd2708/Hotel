package model;

import java.io.Serializable;

public class IncomeStat implements Serializable {
    
    private String periodName; // (monthly, quartile, yearly)
    private int totalClient;
    private float totalIncome;

    public IncomeStat() {
        super();
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public int getTotalClient() {
        return totalClient;
    }

    public void setTotalClient(int totalClient) {
        this.totalClient = totalClient;
    }

    public float getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(float totalIncome) {
        this.totalIncome = totalIncome;
    }
}

