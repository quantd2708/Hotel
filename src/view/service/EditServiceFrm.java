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

// Tương tự EditRoomFrm
public class EditServiceFrm extends JFrame implements ActionListener {
    private Service service;
    private JTextField txtId, txtName, txtUnit, txtPrice, txtDes;
    private JButton btnUpdate, btnCancel;
    private User user;
    private JFrame parentFrame; // ManageServiceFrm
    
    public EditServiceFrm(User user, Service service, JFrame parent) {
        super("Edit service: " + service.getName());
        this.user = user;
        this.service = service;
        this.parentFrame = parent;
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel lblHome = new JLabel("Edit a service");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);  
        lblHome.setFont(lblHome.getFont().deriveFont (20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));
        
        txtId = new JTextField(15);
        txtId.setEditable(false);
        txtName = new JTextField(15);
        txtUnit = new JTextField(15);
        txtPrice = new JTextField(15);
        txtDes = new JTextField(15);
        btnUpdate = new JButton("Update");
        btnCancel = new JButton("Cancel");
        
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(6, 2, 5, 5));
        content.add(new JLabel("Service ID:"));   content.add(txtId);
        content.add(new JLabel("Service Name:"));  content.add(txtName);
        content.add(new JLabel("Unit:"));   content.add(txtUnit);
        content.add(new JLabel("Price:"));  content.add(txtPrice);
        content.add(new JLabel("Description:"));    content.add(txtDes);
        content.add(btnUpdate);     content.add(btnCancel);
        pnMain.add(content);
        
        btnUpdate.addActionListener(this);
        btnCancel.addActionListener(this);
        
        initForm();     
        this.setContentPane(pnMain);
        this.setSize(600, 350);       
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initForm() {
        if (service != null) {
            txtId.setText(service.getId()+"");
            txtName.setText(service.getName());
            txtUnit.setText(service.getUnity()); // Tên model là 'unity'
            txtPrice.setText(service.getPrice()+"");
            txtDes.setText(service.getDescription());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnUpdate) {
            btnUpdateClick();
        } else if (e.getSource() == btnCancel) {
            parentFrame.setVisible(true);
            this.dispose();
        }
    }
    
    private void btnUpdateClick() {
        try {
            service.setName(txtName.getText());
            service.setUnity(txtUnit.getText()); // Tên model là 'unity'
            service.setPrice(Float.parseFloat(txtPrice.getText()));
            service.setDescription(txtDes.getText());
            
            ServiceDAO sd = new ServiceDAO();
            if (sd.updateService(service)) {
                JOptionPane.showMessageDialog(this, "Service updated successfully!");
                parentFrame.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not update service.");
            }
        } catch (NumberFormatException ex) {
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