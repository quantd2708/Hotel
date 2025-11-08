package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.IncomeStat; 

public class IncomeStatDAO extends DAO {

    public IncomeStatDAO() {
        super();
    }

    /**
     * Lấy thống kê doanh thu theo Tháng, Quý, hoặc Năm (tùy chọn).
     * @param year Năm cần thống kê
     * @param periodType "Monthly", "Quarterly", hoặc "Yearly"
     * @return
     */
    public ArrayList<IncomeStat> getIncomeStat(int year, String periodType) {
        ArrayList<IncomeStat> result = new ArrayList<>();
        String sql = "";

        // Tùy chọn truy vấn SQL dựa trên periodType
        if (periodType.equalsIgnoreCase("Monthly")) {
            sql = "SELECT "
                + "  MONTHNAME(paymentDate) AS periodName, "
                + "  COUNT(DISTINCT b.clientID) AS totalClient, "
                + "  SUM(amount) AS totalIncome "
                + "FROM tblBill "
                + "JOIN tblBooking b ON tblBill.bookingID = b.ID "
                + "WHERE YEAR(paymentDate) = ? "
                + "GROUP BY MONTH(paymentDate), periodName "
                + "ORDER BY MONTH(paymentDate)";
                
        } else if (periodType.equalsIgnoreCase("Quarterly")) {
            sql = "SELECT "
                + "  CONCAT('Quarter ', QUARTER(paymentDate)) AS periodName, "
                + "  COUNT(DISTINCT b.clientID) AS totalClient, "
                + "  SUM(amount) AS totalIncome "
                + "FROM tblBill "
                + "JOIN tblBooking b ON tblBill.bookingID = b.ID "
                + "WHERE YEAR(paymentDate) = ? "
                + "GROUP BY QUARTER(paymentDate), periodName "
                + "ORDER BY QUARTER(paymentDate)";
                
        } else { // Yearly
            sql = "SELECT "
                + "  CAST(? AS CHAR) AS periodName, "
                + "  COUNT(DISTINCT b.clientID) AS totalClient, "
                + "  SUM(amount) AS totalIncome "
                + "FROM tblBill "
                + "JOIN tblBooking b ON tblBill.bookingID = b.ID "
                + "WHERE YEAR(paymentDate) = ? "
                + "GROUP BY periodName";
        }

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            if (periodType.equalsIgnoreCase("Yearly")) {
                ps.setInt(1, year); // Tham số 1 cho "periodName"
                ps.setInt(2, year); // Tham số 2 cho "WHERE"
            } else {
                ps.setInt(1, year); // Chỉ có 1 tham số
            }
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                IncomeStat is = new IncomeStat();
                is.setPeriodName(rs.getString("periodName"));
                is.setTotalClient(rs.getInt("totalClient"));
                is.setTotalIncome(rs.getFloat("totalIncome"));
                result.add(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}