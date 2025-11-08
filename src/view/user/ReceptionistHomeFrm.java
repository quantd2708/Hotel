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
import view.booking.SearchFreeRoomFrm; // Import luồng đặt phòng
import view.booking.SearchCheckinBookingFrm;
import view.checkout.SearchActiveBookingFrm;

public class ReceptionistHomeFrm extends JFrame implements ActionListener {
    private JButton btnWalkInBooking, btnCheckin, btnCheckout, btnLogout;
    private User user;

    public ReceptionistHomeFrm(User user) {
        super("Receptionist Home");
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

        JLabel lblHome = new JLabel("Receptionist's Home");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(28.0f));
        listPane.add(lblHome);
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));

        // Nút 1: Đặt phòng tại quầy
        btnWalkInBooking = new JButton("Walk-in Booking");
        btnWalkInBooking.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnWalkInBooking.addActionListener(this);
        listPane.add(btnWalkInBooking);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));

        // Nút 2: Check-in
        btnCheckin = new JButton("Check-in");
        btnCheckin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCheckin.addActionListener(this);
        listPane.add(btnCheckin);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Nút 3: Check-out & Payment
        btnCheckout = new JButton("Check-out & Payment");
        btnCheckout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCheckout.addActionListener(this);
        listPane.add(btnCheckout);
        
        // Nút 4: Đăng xuất
        btnLogout = new JButton("Logout");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(this);
        listPane.add(Box.createRigidArea(new Dimension(0, 20))); // Khoảng cách
        listPane.add(btnLogout);

        this.setSize(600, 400); // Tăng chiều cao
        this.setLocationRelativeTo(null);
        this.add(listPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnWalkInBooking) {
            // Chức năng này đã hoàn thành, chỉ cần gọi
            (new SearchFreeRoomFrm(user, this)).setVisible(true);
            this.dispose();
        } 
        else if (e.getSource() == btnCheckin) {
            // Mở màn hình tìm kiếm Check-in
            (new SearchCheckinBookingFrm(user)).setVisible(true);
            this.dispose();       
        }
        else if (e.getSource() == btnCheckout) {
            (new SearchActiveBookingFrm(user)).setVisible(true);
            this.dispose();
        }
        else if (e.getSource() == btnLogout) {
            (new LoginFrm()).setVisible(true);
            this.dispose();
        }
    }
}