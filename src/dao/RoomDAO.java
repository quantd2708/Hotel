package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import model.Room;

public class RoomDAO extends DAO {

    public RoomDAO() {
        super();
    }

    /**
     * search all rooms in the tblRoom whose name contains the @key
     *
     * @param key
     * @return list of room whose name contains the @key
     */
    public ArrayList<Room> searchRoom(String key) {
        ArrayList<Room> result = new ArrayList<Room>();
        String sql = "SELECT * FROM tblRoom WHERE name LIKE ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Room rm = new Room();
                rm.setId(rs.getInt("id"));
                rm.setName(rs.getString("name"));
                rm.setType(rs.getString("type"));
                rm.setPrice(rs.getFloat("price"));
                // SỬA LỖI TẠI ĐÂY: "des" đã được đổi thành "description"
                rm.setDes(rs.getString("description")); 
                result.add(rm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * add a new @room into the DB
     *
     * @param rm
     */
    public boolean addRoom(Room rm) {
        // Giả sử hotelID luôn là 1, hoặc bạn có thể thêm trường chọn hotelID
        String sql = "INSERT INTO tblRoom(name, type, price, description, hotelID) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, rm.getName());
            ps.setString(2, rm.getType());
            ps.setFloat(3, rm.getPrice());
            ps.setString(4, rm.getDes());
            ps.setInt(5, 1); // Đặt tạm hotelID = 1

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * update the @room
     *
     * @param rm
     */
    public boolean updateRoom(Room rm) {
        // Giả sử hotelID là 1
        String sql = "UPDATE tblRoom SET name=?, type=?, price=?, description=?, hotelID=? WHERE id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, rm.getName());
            ps.setString(2, rm.getType());
            ps.setFloat(3, rm.getPrice());
            ps.setString(4, rm.getDes());
            ps.setInt(5, 1); // Đặt tạm hotelID = 1
            ps.setInt(6, rm.getId());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * delete the @room
     *
     * @param id
     */
    public boolean deleteRoom(int id) {
        // Cần kiểm tra ràng buộc khóa ngoại (ví dụ: phòng đã được đặt)
        // Mã đơn giản:
        String sql = "DELETE FROM tblRoom WHERE id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            // Lỗi 1451: Lỗi ràng buộc khóa ngoại (phòng đã được đặt)
            if(e.getErrorCode() == 1451){
                return false; // Trả về false nếu không xóa được
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<Room> searchFreeRoom(Date checkin, Date checkout) {
        ArrayList<Room> result = new ArrayList<Room>();
        String sql = "SELECT * FROM tblRoom WHERE id NOT IN (SELECT tblRoomID "
                + "FROM tblBookedRoom WHERE checkout > ? AND checkin < ?)";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, sdf.format(checkin));
            ps.setString(2, sdf.format(checkout));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Room rm = new Room();
                rm.setId(rs.getInt("id"));
                rm.setName(rs.getString("name"));
                rm.setType(rs.getString("type"));
                rm.setPrice(rs.getFloat("price"));
                rm.setDes(rs.getString("description"));
                result.add(rm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}