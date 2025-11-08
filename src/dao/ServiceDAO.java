package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Service; // Sử dụng model Service đã có

public class ServiceDAO extends DAO {

    public ServiceDAO() {
        super();
    }

    /**
     * Tìm kiếm dịch vụ theo tên
     */
    public ArrayList<Service> searchService(String key) {
        ArrayList<Service> result = new ArrayList<>();
        String sql = "SELECT * FROM tblService WHERE name LIKE ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Service s = new Service();
                s.setId(rs.getInt("ID"));
                s.setName(rs.getString("name"));
                s.setPrice(rs.getFloat("price"));
                s.setDescription(rs.getString("description"));
                
                // Ánh xạ CSDL "unit" (SQL) -> Model "unity" (Java)
                s.setUnity(rs.getString("unit")); 
                
                result.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Thêm dịch vụ mới
     */
    public boolean addService(Service s) {
        String sql = "INSERT INTO tblService(name, unit, price, description) VALUES(?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, s.getName());
            
            // Ánh xạ Model "unity" (Java) -> CSDL "unit" (SQL)
            ps.setString(2, s.getUnity()); 
            
            ps.setFloat(3, s.getPrice());
            ps.setString(4, s.getDescription());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Có thể do trùng tên
        }
        return true;
    }

    /**
     * Cập nhật dịch vụ
     */
    public boolean updateService(Service s) {
        String sql = "UPDATE tblService SET name=?, unit=?, price=?, description=? WHERE ID=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, s.getName());
            
            // Ánh xạ Model "unity" (Java) -> CSDL "unit" (SQL)
            ps.setString(2, s.getUnity()); 
            
            ps.setFloat(3, s.getPrice());
            ps.setString(4, s.getDescription());
            ps.setInt(5, s.getId());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Xóa một dịch vụ
     * Trả về false nếu dịch vụ đã được sử dụng (lỗi khóa ngoại)
     */
    public boolean deleteService(int id) {
        String sql = "DELETE FROM tblService WHERE ID=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Lỗi 1451: Lỗi ràng buộc khóa ngoại (đã có trong tblUsedService)
            if (e.getErrorCode() == 1451) {
                return false;
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }
}