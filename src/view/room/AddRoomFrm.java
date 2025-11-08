package view.room;

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
import dao.RoomDAO;
import model.Room;
import model.User;
import view.user.ManagerHomeFrm;

public class AddRoomFrm extends JFrame implements ActionListener {
    private JTextField txtName, txtType, txtPrice, txtDes;
    private JButton btnSave, btnReset, btnCancel;
    private User user;

    public AddRoomFrm(User user) {
        super("Add new room");
        this.user = user;

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Add new room");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        txtName = new JTextField(15);
        txtType = new JTextField(15);
        txtPrice = new JTextField(15);
        txtDes = new JTextField(15);
        btnSave = new JButton("Save");
        btnReset = new JButton("Reset");
        btnCancel = new JButton("Cancel");

        JPanel content = new JPanel();
        content.setLayout(new GridLayout(6, 2, 5, 5)); // 6 hàng, 2 cột
        content.add(new JLabel("Room name:"));
        content.add(txtName);
        content.add(new JLabel("Type (single/double/twin):"));
        content.add(txtType);
        content.add(new JLabel("Price:"));
        content.add(txtPrice);
        content.add(new JLabel("Description:"));
        content.add(txtDes);
        content.add(btnSave);
        content.add(btnReset);
        content.add(btnCancel);
        content.add(new JLabel("")); // Ô trống
        
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
            txtType.setText("");
            txtPrice.setText("");
            txtDes.setText("");
            return;
        }
        if (btnClicked.equals(btnSave)) {
            btnSaveClick();
            return;
        }
        if (btnClicked.equals(btnCancel)) {
            (new ManageRoomFrm(user)).setVisible(true);
            this.dispose();
        }
    }

    private void btnSaveClick() {
        try {
            Room room = new Room();
            room.setName(txtName.getText());
            room.setType(txtType.getText());
            room.setPrice(Float.parseFloat(txtPrice.getText()));
            room.setDes(txtDes.getText());

            if(room.getName().isEmpty() || room.getType().isEmpty() || txtPrice.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, Type, and Price are required!");
                return;
            }

            RoomDAO rd = new RoomDAO();
            if (rd.addRoom(room)) {
                JOptionPane.showMessageDialog(this,
                        "The room is successfully added!");
                (new ManageRoomFrm(user)).setVisible(true); // Quay về màn hình quản lý
                this.dispose();
            } else {
                 JOptionPane.showMessageDialog(this,
                        "Error adding room. Name might already exist.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a number!");
        }
    }
}

