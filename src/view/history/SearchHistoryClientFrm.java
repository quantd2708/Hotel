package view.history; // Hoặc gói view bạn đang dùng

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dao.ClientDAO;
import model.Client;
import model.User;
import view.history.ViewClientHistoryFrm;
/**
 * Giao diện Giai đoạn 1:
 * Tìm kiếm và chọn một khách hàng để xem lịch sử.
 */
public class SearchHistoryClientFrm extends JFrame implements ActionListener {
    private ArrayList<Client> listClient;
    private JTextField txtKey;
    private JButton btnSearch, btnBack;
    private JTable tblResult;
    private User user;
    private JFrame homeFrame; // Frame cha (ReceptionistHomeFrm)

    public SearchHistoryClientFrm(User user, JFrame homeFrame) {
        super("Search Client History");
        this.user = user;
        this.homeFrame = homeFrame;
        listClient = new ArrayList<>();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Search Client History");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel lblSub = new JLabel("Select a client to view their booking history");
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMain.add(lblSub);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel Tìm kiếm
        JPanel pnSearch = new JPanel();
        pnSearch.setLayout(new BoxLayout(pnSearch, BoxLayout.X_AXIS));
        pnSearch.add(new JLabel("Client Full Name: "));
        txtKey = new JTextField(15);
        pnSearch.add(txtKey);
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(this);
        pnSearch.add(btnSearch);
        pnMain.add(pnSearch);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        // Panel Kết quả
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
                    
                    // Mở Giai đoạn 2: Hiển thị Booking của khách này
                    // (Chúng ta sẽ tạo Giao diện 'ViewClientHistoryFrm' ở bước tiếp theo)
                    
                    // TẠM THỜI CHÚNG TA SẼ HIỂN THỊ THÔNG BÁO
                    (new ViewClientHistoryFrm(user, selectedClient, SearchHistoryClientFrm.this)).setVisible(true);
                    setVisible(false);
                    /* // Code thật sẽ là:
                    (new ViewClientHistoryFrm(user, selectedClient, SearchHistoryClientFrm.this)).setVisible(true);
                    setVisible(false);
                    */
                }
            }
        });
        
        pnResult.add(scrollPane);
        pnMain.add(pnResult);
        
        // Nút Back
        btnBack = new JButton("Back to Home");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(this);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        pnMain.add(btnBack);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        this.add(pnMain);
        this.setSize(700, 500);
        this.setLocationRelativeTo(homeFrame);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            String key = txtKey.getText();
            
            ClientDAO cd = new ClientDAO();
            // Sử dụng lại hàm searchClient (đã có)
            listClient = cd.searchClient(key.trim()); 
            
            String[] columnNames = {"ID", "Full Name", "ID Card", "Address", "Telephone", "Email"};
            String[][] value = new String[listClient.size()][6];
            for (int i = 0; i < listClient.size(); i++) {
                Client c = listClient.get(i);
                value[i][0] = c.getId() + "";
                value[i][1] = c.getFullName();
                value[i][2] = c.getIdCard();
                value[i][3] = c.getAddress();
                value[i][4] = c.getTel();
                value[i][5] = c.getEmail();
            }
            DefaultTableModel model = new DefaultTableModel(value, columnNames) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            tblResult.setModel(model);
        } else if (e.getSource() == btnBack) {
            homeFrame.setVisible(true);
            this.dispose();
        }
    }
}