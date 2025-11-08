    package dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import model.BookedRoom;
import model.Booking;
import model.Client;
import model.Room;

    
public class BookingDAO extends DAO{

    public BookingDAO() {
        super();
    }
    
    /**
     * Insert a new booking into the database, including its booked rooms. 
     * All are added in a single transaction.
     * @param b
     * @return
     */
    public boolean addBooking(Booking b) {
        // Sửa tên cột CSDL
        String sqlAddBooking = "INSERT INTO tblBooking(UserID, clientID, bookingdate, sellOff, note) VALUES(?,?,?,?,?)";
        String sqlAddBookedRoom = "INSERT INTO tblBookedRoom(tblBookingID, tblRoomID, checkin, checkout, price, sellOff, ischeckin)  VALUES(?,?,?,?,?,?,?)";String sqlCheckbookedRoom = "SELECT * FROM tblBookedRoom WHERE tblRoomID = ? AND checkout > ? AND checkin < ?";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean result = true;
        try {
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(sqlAddBooking,
                                     Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, b.getCreator().getId());
            ps.setInt(2, b.getClient().getId());
            ps.setString(3, sdf.format(b.getBookedDate()));
            ps.setFloat(4, b.getSaleoff());
            ps.setString(5, b.getNote());
            
            ps.executeUpdate();         
            //get id of the new inserted booking
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                b.setId(generatedKeys.getInt(1));
                
                //insert booked rooms
                for(BookedRoom br: b.getBookedRoom()) {
                    //check if the room is available at the period
                    ps = con.prepareStatement(sqlCheckbookedRoom);
                    ps.setInt(1, br.getRoom().getId());
                    ps.setString(2, sdf.format(br.getCheckin()));
                    ps.setString(3, sdf.format(br.getCheckout()));
                    
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()) {//unavailable
                        result = false;
                        try {
                            con.rollback();
                            con.setAutoCommit(true);
                        }catch(Exception ex) {
                            result = false;
                            ex.printStackTrace();
                        }
                        return result;
                    }
                    
                    //insert booked room
                    ps = con.prepareStatement(sqlAddBookedRoom,
                                     Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, b.getId());
                    ps.setInt(2, br.getRoom().getId());
                    ps.setString(3, sdf.format(br.getCheckin()));
                    ps.setString(4, sdf.format(br.getCheckout()));
                    ps.setFloat(5, br.getPrice());
                    ps.setFloat(6, br.getSaleoff());
                    ps.setBoolean(7, br.isChecked());
                    
                    ps.executeUpdate();         
                    //get id of the new inserted booking
                    generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        br.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
            con.commit(); // Hoàn tất transaction
        }catch(Exception e) {
            result = false;         
            try {               
                con.rollback();
            }catch(Exception ex) {
                result = false;
                ex.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            try {               
                con.setAutoCommit(true); // Trả về chế độ auto-commit
            }catch(Exception ex) {
                result = false;
                ex.printStackTrace();
            }
        }
        return result;
    }   
    
    /**
     * get list of booking involved the room whose @idroom is given 
     * between @startDate and @endDate
     * @param idroom
     * @param startDate
     * @param endDate
     * @return
     */
    public ArrayList<Booking> getBookingOfRoom(int idroom, Date startDate, Date endDate){
        ArrayList<Booking> result = new ArrayList<Booking>();
        
        // ====================== SỬA LỖI TẠI ĐÂY ======================
        // Cả 'a.sellOff' và 'b.sellOff' đều được viết hoa chữ 'O'
        String sql = "SELECT a.id as idbookedroom, GREATEST(a.checkin,?) as checkin, "
                + "LEAST(a.checkout,?) as checkout, a.price, a.sellOff as roomsaleoff, "
                + "b.id as idbooking, b.sellOff as bookingsaleoff,  c.id as idclient, "
                + "c.fullName, c.address, c.idcard, c.tel  "
                + "FROM tblBookedRoom a, tblBooking b, tblClient c "
                + "WHERE a.tblRoomID = ? AND a.isCheckin = 1  AND a.checkout > ? " // Đã sửa a.ischeckin
                + "AND a.checkin < ? AND b.id = a.tblBookingID "
                + "AND c.id = b.clientID";
        // ==================== KẾT THÚC SỬA LỖI ====================

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, sdf.format(startDate));
            ps.setString(2, sdf.format(endDate));
            ps.setInt(3, idroom);
            ps.setString(4, sdf.format(startDate));
            ps.setString(5, sdf.format(endDate));
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Booking b = new Booking();
                b.setId(rs.getInt("idbooking"));
                b.setSaleoff(rs.getFloat("bookingsaleoff")); // Tên alias 'bookingsaleoff' là đúng
                //client
                Client c = new Client();
                c.setId(rs.getInt("idclient"));
                c.setFullName(rs.getString("fullName"));
                c.setAddress(rs.getString("address"));
                c.setIdCard(rs.getString("idcard"));
                b.setClient(c);
                //booked room
                BookedRoom br = new BookedRoom();
                br.setId(rs.getInt("idbookedroom"));                   
                br.setSaleoff(rs.getFloat("roomsaleoff")); // Tên alias 'roomsaleoff' là đúng
                br.setPrice(rs.getFloat("price"));
                br.setCheckin(rs.getTimestamp("checkin"));
                br.setCheckout(rs.getTimestamp("checkout"));   
                b.getBookedRoom().add(br);
                result.add(b);
            }
        }catch(Exception e){
            e.printStackTrace();
        }   
        return result;
    }
    /**
     * Tìm kiếm các booking dựa trên tên khách hàng.
     * Chỉ retrieves các booking CHƯA BAO GIỜ check-in.
     * @param clientNameKey Tên khách hàng để tìm kiếm
     * @return Một ArrayList các đối tượng Booking.
     */
    public ArrayList<Booking> searchBookingByClient(String clientNameKey) {
        ArrayList<Booking> result = new ArrayList<>();
        // SQL này join 2 bảng Booking và Client
        // và loại trừ (NOT IN) tất cả booking nào đã có ít nhất 1 phòng check-in
        String sql = "SELECT b.ID, b.bookingDate, b.sellOff, b.note, "
                + "c.ID AS clientID, c.fullName, c.idCard, c.tel "
                + "FROM tblBooking b "
                + "JOIN tblClient c ON b.clientID = c.ID "
                + "WHERE c.fullName LIKE ? "
                + "AND b.ID NOT IN ("
                + "  SELECT DISTINCT tblBookingID FROM tblBookedRoom WHERE isCheckin = 1"
                + ")";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + clientNameKey + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("ID"));
                b.setBookedDate(rs.getTimestamp("bookingDate"));
                b.setSaleoff(rs.getFloat("sellOff")); // Khớp với CSDL
                b.setNote(rs.getString("note"));

                Client c = new Client();
                c.setId(rs.getInt("clientID"));
                c.setFullName(rs.getString("fullName"));
                c.setIdCard(rs.getString("idCard"));
                c.setTel(rs.getString("tel"));
                b.setClient(c);

                result.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Hủy một booking (xóa tất cả các bản ghi liên quan trong một transaction).
     * 1. Xóa tblUsedService (liên quan đến tblBookedRoom)
     * 2. Xóa tblBill (liên quan đến tblBooking)
     * 3. Xóa tblBookedRoom (liên quan đến tblBooking)
     * 4. Xóa tblBooking
     * @param bookingID ID của booking cần hủy.
     * @return true nếu thành công, false nếu có lỗi.
     */
    public boolean cancelBooking(int bookingID) {
        String sqlGetBookedRoomIDs = "SELECT ID FROM tblBookedRoom WHERE tblBookingID = ?";
        String sqlDeleteUsedService = "DELETE FROM tblUsedService WHERE bookedRoomID = ?";
        String sqlDeleteBill = "DELETE FROM tblBill WHERE bookingID = ?";
        String sqlDeleteBookedRoom = "DELETE FROM tblBookedRoom WHERE tblBookingID = ?";
        String sqlDeleteBooking = "DELETE FROM tblBooking WHERE ID = ?";

        try {
            con.setAutoCommit(false); // Bắt đầu transaction

            // 1. Lấy danh sách ID các phòng đã đặt
            ArrayList<Integer> bookedRoomIDs = new ArrayList<>();
            PreparedStatement ps = con.prepareStatement(sqlGetBookedRoomIDs);
            ps.setInt(1, bookingID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookedRoomIDs.add(rs.getInt("ID"));
            }

            // 2. Xóa Dịch vụ đã dùng (nếu có)
            if (!bookedRoomIDs.isEmpty()) {
                ps = con.prepareStatement(sqlDeleteUsedService);
                for (int brID : bookedRoomIDs) {
                    ps.setInt(1, brID);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 3. Xóa Hóa đơn (nếu có)
            ps = con.prepareStatement(sqlDeleteBill);
            ps.setInt(1, bookingID);
            ps.executeUpdate();

            // 4. Xóa các Phòng đã đặt
            ps = con.prepareStatement(sqlDeleteBookedRoom);
            ps.setInt(1, bookingID);
            ps.executeUpdate();

            // 5. Xóa Booking chính
            ps = con.prepareStatement(sqlDeleteBooking);
            ps.setInt(1, bookingID);
            ps.executeUpdate();

            con.commit(); // Hoàn tất transaction
            return true;

        } catch (Exception e) {
            try {
                con.rollback(); // Hoàn tác nếu có lỗi
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                con.setAutoCommit(true); // Trả về chế độ auto-commit
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Tìm kiếm các booking (đặt phòng) có lịch check-in (từ hôm nay trở về trước)
     * nhưng CHƯA check-in.
     * @param key Từ khóa tìm kiếm
     * @param searchType Loại tìm kiếm ("Name", "ID Card", "Phone")
     * @return Danh sách các Booking thỏa mãn.
     */
    public ArrayList<Booking> searchBookingForCheckin(String key, String searchType) {
        ArrayList<Booking> result = new ArrayList<>();
        
        String searchColumn;
        if (searchType.equalsIgnoreCase("ID Card")) {
            searchColumn = "c.idCard";
        } else if (searchType.equalsIgnoreCase("Phone")) {
            searchColumn = "c.tel";
        } else {
            searchColumn = "c.fullName"; // Mặc định
        }

        // SQL này tìm các Booking mà TỒN TẠI (EXISTS) ít nhất 1 BookedRoom
        // có ngày checkin <= ngày hiện tại VÀ isCheckin = 0
        String sql = "SELECT DISTINCT b.ID, b.bookingDate, b.sellOff, b.note, "
                + "c.ID AS clientID, c.fullName, c.idCard, c.tel "
                + "FROM tblBooking b "
                + "JOIN tblClient c ON b.clientID = c.ID "
                + "WHERE " + searchColumn + " LIKE ? "
                + "AND EXISTS ("
                + "  SELECT 1 FROM tblBookedRoom br "
                + "  WHERE br.tblBookingID = b.ID "
                + "  AND br.isCheckin = 0 "
                + "  AND DATE(br.checkin) <= CURDATE()"
                + ")";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("ID"));
                b.setBookedDate(rs.getTimestamp("bookingDate"));
                b.setSaleoff(rs.getFloat("sellOff"));
                b.setNote(rs.getString("note"));

                Client c = new Client();
                c.setId(rs.getInt("clientID"));
                c.setFullName(rs.getString("fullName"));
                c.setIdCard(rs.getString("idCard"));
                c.setTel(rs.getString("tel"));
                b.setClient(c);

                result.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy danh sách các phòng CẦN check-in (chưa checkin và đã đến hạn)
     * của một booking cụ thể.
     * @param bookingID
     * @return Danh sách các BookedRoom (đã join với Room).
     */
    public ArrayList<BookedRoom> getBookedRoomsForCheckin(int bookingID) {
        ArrayList<BookedRoom> result = new ArrayList<>();
        String sql = "SELECT br.ID, br.checkin, br.checkout, br.price, "
                + "r.ID AS roomID, r.name, r.type "
                + "FROM tblBookedRoom br "
                + "JOIN tblRoom r ON br.tblRoomID = r.ID "
                + "WHERE br.tblBookingID = ? "
                + "AND br.isCheckin = 0 "
                + "AND DATE(br.checkin) <= CURDATE()";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, bookingID);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                BookedRoom br = new BookedRoom();
                br.setId(rs.getInt("ID"));
                br.setCheckin(rs.getTimestamp("checkin"));
                br.setCheckout(rs.getTimestamp("checkout"));
                br.setPrice(rs.getFloat("price"));
                
                Room r = new Room();
                r.setId(rs.getInt("roomID"));
                r.setName(rs.getString("name"));
                r.setType(rs.getString("type"));
                br.setRoom(r);
                
                result.add(br);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * Thực hiện cập nhật trạng thái isCheckin = 1
     * @param bookedRoomID
     * @return true nếu thành công.
     */
    public boolean performCheckin(int bookedRoomID) {
        String sql = "UPDATE tblBookedRoom SET isCheckin = 1 WHERE ID = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, bookedRoomID);
            int rowsAffected = ps.executeUpdate();
            return (rowsAffected > 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Tìm kiếm các booking (đặt phòng) ĐANG Ở (đã checkin).
     * @param key Từ khóa tìm kiếm
     * @param searchType Loại tìm kiếm ("Name", "ID Card", "Phone")
     * @return Danh sách các Booking thỏa mãn.
     */
    public ArrayList<Booking> searchActiveBooking(String key, String searchType) {
        ArrayList<Booking> result = new ArrayList<>();
        
        String searchColumn;
        if (searchType.equalsIgnoreCase("ID Card")) {
            searchColumn = "c.idCard";
        } else if (searchType.equalsIgnoreCase("Phone")) {
            searchColumn = "c.tel";
        } else {
            searchColumn = "c.fullName"; // Mặc định
        }

        // Tìm các Booking mà TỒN TẠI (EXISTS) ít nhất 1 BookedRoom
        // có isCheckin = 1 VÀ chưa có trong tblBill
        String sql = "SELECT DISTINCT b.ID, b.bookingDate, b.sellOff, b.note, "
                + "c.ID AS clientID, c.fullName, c.idCard, c.tel "
                + "FROM tblBooking b "
                + "JOIN tblClient c ON b.clientID = c.ID "
                + "WHERE " + searchColumn + " LIKE ? "
                + "AND b.ID NOT IN (SELECT DISTINCT bookingID FROM tblBill) " // Chưa thanh toán
                + "AND EXISTS ("
                + "  SELECT 1 FROM tblBookedRoom br "
                + "  WHERE br.tblBookingID = b.ID "
                + "  AND br.isCheckin = 1" // Đã check-in
                + ")";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // ... (Code copy từ searchBookingForCheckin) ...
                Booking b = new Booking();
                b.setId(rs.getInt("ID"));
                b.setBookedDate(rs.getTimestamp("bookingDate"));
                b.setSaleoff(rs.getFloat("sellOff"));
                b.setNote(rs.getString("note"));
                Client c = new Client();
                c.setId(rs.getInt("clientID"));
                c.setFullName(rs.getString("fullName"));
                c.setIdCard(rs.getString("idCard"));
                c.setTel(rs.getString("tel"));
                b.setClient(c);
                result.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy danh sách các phòng ĐÃ check-in của một booking
     */
    public ArrayList<BookedRoom> getActiveBookedRooms(int bookingID) {
        ArrayList<BookedRoom> result = new ArrayList<>();
        String sql = "SELECT br.ID, br.checkin, br.checkout, br.price, br.sellOff, "
                + "r.ID AS roomID, r.name, r.type "
                + "FROM tblBookedRoom br "
                + "JOIN tblRoom r ON br.tblRoomID = r.ID "
                + "WHERE br.tblBookingID = ? "
                + "AND br.isCheckin = 1"; // Chỉ lấy phòng đã check-in
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, bookingID);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                BookedRoom br = new BookedRoom();
                br.setId(rs.getInt("ID"));
                br.setCheckin(rs.getTimestamp("checkin"));
                br.setCheckout(rs.getTimestamp("checkout"));
                br.setPrice(rs.getFloat("price"));
                br.setSaleoff(rs.getFloat("sellOff"));
                
                Room r = new Room();
                r.setId(rs.getInt("roomID"));
                r.setName(rs.getString("name"));
                r.setType(rs.getString("type"));
                br.setRoom(r);
                
                result.add(br);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
