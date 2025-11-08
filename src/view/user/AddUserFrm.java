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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import dao.UserDAO;
import model.User;

// Dựa trên AddRoomFrm
public class AddUserFrm extends JFrame implements ActionListener {
    private JTextField txtFullName, txtUsername;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cmbPosition;
    private JButton btnSave, btnReset, btnCancel;
    private User adminUser; // Admin đang đăng nhập

    public AddUserFrm(User user) {
        super("Add new user");
        this.adminUser = user;

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Add new user");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtConfirmPassword = new JPasswordField(15);
        txtFullName = new JTextField(15);
        
        // Các vai trò có thể tạo
        String[] positions = {"seller", "receptionist", "manager", "admin"};
        cmbPosition = new JComboBox<>(positions);

        btnSave = new JButton("Save");
        btnReset = new JButton("Reset");
        btnCancel = new JButton("Cancel");

        JPanel content = new JPanel();
        content.setLayout(new GridLayout(7, 2, 5, 5));
        content.add(new JLabel("Full Name*:"));
        content.add(txtFullName);
        content.add(new JLabel("Username*:"));
        content.add(txtUsername);
        content.add(new JLabel("Password*:"));
        content.add(txtPassword);
        content.add(new JLabel("Confirm Password*:"));
        content.add(txtConfirmPassword);
        content.add(new JLabel("Position*:"));
        content.add(cmbPosition);
        
        content.add(btnSave);
        content.add(btnReset);
        content.add(btnCancel);
        content.add(new JLabel("")); // Ô trống

        pnMain.add(content);

        btnSave.addActionListener(this);
        btnReset.addActionListener(this);
        btnCancel.addActionListener(this);

        this.setContentPane(pnMain);
        this.setSize(600, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton) e.getSource();
        if (btnClicked.equals(btnReset)) {
            txtFullName.setText("");
            txtUsername.setText("");
            txtPassword.setText("");
            txtConfirmPassword.setText("");
            cmbPosition.setSelectedIndex(0);
        } else if (btnClicked.equals(btnSave)) {
            btnSaveClick();
        } else if (btnClicked.equals(btnCancel)) {
            (new ManageUserFrm(adminUser)).setVisible(true);
            this.dispose();
        }
    }

    private void btnSaveClick() {
        String fullName = txtFullName.getText();
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String position = (String) cmbPosition.getSelectedItem();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full Name, Username, and Password are required!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        User newUser = new User();
        newUser.setName(fullName);
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setPosition(position);

        UserDAO ud = new UserDAO();
        if (ud.addUser(newUser)) {
            JOptionPane.showMessageDialog(this, "User added successfully!");
            (new ManageUserFrm(adminUser)).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error: Username might already exist.");
        }
    }
}