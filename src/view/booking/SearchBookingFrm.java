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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dao.BookingDAO;
import model.Booking;
import model.User;
import view.user.SellerHomeFrm;

public class SearchBookingFrm extends JFrame implements ActionListener {
    private ArrayList<Booking> listBooking;
    private JTextField txtKey;
    private JButton btnSearch, btnBack;
    private JTable tblResult;
    private User user;
    private SearchBookingFrm mainFrm;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public SearchBookingFrm(User user) {
        super("Search Booking to Cancel");
        this.user = user;
        mainFrm = this;
        listBooking = new ArrayList<>();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Search Booking to Cancel");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel lblSub = new JLabel("(Only bookings that have not been checked-in will be shown)");
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMain.add(lblSub);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

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
                    
                    int confirm = JOptionPane.showConfirmDialog(mainFrm,
                            "Are you sure you want to cancel this booking?\n"
                            + "Booking ID: " + selectedBooking.getId() + "\n"
                            + "Client: " + selectedBooking.getClient().getFullName(),
                            "Confirm Cancellation",
                            JOptionPane.YES_NO_OPTION);
                        
                    if (confirm == JOptionPane.YES_OPTION) {
                        BookingDAO bd = new BookingDAO();
                        if (bd.cancelBooking(selectedBooking.getId())) {
                            JOptionPane.showMessageDialog(mainFrm, "Booking canceled successfully!");
                            // Tải lại bảng
                            listBooking.remove(row);
                            ((DefaultTableModel)tblResult.getModel()).removeRow(row);
                        } else {
                            JOptionPane.showMessageDialog(mainFrm, "Error: Could not cancel booking.");
                        }
                    }
                }
            }
        });
        
        pnResult.add(scrollPane);
        pnMain.add(pnResult);
        
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
            if (key == null) key = "";

            BookingDAO bd = new BookingDAO();
            listBooking = bd.searchBookingByClient(key.trim());

            String[] columnNames = {"Booking ID", "Client Name", "Client ID Card", "Client Tel", "Booking Date", "Note"};
            String[][] value = new String[listBooking.size()][6];
            for (int i = 0; i < listBooking.size(); i++) {
                Booking b = listBooking.get(i);
                value[i][0] = b.getId() + "";
                value[i][1] = b.getClient().getFullName();
                value[i][2] = b.getClient().getIdCard();
                value[i][3] = b.getClient().getTel();
                value[i][4] = (b.getBookedDate() != null) ? sdf.format(b.getBookedDate()) : "";
                value[i][5] = b.getNote();
            }
            DefaultTableModel model = new DefaultTableModel(value, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tblResult.setModel(model);
        } else if (e.getSource() == btnBack) {
            (new SellerHomeFrm(user)).setVisible(true);
            this.dispose();
        }
    }
}