package view.room;

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
import view.user.ManagerHomeFrm; // Quay về màn hình chính

public class ManageRoomFrm extends JFrame implements ActionListener {
    private JButton btnAdd, btnEdit, btnDel, btnBack; // Thêm nút Back
    private User user;

    public ManageRoomFrm(User user) {
        super("Room management");
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

        JLabel lblHome = new JLabel("Room management");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(28.0f));
        listPane.add(lblHome);
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));

        btnAdd = new JButton("Add room");
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdd.addActionListener(this);
        listPane.add(btnAdd);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));

        btnEdit = new JButton("Edit room");
        btnEdit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEdit.addActionListener(this);
        listPane.add(btnEdit);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));

        btnDel = new JButton("Delete room");
        btnDel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDel.addActionListener(this);
        listPane.add(btnDel);
        listPane.add(Box.createRigidArea(new Dimension(0, 20))); // Thêm khoảng cách

        btnBack = new JButton("Back to Home");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(this);
        listPane.add(btnBack);

        this.setSize(600, 350); // Tăng chiều cao
        this.setLocationRelativeTo(null);
        this.add(listPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton) e.getSource();

        if (btnClicked.equals(btnEdit)) {
            // "edit" mode
            (new SearchRoomFrm(user, "edit")).setVisible(true);
            this.dispose();
        } else if (btnClicked.equals(btnAdd)) {
            // Mở form thêm mới
            (new AddRoomFrm(user)).setVisible(true);
            this.dispose();
        } else if (btnClicked.equals(btnDel)) {
            // "delete" mode
            (new SearchRoomFrm(user, "delete")).setVisible(true);
            this.dispose();
        } else if (btnClicked.equals(btnBack)) {
            (new ManagerHomeFrm(user)).setVisible(true);
            this.dispose();
        }
    }
}

