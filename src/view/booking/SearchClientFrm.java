package view.booking;

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
// import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dao.ClientDAO;
import model.Booking;
import model.Client;
import model.User;

public class SearchClientFrm extends JFrame implements ActionListener {
    private ArrayList<Client> listClient;
    private JTextField txtKey;
    private JButton btnSearch, btnAddClient;
    private JTable tblResult;
    private User user;
    private Booking booking;
    private JFrame parentFrame; // --- THÊM DÒNG NÀY (Để lưu frame cha) ---
    private JButton btnBack;
    private JFrame homeFrame;
    
    public SearchClientFrm(User user, Booking booking, JFrame parentFrame, JFrame homeFrame) {
        super("Search client");
        this.user = user;
        this.booking = booking;
        this.parentFrame = parentFrame;
        this.homeFrame = homeFrame;
        listClient = new ArrayList<Client>();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Search Client (or Add New)");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel pnSearch = new JPanel();
        pnSearch.setLayout(new BoxLayout(pnSearch, BoxLayout.X_AXIS));
        pnSearch.add(new JLabel("Client Name: "));
        txtKey = new JTextField(15);
        pnSearch.add(txtKey);
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(this);
        pnSearch.add(btnSearch);
        pnMain.add(pnSearch);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel pnResult = new JPanel();
        pnResult.setLayout(new BoxLayout(pnResult, BoxLayout.Y_AXIS));
        tblResult = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));
        
        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblResult.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    Client selectedClient = listClient.get(row);
                    booking.setClient(selectedClient);
                    
                    // Chuyển đến màn hình xác nhận
//                    (new ConfirmBookingFrm(user, booking)).setVisible(true);
//                    dispose();
                    (new ConfirmBookingFrm(user, booking, SearchClientFrm.this, homeFrame)).setVisible(true);
                    setVisible(false);
                }
            }
        });
        
        pnResult.add(scrollPane);
        pnMain.add(pnResult);
        
        JPanel pnAdd = new JPanel();
        pnAdd.setLayout(new BoxLayout(pnAdd, BoxLayout.X_AXIS));
        
        // Nút Back (Mới)
        btnBack = new JButton("Back (Select Room)");
        btnBack.addActionListener(this);
        pnAdd.add(btnBack);

        pnAdd.add(Box.createHorizontalGlue()); // Đẩy nút Add New về bên phải
        
        btnAddClient = new JButton("Add New Client");
        btnAddClient.addActionListener(this);
        pnAdd.add(btnAddClient);
        pnMain.add(pnAdd);
        
        
        this.add(pnMain);
        this.setSize(700, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            String key = txtKey.getText();
            if (key == null) key = "";

            ClientDAO cd = new ClientDAO();
            listClient = cd.searchClient(key.trim());

            String[] columnNames = {"ID", "Full Name", "ID Card", "Address", "Tel", "Email"};
            String[][] value = new String[listClient.size()][6];
            for (int i = 0; i < listClient.size(); i++) {
                value[i][0] = listClient.get(i).getId() + "";
                value[i][1] = listClient.get(i).getFullName();
                value[i][2] = listClient.get(i).getIdCard();
                value[i][3] = listClient.get(i).getAddress();
                value[i][4] = listClient.get(i).getTel();
                value[i][5] = listClient.get(i).getEmail();
            }
            DefaultTableModel model = new DefaultTableModel(value, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tblResult.setModel(model);
        } else if (e.getSource() == btnAddClient) {
            // --- SỬA LẠI HÀM GỌI NÀY (Thêm 'this' làm frame cha) ---
            (new AddClientFrm(user, booking, this, homeFrame)).setVisible(true);
            this.setVisible(false);
            
        // --- THÊM KHỐI 'ELSE IF' NÀY (Sự kiện nút Back) ---
        } else if (e.getSource() == btnBack) {
            parentFrame.setVisible(true); // Hiển thị lại frame SearchFreeRoomFrm
            this.dispose();               // Đóng frame hiện tại
        }
        // --- KẾT THÚC THÊM ---
    }
}
