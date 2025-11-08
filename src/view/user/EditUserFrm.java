package view.user;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import dao.UserDAO;
import model.User;

// Dựa trên EditRoomFrm
public class EditUserFrm extends JFrame implements ActionListener {
    private User userToEdit; // User đang bị sửa
    private User adminUser; // Admin đang đăng nhập
    private JTextField txtId, txtFullName, txtUsername;
    private JComboBox<String> cmbPosition;
    private JButton btnUpdate, btnCancel;

    public EditUserFrm(User adminUser, User userToEdit) {
        super("Edit user: " + userToEdit.getUsername());
        this.adminUser = adminUser;
        this.userToEdit = userToEdit;

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Edit User");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        txtId = new JTextField(15);
        txtId.setEditable(false);
        txtUsername = new JTextField(15);
        txtFullName = new JTextField(15);
        
        String[] positions = {"seller", "receptionist", "manager", "admin"};
        cmbPosition = new JComboBox<>(positions);

        btnUpdate = new JButton("Update");
        btnCancel = new JButton("Cancel"); // Nút này sẽ quay về ManageUserFrm

        JPanel content = new JPanel();
        content.setLayout(new GridLayout(5, 2, 5, 5)); // 5 hàng
        content.add(new JLabel("ID:"));
        content.add(txtId);
        content.add(new JLabel("Full Name:"));
        content.add(txtFullName);
        content.add(new JLabel("Username:"));
        content.add(txtUsername);
        content.add(new JLabel("Position:"));
        content.add(cmbPosition);
        content.add(btnUpdate);
        content.add(btnCancel);
        pnMain.add(content);
        
        btnUpdate.addActionListener(this);
        btnCancel.addActionListener(this);

        initForm();
        this.setContentPane(pnMain);
        this.setSize(600, 300);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initForm() {
        if (userToEdit != null) {
            txtId.setText(String.valueOf(userToEdit.getId()));
            txtFullName.setText(userToEdit.getName());
            txtUsername.setText(userToEdit.getUsername());
            cmbPosition.setSelectedItem(userToEdit.getPosition());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnUpdate) {
            btnUpdateClick();
        } else if (e.getSource() == btnCancel) {
            (new ManageUserFrm(adminUser)).setVisible(true);
            this.dispose();
        }
    }

    private void btnUpdateClick() {
        userToEdit.setName(txtFullName.getText());
        userToEdit.setUsername(txtUsername.getText());
        userToEdit.setPosition((String) cmbPosition.getSelectedItem());

        UserDAO ud = new UserDAO();
        if (ud.updateUser(userToEdit)) {
            JOptionPane.showMessageDialog(this, "User updated successfully!");
            (new ManageUserFrm(adminUser)).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error: Username might already exist.");
        }
    }
}