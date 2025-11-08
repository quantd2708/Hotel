package view.service;

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

// File này tương tự ManageRoomFrm
public class ManageServiceFrm extends JFrame implements ActionListener {
    private JButton btnAdd, btnEdit, btnDel, btnBack;
    private User user;

    public ManageServiceFrm(User user) {
        super("Service management");
        this.user = user;

        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblHome = new JLabel("Service management");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(28.0f));
        listPane.add(lblHome);
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));

        btnAdd = new JButton("Add service");
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdd.addActionListener(this);
        listPane.add(btnAdd);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));

        btnEdit = new JButton("Edit service");
        btnEdit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEdit.addActionListener(this);
        listPane.add(btnEdit);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));

        btnDel = new JButton("Delete service");
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
            // "edit" mode
            (new SearchServiceFrm(user, "edit", this)).setVisible(true);
            this.setVisible(false);
        } else if (btnClicked.equals(btnAdd)) {
            // Mở form thêm mới
            (new AddServiceFrm(user, this)).setVisible(true);
            this.setVisible(false);
        } else if (btnClicked.equals(btnDel)) {
            // "delete" mode
            (new SearchServiceFrm(user, "delete", this)).setVisible(true);
            this.setVisible(false);
        } else if (btnClicked.equals(btnBack)) {
            (new ManagerHomeFrm(user)).setVisible(true);
            this.dispose();
        }
    }
}