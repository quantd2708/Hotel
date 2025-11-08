package view.hotel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import dao.HotelDAO;
import model.Hotel;
import model.User;
import view.user.ManagerHomeFrm;

public class ManageHotelFrm extends JFrame implements ActionListener {
    private JTextField txtName, txtAddress, txtStarLevel, txtDescription;
    private JButton btnUpdate, btnBack;
    private User user;
    private Hotel hotel; // Đối tượng hotel để lưu trữ
    
    public ManageHotelFrm(User user) {
        super("Hotel Information Management");
        this.user = user;
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Hotel Information Management");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Form điền thông tin
        JPanel pnForm = new JPanel(new GridLayout(5, 2, 10, 10));
        
        pnForm.add(new JLabel("Hotel Name:"));
        txtName = new JTextField(20);
        pnForm.add(txtName);
        
        pnForm.add(new JLabel("Address:"));
        txtAddress = new JTextField(20);
        pnForm.add(txtAddress);
        
        pnForm.add(new JLabel("Star Level (0-5):"));
        txtStarLevel = new JTextField(20);
        pnForm.add(txtStarLevel);
        
        pnForm.add(new JLabel("Description:"));
        txtDescription = new JTextField(20);
        pnForm.add(txtDescription);
        
        btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(this);
        pnForm.add(btnUpdate);
        
        btnBack = new JButton("Back");
        btnBack.addActionListener(this);
        pnForm.add(btnBack);
        
        pnMain.add(pnForm);
        
        this.setContentPane(pnMain);
        this.setSize(600, 350);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Tải thông tin khách sạn ngay khi mở
        loadHotelInfo();
    }
    
    private void loadHotelInfo() {
        HotelDAO hotelDAO = new HotelDAO();
        // Mặc định lấy ID = 1 vì là hệ thống single-hotel
        hotel = hotelDAO.getHotelInfo(1); 
        
        if (hotel != null) {
            txtName.setText(hotel.getName());
            txtAddress.setText(hotel.getAddress());
            txtStarLevel.setText(String.valueOf(hotel.getStarLevel()));
            txtDescription.setText(hotel.getDescription());
        } else {
            JOptionPane.showMessageDialog(this, "Error: Could not load hotel information. Assumed ID 1.");
            // Có thể khởi tạo hotel rỗng nếu muốn
            hotel = new Hotel();
            hotel.setId(1); // Đảm bảo ID là 1
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnUpdate) {
            try {
                // Lấy dữ liệu từ form
                hotel.setName(txtName.getText());
                hotel.setAddress(txtAddress.getText());
                hotel.setStarLevel(Integer.parseInt(txtStarLevel.getText()));
                hotel.setDescription(txtDescription.getText());
                
                // Gọi DAO để cập nhật
                HotelDAO hotelDAO = new HotelDAO();
                if (hotelDAO.updateHotelInfo(hotel)) {
                    JOptionPane.showMessageDialog(this, "Hotel information updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Could not update information.");
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Star Level must be a number.");
            }
        } else if (e.getSource() == btnBack) {
            (new ManagerHomeFrm(user)).setVisible(true);
            this.dispose();
        }
    }
}