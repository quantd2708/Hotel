package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.Service;
import model.UsedService;

public class UsedServiceDAO extends DAO {

    public UsedServiceDAO() {
        super();
    }

    /**
     * Lấy tất cả dịch vụ đã sử dụng cho một phòng (đã đặt).
     * @param bookedRoomID ID của tblBookedRoom
     * @return
     */
    public ArrayList<UsedService> getUsedServices(int bookedRoomID) {
        ArrayList<UsedService> result = new ArrayList<>();
        String sql = "SELECT us.ID, us.quantity, us.price, us.sellOff, "
                + "s.ID AS serviceID, s.name, s.unit "
                + "FROM tblUsedService us "
                + "JOIN tblService s ON us.serviceID = s.ID "
                + "WHERE us.bookedRoomID = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, bookedRoomID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UsedService us = new UsedService();
                us.setId(rs.getInt("ID"));
                us.setQuantity(rs.getInt("quantity"));
                us.setPrice(rs.getFloat("price"));
                us.setSellOff(rs.getFloat("sellOff"));

                Service s = new Service();
                s.setId(rs.getInt("serviceID"));
                s.setName(rs.getString("name"));
                s.setUnity(rs.getString("unit")); // Lấy 'unit' từ CSDL
                us.setService(s);

                result.add(us);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Thêm một dịch vụ đã sử dụng vào phòng.
     * @param us Đối tượng UsedService (chứa quantity, price, sellOff)
     * @param bookedRoomID
     * @param serviceID
     * @return
     */
    public boolean addOrUpdateUsedService(UsedService us, int bookedRoomID, int serviceID) {
        // 1. Thử tìm bản ghi hiện có
        String sqlCheck = "SELECT ID, quantity FROM tblUsedService WHERE bookedRoomID = ? AND serviceID = ?";
        String sqlUpdate = "UPDATE tblUsedService SET quantity = ?, price = ?, sellOff = ? WHERE ID = ?";
        String sqlInsert = "INSERT INTO tblUsedService(quantity, price, sellOff, bookedRoomID, serviceID) VALUES(?,?,?,?,?)";

        try {
            PreparedStatement psCheck = con.prepareStatement(sqlCheck);
            psCheck.setInt(1, bookedRoomID);
            psCheck.setInt(2, serviceID);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                // ĐÃ TỒN TẠI: Thực hiện UPDATE
                int existingID = rs.getInt("ID");
                int existingQuantity = rs.getInt("quantity");

                int newQuantity = existingQuantity + us.getQuantity();

                PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
                psUpdate.setInt(1, newQuantity);
                psUpdate.setFloat(2, us.getPrice()); // Cập nhật giá
                psUpdate.setFloat(3, us.getSellOff()); // Cập nhật giảm giá
                psUpdate.setInt(4, existingID);
                psUpdate.executeUpdate();

            } else {
                // CHƯA TỒN TẠI: Thực hiện INSERT
                PreparedStatement psInsert = con.prepareStatement(sqlInsert);
                psInsert.setInt(1, us.getQuantity());
                psInsert.setFloat(2, us.getPrice());
                psInsert.setFloat(3, us.getSellOff());
                psInsert.setInt(4, bookedRoomID);
                psInsert.setInt(5, serviceID);
                psInsert.executeUpdate();
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một dịch vụ đã sử dụng (ví dụ: nhập nhầm)
     * @param usedServiceID ID của tblUsedService
     * @return
     */
    public boolean deleteUsedService(int usedServiceID) {
        String sql = "DELETE FROM tblUsedService WHERE ID = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, usedServiceID);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}