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

// Tương tự như ManageRoomFrm
public class ManageUserFrm extends JFrame implements ActionListener {
    private JButton btnAdd, btnEdit, btnDel, btnBack;
    private User user; // User đang đăng nhập (Admin)

    public ManageUserFrm(User user) {
        super("User management");
        this.user = user;

        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblHome = new JLabel("User management");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(28.0f));
        listPane.add(lblHome);
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));

        btnAdd = new JButton("Add User");
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdd.addActionListener(this);
        listPane.add(btnAdd);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));

        btnEdit = new JButton("Edit User");
        btnEdit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEdit.addActionListener(this);
        listPane.add(btnEdit);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));

        btnDel = new JButton("Delete User");
        btnDel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDel.addActionListener(this);
        listPane.add(btnDel);
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));

        btnBack = new JButton("Back to Home");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(this);
        listPane.add(btnBack);

        this.setSize(600, 350);
        this.setLocationRelativeTo(null);
        this.add(listPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton) e.getSource();

        if (btnClicked.equals(btnEdit)) {
            (new SearchUserFrm(user, "edit")).setVisible(true);
            this.dispose();
        } else if (btnClicked.equals(btnAdd)) {
            (new AddUserFrm(user)).setVisible(true);
            this.dispose();
        } else if (btnClicked.equals(btnDel)) {
            (new SearchUserFrm(user, "delete")).setVisible(true);
            this.dispose();
        } else if (btnClicked.equals(btnBack)) {
            (new AdminHomeFrm(user)).setVisible(true);
            this.dispose();
        }
    }
}