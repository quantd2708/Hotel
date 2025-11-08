package view.booking;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import dao.ClientDAO;
import model.Booking;
import model.Client;
import model.User;

public class AddClientFrm extends JFrame implements ActionListener {
    private JTextField txtFullName, txtIdCard, txtAddress, txtTel, txtEmail, txtNote;
    private JButton btnSave, btnCancel;
    private User user;
    private Booking booking;
    private JFrame parentFrame;
    private JFrame homeFrame;
    
    
    public AddClientFrm(User user, Booking booking, JFrame parentFrame, JFrame homeFrame) {
        super("Add new client");
        this.user = user;
        this.booking = booking;
        this.parentFrame = parentFrame;
        this.homeFrame = homeFrame;
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Add New Client");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        txtFullName = new JTextField(15);
        txtIdCard = new JTextField(15);
        txtAddress = new JTextField(15);
        txtTel = new JTextField(15);
        txtEmail = new JTextField(15);
        txtNote = new JTextField(15);
        btnSave = new JButton("Save and Continue");
        btnCancel = new JButton("Cancel");

        JPanel content = new JPanel();
        content.setLayout(new GridLayout(7, 2, 5, 5)); // 7 hàng
        content.add(new JLabel("Full Name*:"));
        content.add(txtFullName);
        content.add(new JLabel("ID Card*:"));
        content.add(txtIdCard);
        content.add(new JLabel("Address:"));
        content.add(txtAddress);
        content.add(new JLabel("Telephone:"));
        content.add(txtTel);
        content.add(new JLabel("Email:"));
        content.add(txtEmail);
        content.add(new JLabel("Note:"));
        content.add(txtNote);
        content.add(btnSave);
        content.add(btnCancel);
        
        pnMain.add(content);
        
        btnSave.addActionListener(this);
        btnCancel.addActionListener(this);

        this.setContentPane(pnMain);
        this.setSize(600, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton) e.getSource();
        if (btnClicked.equals(btnSave)) {
            btnSaveClick();
        } else if (btnClicked.equals(btnCancel)) {
            // Quay lại màn hình tìm kiếm client
            parentFrame.setVisible(true); // Quay lại màn hình tìm kiếm client
            this.dispose();
        }
    }

    private void btnSaveClick() {
        String fullName = txtFullName.getText();
        String idCard = txtIdCard.getText();
        if (fullName.isEmpty() || idCard.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full Name and ID Card are required!");
            return;
        }

        Client client = new Client();
        client.setFullName(fullName);
        client.setIdCard(idCard);
        client.setAddress(txtAddress.getText());
        client.setTel(txtTel.getText());
        client.setEmail(txtEmail.getText());
        client.setNote(txtNote.getText());

        ClientDAO cd = new ClientDAO();
        cd.addClient(client); 
        // Sau khi thêm, client object sẽ có ID (do RETURN_GENERATED_KEYS)

        if (client.getId() > 0) {
            booking.setClient(client); // Gán client vừa tạo vào booking
            // Chuyển tiếp đến màn hình xác nhận
            (new ConfirmBookingFrm(user, booking, this.parentFrame, homeFrame)).setVisible(true); 
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error adding client. ID Card might already exist.");
        }
    }
}
