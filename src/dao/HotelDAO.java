package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Hotel; // Sử dụng model Hotel đã có

public class HotelDAO extends DAO {

    public HotelDAO() {
        super();
    }

    /**
     * Lấy thông tin của khách sạn.
     * Vì hệ thống là single-hotel, chúng ta mặc định lấy ID = 1.
     * @param hotelID (Mặc định là 1)
     * @return đối tượng Hotel, hoặc null nếu có lỗi.
     */
    public Hotel getHotelInfo(int hotelID) {
        Hotel hotel = null;
        String sql = "SELECT * FROM tblHotel WHERE ID = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, hotelID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                hotel = new Hotel();
                hotel.setId(rs.getInt("ID"));
                hotel.setName(rs.getString("name"));
                hotel.setAddress(rs.getString("address"));
                hotel.setStarLevel(rs.getInt("starLevel"));
                hotel.setDescription(rs.getString("description"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hotel;
    }

    /**
     * Cập nhật thông tin của khách sạn.
     * @param hotel Đối tượng Hotel chứa thông tin mới.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateHotelInfo(Hotel hotel) {
        String sql = "UPDATE tblHotel SET name = ?, address = ?, starLevel = ?, description = ? WHERE ID = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, hotel.getName());
            ps.setString(2, hotel.getAddress());
            ps.setInt(3, hotel.getStarLevel());
            ps.setString(4, hotel.getDescription());
            ps.setInt(5, hotel.getId()); // Cập nhật theo ID

            int rowsAffected = ps.executeUpdate();
            return (rowsAffected > 0);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}