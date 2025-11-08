package view.user;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dao.UserDAO;
import model.User;

// File này được thiết kế dựa trên SearchRoomFrm
public class SearchUserFrm extends JFrame implements ActionListener {
    private ArrayList<User> listUser;
    private JTextField txtKey;
    private JButton btnSearch;
    private JButton btnBack;
    private JTable tblResult;
    private User user; // User đang đăng nhập (Admin)
    private String mode; // "edit" hoặc "delete"
    private SearchUserFrm mainFrm;

    public SearchUserFrm(User user, String mode) {
        super("Search user to " + mode);
        this.user = user;
        this.mode = mode;
        mainFrm = this;
        listUser = new ArrayList<User>();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Search a user to " + mode);
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel pn1 = new JPanel();
        pn1.setLayout(new BoxLayout(pn1, BoxLayout.X_AXIS));
        pn1.add(new JLabel("Full Name: "));
        txtKey = new JTextField();
        pn1.add(txtKey);
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(this);
        pn1.add(btnSearch);
        pnMain.add(pn1);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel pn2 = new JPanel();
        pn2.setLayout(new BoxLayout(pn2, BoxLayout.Y_AXIS));
        tblResult = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));

        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblResult.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    User selectedUser = listUser.get(row);

                    // Ngăn Admin tự xóa/sửa chính mình
                    if (selectedUser.getId() == user.getId()) {
                        JOptionPane.showMessageDialog(mainFrm, "Cannot " + mode + " your own account!");
                        return;
                    }

                    // Tùy theo 'mode' mà gọi hành động khác nhau
                    if (mode.equals("edit")) {
                        (new EditUserFrm(user, selectedUser)).setVisible(true);
                        mainFrm.dispose();
                    } else if (mode.equals("delete")) {
                        int confirm = JOptionPane.showConfirmDialog(mainFrm,
                                "Delete user: " + selectedUser.getName() + " (Username: " + selectedUser.getUsername() + ")?",
                                "Confirm Deletion",
                                JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            UserDAO ud = new UserDAO();
                            if (ud.deleteUser(selectedUser.getId())) {
                                JOptionPane.showMessageDialog(mainFrm, "User deleted!");
                                listUser.remove(row);
                                ((DefaultTableModel) tblResult.getModel()).removeRow(row);
                            } else {
                                JOptionPane.showMessageDialog(mainFrm, "Error: User might have existing bookings/bills.");
                            }
                        }
                    }
                }
            }
        });

        pn2.add(scrollPane);
        pnMain.add(pn2);
        btnBack = new JButton("Back");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(this);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        pnMain.add(btnBack);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        
        this.add(pnMain);
        this.setSize(600, 400); // Tăng chiều cao
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            String key = txtKey.getText();
            if (key == null) key = "";

            UserDAO ud = new UserDAO();
            listUser = ud.searchUser(key.trim());

            String[] columnNames = {"Id", "Username", "Full Name", "Position"};
            String[][] value = new String[listUser.size()][4];
            for (int i = 0; i < listUser.size(); i++) {
                value[i][0] = listUser.get(i).getId() + "";
                value[i][1] = listUser.get(i).getUsername();
                value[i][2] = listUser.get(i).getName();
                value[i][3] = listUser.get(i).getPosition();
            }
            DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tblResult.setModel(tableModel);
        }else if (e.getSource() == btnBack) {
            (new ManageUserFrm(user)).setVisible(true);
            this.dispose();
        }
    }
}