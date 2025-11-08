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
import javax.swing.JPanel;
import model.User;
import view.room.ManageRoomFrm;
import view.stat.room.SelectStatFrm;



public class AdminHomeFrm extends JFrame implements ActionListener {
    private JButton btnManageUser;
    private User user;
    private JButton btnLogout;
    
    public AdminHomeFrm(User user) {
        super("Admin Home");
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

        JLabel lblHome = new JLabel("Admin's Home");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(28.0f));
        listPane.add(lblHome);
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));

        btnManageUser = new JButton("User Management");
        btnManageUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnManageUser.addActionListener(this);
        listPane.add(btnManageUser);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Thêm các nút quản lý khác nếu cần (vd: backup db...)
        btnLogout = new JButton("Logout");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(this);
        listPane.add(Box.createRigidArea(new Dimension(0, 20))); // Thêm khoảng cách
        listPane.add(btnLogout);
        
        this.setSize(600, 350);
        this.setLocationRelativeTo(null);
        this.add(listPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnManageUser) {
            (new ManageUserFrm(user)).setVisible(true);
            this.dispose();
        }
        else if (e.getSource() == btnLogout) {
            (new LoginFrm()).setVisible(true); // Mở lại màn hình Login
            this.dispose(); // Đóng màn hình Admin
        }
    }
}