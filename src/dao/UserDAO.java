package dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.User;

public class UserDAO extends DAO{
    
    public UserDAO() {
        super();
    }
    
    public boolean checkLogin(User user) {
        boolean result = false;
        // ĐÃ SỬA: Thêm 'ID' vào câu lệnh SELECT
        String sql = "SELECT ID, fullName, position FROM tblUser WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                // ĐÃ SỬA: Lấy ID và gán vào đối tượng user
                user.setId(rs.getInt("ID")); 
                user.setName(rs.getString("fullName"));
                user.setPosition(rs.getString("position"));
                result = true;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public ArrayList<User> searchUser(String key) {
        ArrayList<User> result = new ArrayList<>();
        // Lấy tất cả trừ mật khẩu
        String sql = "SELECT ID, username, fullName, position FROM tblUser WHERE fullName LIKE ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("ID"));
                user.setUsername(rs.getString("username"));
                user.setName(rs.getString("fullName")); // setName(fullName)
                user.setPosition(rs.getString("position"));
                result.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Thêm user mới.
     * Trả về false nếu username đã tồn tại (lỗi UNIQUE constraint)
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO tblUser(username, password, fullName, position) VALUES(?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword()); // Lưu ý: Đang lưu mật khẩu dạng clear text
            ps.setString(3, user.getName());
            ps.setString(4, user.getPosition());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Có thể do trùng username
        }
        return true;
    }

    /**
     * Cập nhật thông tin user (trừ mật khẩu)
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE tblUser SET username=?, fullName=?, position=? WHERE ID=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPosition());
            ps.setInt(4, user.getId());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Có thể do trùng username
        }
        return true;
    }

    /**
     * Xóa một user
     * Trả về false nếu user đó đã tạo hóa đơn/booking (lỗi khóa ngoại)
     */
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM tblUser WHERE ID=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            // Lỗi 1451: Lỗi ràng buộc khóa ngoại
            if (e.getErrorCode() == 1451) {
                return false;
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

