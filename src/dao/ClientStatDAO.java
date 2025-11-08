package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import model.ClientStat; // Sử dụng model ClientStat đã có

public class ClientStatDAO extends DAO {

    public ClientStatDAO() {
        super();
    }

    /**
     * Lấy thống kê khách hàng theo doanh thu và số ngày ở
     * trong một khoảng thời gian.
     *
     * @param startDate Ngày bắt đầu (tính theo ngày check-in/check-out)
     * @param endDate   Ngày kết thúc (tính theo ngày check-in/check-out)
     * @return
     */
    public ArrayList<ClientStat> getClientStat(Date startDate, Date endDate) {
        ArrayList<ClientStat> result = new ArrayList<>();
        // Truy vấn này có tổng cộng 6 tham số (?)
        String sql = "SELECT c.ID, c.fullName, c.idCard, c.address, c.tel, c.email, "
                + "(SELECT SUM(DATEDIFF(LEAST(br.checkout, ?), GREATEST(br.checkin, ?))) " // ?1, ?2
                + " FROM tblBookedRoom br, tblBooking b"
                + " WHERE br.tblBookingID = b.ID AND b.clientID = c.ID"
                + " AND br.checkout > ? AND br.checkin < ? AND br.isCheckin = 1" // ?3, ?4
                + ") as totalDays, "
                + "(SELECT SUM(bill.amount) "
                + " FROM tblBill bill, tblBooking b "
                + " WHERE bill.bookingID = b.ID AND b.clientID = c.ID "
                + " AND bill.paymentDate BETWEEN ? AND ? " // ?5, ?6
                + ") as totalPayment "
                + "FROM tblClient c "
                + "ORDER BY totalPayment DESC, totalDays DESC";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // ====================== KHỐI ĐÃ SỬA LỖI ======================
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            // Tham số cho totalDays (4 tham số)
            ps.setString(1, sdf.format(endDate));   // ?1
            ps.setString(2, sdf.format(startDate)); // ?2
            ps.setString(3, sdf.format(startDate)); // ?3 (Lỗi cũ là endDate)
            ps.setString(4, sdf.format(endDate));   // ?4 (Lỗi cũ là startDate)
            ps.setString(5, sdf.format(startDate)); // ?5
            ps.setString(6, sdf.format(endDate));   // ?6 (Lỗi cũ là dòng này bị thiếu)

            ResultSet rs = ps.executeQuery();
        // ==================== KẾT THÚC SỬA LỖI ====================

            while (rs.next()) {
                ClientStat cs = new ClientStat();
                cs.setId(rs.getInt("id"));
                cs.setFullName(rs.getString("fullName"));
                cs.setIdCard(rs.getString("idCard"));
                cs.setAddress(rs.getString("address"));
                cs.setTel(rs.getString("tel"));
                cs.setEmail(rs.getString("email"));
                
                // Lấy dữ liệu thống kê
                cs.setTotalStayedDays(rs.getInt("totalDays"));
                cs.setTotalPayment(rs.getFloat("totalPayment"));
                
                result.add(cs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}