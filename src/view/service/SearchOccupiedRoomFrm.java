package view.service;

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
import dao.BookingDAO;
import model.BookedRoom;
import model.User;
import model.Room; // Import này có thể cần
import java.text.SimpleDateFormat; // Import này cần

// Import tệp tin thứ 2
import view.service.AddServiceDialog;

public class SearchOccupiedRoomFrm extends JFrame implements ActionListener {
    private ArrayList<BookedRoom> listBookedRoom;
    private JTextField txtKey;
    private JButton btnSearch, btnBack;
    private JTable tblResult;
    private User user;
    private JFrame homeFrame; // Frame cha (ReceptionistHomeFrm)
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public SearchOccupiedRoomFrm(User user, JFrame homeFrame) {
        super("Select Occupied Room");
        this.user = user;
        this.homeFrame = homeFrame;
        listBookedRoom = new ArrayList<>();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Select Occupied Room");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel lblSub = new JLabel("(Only rooms that are checked-in and not checked-out are shown)");
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMain.add(lblSub);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel Tìm kiếm
        JPanel pnSearch = new JPanel();
        pnSearch.setLayout(new BoxLayout(pnSearch, BoxLayout.X_AXIS));
        pnSearch.add(new JLabel("Room Name: "));
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
                    BookedRoom selectedRoom = listBookedRoom.get(row);
                    
                    // Mở cửa sổ chọn dịch vụ (Bước 3)
                    // 'this' là JDialog cha
                    AddServiceDialog dialog = new AddServiceDialog(SearchOccupiedRoomFrm.this, selectedRoom.getId());
                    dialog.setVisible(true);
                    // (Không cần ẩn frame này, JDialog là pop-up)
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

            BookingDAO bd = new BookingDAO();
            // Gọi hàm DAO MỚI
            listBookedRoom = bd.searchOccupiedRooms(key.trim()); 
            
            String[] columnNames = {"BookedRoom ID", "Room Name", "Room Type", "Check-in", "Check-out"};
            String[][] value = new String[listBookedRoom.size()][5];
            for (int i = 0; i < listBookedRoom.size(); i++) {
                BookedRoom br = listBookedRoom.get(i);
                value[i][0] = br.getId() + "";
                value[i][1] = br.getRoom().getName();
                value[i][2] = br.getRoom().getType();
                value[i][3] = sdf.format(br.getCheckin());
                value[i][4] = sdf.format(br.getCheckout());
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