package view.booking;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dao.BookingDAO;
import model.Booking;
import model.User;
import view.user.ReceptionistHomeFrm;
import javax.swing.JOptionPane;

// File này tương tự SearchBookingFrm
public class SearchCheckinBookingFrm extends JFrame implements ActionListener {
    private ArrayList<Booking> listBooking;
    private JTextField txtKey;
    private JButton btnSearch, btnBack;
    private JTable tblResult;
    private User user;
    private JComboBox<String> cmbSearchType;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public SearchCheckinBookingFrm(User user) {
        super("Search Booking for Check-in");
        this.user = user;
        listBooking = new ArrayList<>();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Search Booking for Check-in");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel lblSub = new JLabel("(Only bookings due for check-in today or earlier are shown)");
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMain.add(lblSub);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel Tìm kiếm
        JPanel pnSearch = new JPanel();
        pnSearch.setLayout(new BoxLayout(pnSearch, BoxLayout.X_AXIS));
        
        pnSearch.add(new JLabel("Search by: "));
        String[] searchOptions = {"Name", "ID Card", "Phone"};
        cmbSearchType = new JComboBox<>(searchOptions);
        pnSearch.add(cmbSearchType);
        
        pnSearch.add(Box.createRigidArea(new Dimension(10, 0)));
        
        pnSearch.add(new JLabel("Keyword: "));
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
                    Booking selectedBooking = listBooking.get(row);
                    
                    // Mở cửa sổ chi tiết các phòng cần check-in
                    (new CheckinRoomsFrm(user, selectedBooking.getId(), SearchCheckinBookingFrm.this)).setVisible(true);
                    setVisible(false); // Ẩn đi
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
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            String key = txtKey.getText();
            String searchType = (String) cmbSearchType.getSelectedItem();

            BookingDAO bd = new BookingDAO();
            listBooking = bd.searchBookingForCheckin(key.trim(), searchType);
            
            if(listBooking.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No bookings found matching criteria.");
            }

            String[] columnNames = {"Booking ID", "Client Name", "Client ID Card", "Client Tel", "Booking Date"};
            String[][] value = new String[listBooking.size()][5];
            for (int i = 0; i < listBooking.size(); i++) {
                Booking b = listBooking.get(i);
                value[i][0] = b.getId() + "";
                value[i][1] = b.getClient().getFullName();
                value[i][2] = b.getClient().getIdCard();
                value[i][3] = b.getClient().getTel();
                value[i][4] = (b.getBookedDate() != null) ? sdf.format(b.getBookedDate()) : "";
            }
            DefaultTableModel model = new DefaultTableModel(value, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tblResult.setModel(model);
        } else if (e.getSource() == btnBack) {
            (new ReceptionistHomeFrm(user)).setVisible(true);
            this.dispose();
        }
    }
}