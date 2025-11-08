package view.user;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.User;
import view.booking.SearchFreeRoomFrm; 
import view.booking.SearchBookingFrm;



public class SellerHomeFrm extends JFrame implements ActionListener {
    private JButton btnBookRoom, btnCancelBooking;
    private User user;
    private JButton btnLogout;
    
    public SellerHomeFrm(User user) {
        super("Seller Home");
        this.user = user;

        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));

        JPanel lblPane = new JPanel();
        lblPane.setLayout(new BoxLayout(lblPane, BoxLayout.LINE_AXIS));
        lblPane.add(Box.createRigidArea(new Dimension(450, 0)));
        JLabel lblUser = new JLabel("Logged in as: " + user.getName());
        lblUser.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lblPane.add(lblUser);
        listPane.add(lblPane);
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblHome = new JLabel("Seller's Home");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(28.0f));
        listPane.add(lblHome);
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));

        btnBookRoom = new JButton("Book Room (via phone)");
        btnBookRoom.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBookRoom.addActionListener(this);
        listPane.add(btnBookRoom);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));

        btnCancelBooking = new JButton("Cancel Booking (via phone)");
        btnCancelBooking.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCancelBooking.addActionListener(this);
        listPane.add(btnCancelBooking);

        btnLogout = new JButton("Logout");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(this);
        listPane.add(Box.createRigidArea(new Dimension(0, 20))); // Thêm khoảng cách
        listPane.add(btnLogout);    
        
        this.setSize(600, 350);
        this.setLocationRelativeTo(null); // Căn giữa
        this.add(listPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBookRoom) {
            (new SearchFreeRoomFrm(user, this)).setVisible(true);
            this.dispose();
            
        } else if (e.getSource() == btnCancelBooking) {
            // Mở frame tìm kiếm booking để hủy
            (new SearchBookingFrm(user)).setVisible(true);
            this.dispose();
        } 
        else if (e.getSource() == btnLogout) {
            (new LoginFrm()).setVisible(true); // Mở lại màn hình Login
            this.dispose(); // Đóng màn hình Seller
        }
        // ============= KẾT THÚC SỬA =============
        
        /* // Bỏ (hoặc sửa) khối 'else' nếu nó tồn tại
        else {
            JOptionPane.showMessageDialog(this,
                            "This function is under construction!");
        }
        */
    }
}

