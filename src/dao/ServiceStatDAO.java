package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import model.ServiceStat; // Model mới

public class ServiceStatDAO extends DAO {

    public ServiceStatDAO() {
        super();
    }

    /**
     * Lấy thống kê doanh thu theo dịch vụ trong một khoảng thời gian
     * (dựa trên ngày thanh toán hóa đơn).
     * @param startDate
     * @param endDate
     * @return
     */
    public ArrayList<ServiceStat> getServiceStat(Date startDate, Date endDate) {
        ArrayList<ServiceStat> result = new ArrayList<>();
        
        String sql = "SELECT "
                + "s.ID, s.name, s.unit, "
                + "SUM(us.quantity) AS totalQuantity, "
                + "SUM(us.quantity * us.price * (1 - us.sellOff/100)) AS totalRevenue "
                + "FROM tblService s "
                + "JOIN tblUsedService us ON s.ID = us.serviceID "
                + "JOIN tblBookedRoom br ON us.bookedRoomID = br.ID "
                + "JOIN tblBooking b ON br.tblBookingID = b.ID "
                + "JOIN tblBill bill ON b.ID = bill.bookingID "
                + "WHERE bill.paymentDate BETWEEN ? AND ? "
                + "GROUP BY s.ID, s.name, s.unit "
                + "ORDER BY totalRevenue DESC";
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, sdf.format(startDate));
            ps.setString(2, sdf.format(endDate));
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                ServiceStat ss = new ServiceStat();
                ss.setId(rs.getInt("ID"));
                ss.setName(rs.getString("name"));
                ss.setUnity(rs.getString("unit")); // Lấy 'unit' từ CSDL
                ss.setTotalQuantity(rs.getInt("totalQuantity"));
                ss.setTotalRevenue(rs.getFloat("totalRevenue"));
                
                result.add(ss);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}