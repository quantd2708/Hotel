package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import model.RoomStat;

public class RoomStatDAO extends DAO {

    public RoomStatDAO() {
        super();
    }

    /**
     * Lấy thống kê doanh thu theo phòng, dựa trên NGÀY Ở (check-in/check-out)
     * và công thức: (Tiền phòng * (1 - roomSellOff) * (1 - bookingSellOff)) + Tiền dịch vụ
     *
     * @param startDate Ngày bắt đầu (tính theo ngày ở)
     * @param endDate   Ngày kết thúc (tính theo ngày ở)
     * @return
     */
    public ArrayList<RoomStat> getRoomStat(Date startDate, Date endDate) {
        ArrayList<RoomStat> result = new ArrayList<RoomStat>();
        
        // ====================== BẮT ĐẦU TRUY VẤN MỚI ======================
        // Logic mới:
        // 1. Lọc theo ngày ở (checkin/checkout)
        // 2. Tính tiền phòng = (Số ngày * Giá * (1-roomSellOff) * (1-bookingSellOff))
        // 3. Tính tiền dịch vụ = (Tiền dịch vụ) (Giả định dịch vụ không bị giảm giá chung)
        
        String sql = "SELECT "
            + "  r.id, r.name, r.type, r.description as des, "
            
            // SubQuery 1: Tính tổng số ngày đã ở (lọc theo ngày ở)
            // DATEDIFF(LEAST(checkout, @end), GREATEST(checkin, @start))
            + "  COALESCE((SELECT SUM(DATEDIFF( "
            + "                   LEAST(br.checkout, ?), "  // p1: endDate
            + "                   GREATEST(br.checkin, ?) " // p2: startDate
            + "                 )) "
            + "   FROM tblBookedRoom br "
            + "   WHERE br.tblRoomID = r.id "
            + "     AND br.isCheckin = 1 " // Chỉ tính phòng đã check-in
            + "     AND br.checkin < ? "   // p3: endDate (Overlap filter)
            + "     AND br.checkout > ? "  // p4: startDate (Overlap filter)
            + "  ), 0) AS days, " // COALESCE để trả về 0 nếu là NULL
            
            // SubQuery 2: Tính tổng thu nhập (Tiền phòng + Tiền dịch vụ)
            + "  ( "
            //   Phần A: Tiền phòng (áp dụng cả 2 lần giảm giá)
            + "    COALESCE((SELECT SUM( "
            + "                   DATEDIFF(LEAST(br.checkout, ?), GREATEST(br.checkin, ?)) " // p5, p6
            + "                   * br.price * (1 - br.sellOff/100) * (1 - b.sellOff/100) " // CÔNG THỨC MỚI
            + "                 ) "
            + "              FROM tblBookedRoom br "
            + "              JOIN tblBooking b ON br.tblBookingID = b.ID " // Join với Booking
            + "              WHERE br.tblRoomID = r.id "
            + "                AND br.isCheckin = 1 "
            + "                AND br.checkin < ? "   // p7
            + "                AND br.checkout > ? "  // p8
            + "             ), 0) "
            + "    + "
            //   Phần B: Tiền dịch vụ (chỉ áp dụng giảm giá dịch vụ)
            + "    COALESCE((SELECT SUM(us.quantity * us.price * (1 - us.sellOff/100)) "
            + "              FROM tblUsedService us "
            + "              JOIN tblBookedRoom br ON us.bookedRoomID = br.ID "
            + "              WHERE br.tblRoomID = r.id "
            + "                AND br.isCheckin = 1 " // Dịch vụ của phòng đã check-in
            + "                AND br.checkin < ? "   // p9
            + "                AND br.checkout > ? "  // p10 (Lọc theo ngày ở của phòng)
            + "             ), 0) "
            + "  ) AS income "
            
            + "FROM tblRoom r "
            + "ORDER BY income DESC, days DESC";
        // ====================== KẾT THÚC TRUY VẤN MỚI ======================
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Chỉ cần ngày
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            // Lấy chuỗi ngày
            String sDate = sdf.format(startDate);
            // Thêm 1 ngày vào endDate để bao gồm cả ngày cuối cùng (vì lọc < endDate)
            String eDate = sdf.format(new Date(endDate.getTime() + (1000 * 60 * 60 * 24)));

            // Gán 10 tham số
            ps.setString(1, eDate);  // SubQuery 1 (Days)
            ps.setString(2, sDate);
            ps.setString(3, eDate);
            ps.setString(4, sDate);
            
            ps.setString(5, eDate);  // SubQuery 2 (Room Income)
            ps.setString(6, sDate);
            ps.setString(7, eDate);
            ps.setString(8, sDate);
            
            ps.setString(9, eDate);  // SubQuery 2 (Service Income)
            ps.setString(10, sDate);
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RoomStat r = new RoomStat();
                r.setId(rs.getInt("id"));
                r.setName(rs.getString("name"));
                r.setType(rs.getString("type"));
                r.setDes(rs.getString("des"));
                
                // getInt/getFloat tự động xử lý COALESCE(NULL, 0)
                r.setTotalDay(rs.getInt("days"));
                r.setTotalIncome(rs.getFloat("income"));
                
                result.add(r); // Luôn thêm phòng (theo yêu cầu trước)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}