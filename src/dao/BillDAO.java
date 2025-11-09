package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Bill;
import model.Booking;
import model.Client;
import model.User;
import java.util.ArrayList; // Đảm bảo bạn có import này
import java.util.Date; // Đảm bảo bạn có import này
import java.sql.Timestamp; // Đảm bảo bạn có import này

public class BillDAO extends DAO {

    public BillDAO() {
        super();
    }

    /**
     * Lấy thông tin Bill (hóa đơn) dựa trên bookingID.
     * Hàm này sẽ JOIN với các bảng khác để lấy đủ thông tin
     * cho Client và User (người tạo hóa đơn).
     * @param bookingID
     * @return một đối tượng Bill đầy đủ, hoặc null nếu không tìm thấy.
     */
    public Bill getBillByBookingID(int bookingID) {
        Bill bill = null;
        String sql = "SELECT "
                + "b.ID AS billID, b.paymentDate, b.amount, b.paymentType, b.note AS billNote, "
                + "u.ID AS userID, u.fullName AS userFullName, u.position, "
                + "bk.ID AS bookingID, "
                + "c.ID AS clientID, c.fullName AS clientFullName, c.idCard, c.tel "
                + "FROM tblBill b "
                + "JOIN tblUser u ON b.userID = u.ID "
                + "JOIN tblBooking bk ON b.bookingID = bk.ID "
                + "JOIN tblClient c ON bk.clientID = c.ID "
                + "WHERE b.bookingID = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, bookingID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                bill = new Bill();
                bill.setId(rs.getInt("billID"));
                bill.setPaymentDate(rs.getTimestamp("paymentDate"));
                bill.setAmount(rs.getFloat("amount"));
                bill.setPaymentType(rs.getString("paymentType"));
                bill.setNote(rs.getString("billNote"));

                // Tạo đối tượng User (người tạo hóa đơn)
                User creator = new User();
                creator.setId(rs.getInt("userID"));
                creator.setName(rs.getString("userFullName"));
                creator.setPosition(rs.getString("position"));
                bill.setCreator(creator);

                // Tạo đối tượng Client
                Client client = new Client();
                client.setId(rs.getInt("clientID"));
                client.setFullName(rs.getString("clientFullName"));
                client.setIdCard(rs.getString("idCard"));
                client.setTel(rs.getString("tel"));

                // Tạo đối tượng Booking (chỉ chứa client)
                Booking booking = new Booking();
                booking.setId(rs.getInt("bookingID"));
                booking.setClient(client);
                bill.setBooking(booking);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bill;
    }

    /**
     * Lấy tổng số tiền đã thanh toán (bao gồm cả cọc) cho một booking.
     * @param bookingID
     * @return Tổng số tiền đã trả.
     */
    public float getTotalPaidForBooking(int bookingID) {
        float totalPaid = 0;
        String sql = "SELECT SUM(amount) AS total FROM tblBill WHERE bookingID = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, bookingID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalPaid = rs.getFloat("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalPaid;
    }

    /**
     * Lưu hóa đơn (Bill) mới vào CSDL.
     * @param bill Đối tượng Bill chứa thông tin thanh toán
     * @return true nếu thành công
     */
    public boolean addBill(Bill bill) {
        String sql = "INSERT INTO tblBill(paymentDate, amount, paymentType, note, bookingID, userID) "
                + "VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, new java.sql.Timestamp(bill.getPaymentDate().getTime()));
            ps.setFloat(2, bill.getAmount());
            ps.setString(3, bill.getPaymentType());
            ps.setString(4, bill.getNote());
            ps.setInt(5, bill.getBooking().getId());
            ps.setInt(6, bill.getCreator().getId());

            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy TẤT CẢ các hóa đơn (cọc, thanh toán) cho một booking.
     * Chỉ join với User (người tạo).
     * @param bookingID
     * @return một ArrayList các đối tượng Bill (chỉ chứa Creator).
     */
    public ArrayList<Bill> getAllBillsForBooking(int bookingID) {
        ArrayList<Bill> result = new ArrayList<>();
        // Lấy tất cả thông tin cần thiết, join với User để lấy tên người tạo
        String sql = "SELECT "
                + "b.ID AS billID, b.paymentDate, b.amount, b.paymentType, b.note AS billNote, "
                + "u.ID AS userID, u.fullName AS userFullName, u.position "
                + "FROM tblBill b "
                + "JOIN tblUser u ON b.userID = u.ID "
                + "WHERE b.bookingID = ? "
                + "ORDER BY b.paymentDate ASC"; // Sắp xếp theo ngày

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, bookingID);
            ResultSet rs = ps.executeQuery();

            // Dùng while để lấy TẤT CẢ các hóa đơn
            while (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("billID"));
                bill.setPaymentDate(rs.getTimestamp("paymentDate"));
                bill.setAmount(rs.getFloat("amount"));
                bill.setPaymentType(rs.getString("paymentType"));
                bill.setNote(rs.getString("billNote"));

                User creator = new User();
                creator.setId(rs.getInt("userID"));
                creator.setName(rs.getString("userFullName"));
                creator.setPosition(rs.getString("position"));
                bill.setCreator(creator);

                // Lưu ý: bill.getBooking() sẽ là null ở bước này
                result.add(bill);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}