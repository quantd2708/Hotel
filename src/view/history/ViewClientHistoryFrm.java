package view.history;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import model.Client;
import model.User;
import view.history.ViewBookingBillsFrm;
import view.history.ViewBookingDetailsFrm;
/**
 * Giao diện Giai đoạn 2:
 * Hiển thị lịch sử booking của một khách hàng cụ thể.
 */
public class ViewClientHistoryFrm extends JFrame implements ActionListener {
    private ArrayList<Booking> listBooking;
    private JTextField txtStartDate, txtEndDate;
    private JButton btnSearch, btnBack;
    private JTable tblResult;
    private User user;
    private Client client;
    private JFrame parentFrame; // Frame cha (SearchHistoryClientFrm)
    
    private BookingDAO bookingDAO;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ViewClientHistoryFrm(User user, Client client, JFrame parentFrame) {
        super("Booking History for: " + client.getFullName());
        this.user = user;
        this.client = client;
        this.parentFrame = parentFrame;
        this.listBooking = new ArrayList<>();
        this.bookingDAO = new BookingDAO();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblTitle = new JLabel("Booking History for: " + client.getFullName());
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel lblSub = new JLabel("Double-click a booking to see options (View Bill / View Detail)");
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMain.add(lblSub);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel Lọc thời gian
        JPanel pnSearch = new JPanel();
        pnSearch.setLayout(new BoxLayout(pnSearch, BoxLayout.X_AXIS));
        pnSearch.add(new JLabel("From (yyyy-MM-dd): "));
        txtStartDate = new JTextField(10);
        pnSearch.add(txtStartDate);
        pnSearch.add(new JLabel(" To (yyyy-MM-dd): "));
        txtEndDate = new JTextField(10);
        pnSearch.add(txtEndDate);
        btnSearch = new JButton("Filter");
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
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 300));
        
        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Chỉ xử lý khi click đúp
                if (e.getClickCount() == 2) {
                    int row = tblResult.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        Booking selectedBooking = listBooking.get(row);
                        
                        // Mở Giai đoạn 3: Hiển thị các tùy chọn
                        showOptionsForBooking(selectedBooking);
                    }
                }
            }
        });
        
        pnResult.add(scrollPane);
        pnMain.add(pnResult);
        
        // Nút Back
        btnBack = new JButton("Back to Client Search");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(this);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        pnMain.add(btnBack);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        this.add(pnMain);
        this.setSize(800, 550);
        this.setLocationRelativeTo(parentFrame);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Tải tất cả booking của khách này (không lọc)
        loadBookings(null, null);
    }
    
    /**
     * Tải danh sách booking lên JTable
     */
    private void loadBookings(Date startDate, Date endDate) {
        listBooking = bookingDAO.searchBookingsByClient(client.getId(), startDate, endDate);
        
        String[] columnNames = {"Booking ID", "Booking Date", "Num Rooms", "Total Amount", "Note"};
        String[][] value = new String[listBooking.size()][5];
        for (int i = 0; i < listBooking.size(); i++) {
            Booking b = listBooking.get(i);
            value[i][0] = b.getId() + "";
            value[i][1] = sdf.format(b.getBookedDate());
            value[i][2] = b.getBookedRoom().size() + ""; // Số phòng
            value[i][3] = String.format("%.0f", b.getTotal()); // Tính tổng tiền
            value[i][4] = b.getNote();
        }
        DefaultTableModel model = new DefaultTableModel(value, columnNames) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblResult.setModel(model);
    }
    
    /**
     * Hiển thị tùy chọn cho Giai đoạn 3
     */
    private void showOptionsForBooking(Booking booking) {
        String[] options = {"View Bill(s)", "View Room Details", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, 
                "Booking ID: " + booking.getId() + "\nSelect an option:",
                "Booking Options",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) { // View Bill(s)
            (new ViewBookingBillsFrm(user, booking, client, this)).setVisible(true);
            setVisible(false);

        // ====================== BẮT ĐẦU SỬA ĐỔI ======================
        } else if (choice == 1) { // View Room Details
            /* // Code cũ:
            JOptionPane.showMessageDialog(this, "Sẽ mở chi tiết Phòng cho Booking " + booking.getId() + "\n(Chúng ta sẽ làm ở Giai đoạn 3b)");
            */

            // Code MỚI:
            // Truyền 'booking' (chứa list phòng)
            (new ViewBookingDetailsFrm(user, booking, this)).setVisible(true);
            setVisible(false);
        }
        // ====================== KẾT THÚC SỬA ĐỔI ======================
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            // Lọc theo ngày
            Date startDate = null;
            Date endDate = null;
            try {
                if (!txtStartDate.getText().isEmpty()) {
                    startDate = sdf.parse(txtStartDate.getText());
                }
                if (!txtEndDate.getText().isEmpty()) {
                    endDate = sdf.parse(txtEndDate.getText());
                }
                
                // Tải lại bảng với bộ lọc
                loadBookings(startDate, endDate);
                
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd.");
            }
        } else if (e.getSource() == btnBack) {
            parentFrame.setVisible(true);
            this.dispose();
        }
    }
}