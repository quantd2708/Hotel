package view.service;

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
import dao.ServiceDAO;
import model.Service;
import model.User;

public class AddServiceFrm extends JFrame implements ActionListener {
    private JTextField txtName, txtUnit, txtPrice, txtDes;
    private JButton btnSave, btnReset, btnCancel;
    private User user;
    private JFrame parentFrame; // Frame cha (ManageServiceFrm)

    public AddServiceFrm(User user, JFrame parent) {
        super("Add new service");
        this.user = user;
        this.parentFrame = parent;

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Add new service");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        txtName = new JTextField(15);
        txtUnit = new JTextField(15);
        txtPrice = new JTextField(15);
        txtDes = new JTextField(15);
        btnSave = new JButton("Save");
        btnReset = new JButton("Reset");
        btnCancel = new JButton("Cancel");

        JPanel content = new JPanel();
        content.setLayout(new GridLayout(6, 2, 5, 5));
        content.add(new JLabel("Service Name:"));
        content.add(txtName);
        content.add(new JLabel("Unit (e.g., 'item', 'hour', 'kg'):"));
        content.add(txtUnit);
        content.add(new JLabel("Price:"));
        content.add(txtPrice);
        content.add(new JLabel("Description:"));
        content.add(txtDes);
        content.add(btnSave);
        content.add(btnReset);
        content.add(btnCancel);
        content.add(new JLabel("")); 
        
        pnMain.add(content);
        
        btnSave.addActionListener(this);
        btnReset.addActionListener(this);
        btnCancel.addActionListener(this);

        this.setContentPane(pnMain);
        this.setSize(600, 350);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton) e.getSource();
        if (btnClicked.equals(btnReset)) {
            txtName.setText("");
            txtUnit.setText("");
            txtPrice.setText("");
            txtDes.setText("");
        } else if (btnClicked.equals(btnSave)) {
            btnSaveClick();
        } else if (btnClicked.equals(btnCancel)) {
            parentFrame.setVisible(true);
            this.dispose();
        }
    }

    private void btnSaveClick() {
        try {
            Service service = new Service();
            service.setName(txtName.getText());
            service.setUnity(txtUnit.getText()); // Tên model là 'unity'
            service.setPrice(Float.parseFloat(txtPrice.getText()));
            service.setDescription(txtDes.getText());

            if(service.getName().isEmpty() || service.getUnity().isEmpty() || txtPrice.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, Unit, and Price are required!");
                return;
            }

            ServiceDAO sd = new ServiceDAO();
            if (sd.addService(service)) {
                JOptionPane.showMessageDialog(this, "Service added successfully!");
                parentFrame.setVisible(true);
                this.dispose();
            } else {
                 JOptionPane.showMessageDialog(this, "Error: Service name might already exist.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a number!");
        }
    }
    
    // Đảm bảo frame cha được hiển thị lại khi đóng
    @Override
    public void dispose() {
        parentFrame.setVisible(true);
        super.dispose();
    }
}