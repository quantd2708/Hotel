package view.user;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.Box;
import java.awt.Dimension;
import java.awt.Component;

import dao.UserDAO;
import model.User;

public class LoginFrm extends JFrame implements ActionListener {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrm() {
        super("Login");
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtPassword.setEchoChar('*');
        btnLogin = new JButton("Login");

        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width - 5, this.getSize().height - 20);
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.PAGE_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Login");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel pnUsername = new JPanel();
        pnUsername.setLayout(new FlowLayout());
        pnUsername.add(new JLabel("Username:"));
        pnUsername.add(txtUsername);
        pnMain.add(pnUsername);

        JPanel pnPass = new JPanel();
        pnPass.setLayout(new FlowLayout());
        pnPass.add(new JLabel("Password:"));
        pnPass.add(txtPassword);
        pnMain.add(pnPass);
        
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        // Thêm một panel cho button để căn giữa
        JPanel pnButton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnButton.add(btnLogin);
        pnMain.add(pnButton);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        
        btnLogin.addActionListener(this);

        this.setSize(400, 250); // Tăng chiều cao
        this.setLocationRelativeTo(null); // Căn giữa màn hình
        this.setContentPane(pnMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát khi đóng
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ((e.getSource() instanceof JButton)
                && (((JButton) e.getSource()).equals(btnLogin))) {
            User user = new User();
            user.setUsername(txtUsername.getText());
            user.setPassword(new String(txtPassword.getPassword())); // Sửa

            UserDAO ud = new UserDAO();
            if (ud.checkLogin(user)) {
                
                // SỬA LỖI: Phải truyền đối tượng 'user' đã đăng nhập vào Home Frm
                
                if (user.getPosition().equalsIgnoreCase("manager")) {
                    (new ManagerHomeFrm(user)).setVisible(true); // ĐÃ SỬA
                    this.dispose();
                } else if (user.getPosition().equalsIgnoreCase("seller")) {
                    (new SellerHomeFrm(user)).setVisible(true); // ĐÃ SỬA
                    this.dispose();
                } else if (user.getPosition().equalsIgnoreCase("admin")) {
                    (new AdminHomeFrm(user)).setVisible(true);
                    this.dispose(); 
                }else if (user.getPosition().equalsIgnoreCase("receptionist")) {
                    (new ReceptionistHomeFrm(user)).setVisible(true);
                    this.dispose();
                // ============ KẾT THÚC THÊM ============
                    
                }else {
                    JOptionPane.showMessageDialog(this,
                            "The function of the role " + user.getPosition()
                                    + " is under construction!");
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Incorrect username and/or password!");
            }
        }
    }

    public static void main(String[] args) {
        // Cài đặt Look and Feel cho đẹp hơn (tùy chọn)
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        LoginFrm myFrame = new LoginFrm();
        myFrame.setVisible(true);
    }
}

