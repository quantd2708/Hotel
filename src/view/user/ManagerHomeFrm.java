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
import view.room.ManageRoomFrm;
import view.stat.room.SelectStatFrm; // Import cho module Thống kê
import view.hotel.ManageHotelFrm;
import view.service.ManageServiceFrm;

public class ManagerHomeFrm extends JFrame implements ActionListener {
    private JButton btnHotel, btnRoom, btnStat;
    private User user;
    private JButton btnService;
    private JButton btnLogout;

    public ManagerHomeFrm(User user) {
        super("Manager home");
        this.user = user;

        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));

        JPanel lblPane = new JPanel();
        lblPane.setLayout(new BoxLayout(lblPane, BoxLayout.LINE_AXIS));
        lblPane.add(Box.createRigidArea(new Dimension(450, 0)));
        // Hiển thị tên đầy đủ (fullName)
        JLabel lblUser = new JLabel("Logged in as: " + user.getName());
        lblUser.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lblPane.add(lblUser);
        listPane.add(lblPane);
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblHome = new JLabel("Manager's home");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(28.0f));
        listPane.add(lblHome);
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));

        btnHotel = new JButton("Hotel management");
        btnHotel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnHotel.addActionListener(this);
        listPane.add(btnHotel);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));

        btnRoom = new JButton("Room management");
        btnRoom.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRoom.addActionListener(this);
        listPane.add(btnRoom);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));

        btnStat = new JButton("View statistic");
        btnStat.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnStat.addActionListener(this);
        listPane.add(btnStat);
        listPane.add(Box.createRigidArea(new Dimension(0, 10))); // Thêm khoảng cách

        // --- THÊM KHỐI NÀY (Nút Service) ---
        btnService = new JButton("Service management");
        btnService.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnService.addActionListener(this);
        listPane.add(btnService);
        // --- KẾT THÚC THÊM ---
        
        btnLogout = new JButton("Logout");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(this);
        listPane.add(Box.createRigidArea(new Dimension(0, 20))); // Thêm khoảng cách
        listPane.add(btnLogout);
        
        this.setSize(600, 400);
        this.setLocationRelativeTo(null); // Căn giữa màn hình
        this.add(listPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton) e.getSource();

        if (btnClicked.equals(btnRoom)) {
            (new ManageRoomFrm(user)).setVisible(true);
            this.dispose();
        }else if (btnClicked.equals(btnHotel)) {
            (new ManageHotelFrm(user)).setVisible(true);
            this.dispose();
        }
        // Kích hoạt nút Thống kê
        else if (btnClicked.equals(btnStat)) {
            (new SelectStatFrm(user)).setVisible(true);
            this.dispose();
        }else if (btnClicked.equals(btnService)) {
            (new ManageServiceFrm(user)).setVisible(true);
            this.dispose();
        // --- KẾT THÚC THÊM ---
            
        }else if (btnClicked.equals(btnLogout)) {
            (new LoginFrm()).setVisible(true); // Mở lại màn hình Login
            this.dispose(); // Đóng màn hình Manager
        // --- KẾT THÚC THÊM ---
            
        }
        // Các nút khác (như Hotel Management) sẽ rơi vào đây
        else {
            JOptionPane.showMessageDialog(this,
                    "This function is under construction!");
        }
    }
}

