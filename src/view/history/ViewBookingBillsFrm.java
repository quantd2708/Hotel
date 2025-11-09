package view.history;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import dao.BillDAO;
import model.Bill;
import model.Booking;
import model.Client;
import model.User;
import view.bill.BillDetailFrm; // Import màn hình chi tiết đã có

/**
 * Giao diện Giai đoạn 3a:
 * Hiển thị danh sách các hóa đơn (cọc, thanh toán) của 1 booking.
 */
public class ViewBookingBillsFrm extends JFrame implements ActionListener {
    private ArrayList<Bill> listBill;
    private JTable tblResult;
    private JButton btnBack;
    private User user;
    private Booking booking; // Truyền từ P2
    private Client client;   // Truyền từ P1
    private JFrame parentFrame; // Frame cha (ViewClientHistoryFrm)
    
    private BillDAO billDAO;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ViewBookingBillsFrm(User user, Booking booking, Client client, JFrame parentFrame) {
        super("Bill History for Booking ID: " + booking.getId());
        this.user = user;
        this.booking = booking;
        this.client = client;
        this.parentFrame = parentFrame;
        this.listBill = new ArrayList<>();
        this.billDAO = new BillDAO();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblTitle = new JLabel("Bill History for Booking ID: " + booking.getId());
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel lblSub = new JLabel("Client: " + client.getFullName() + " | Double-click a bill to see details.");
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMain.add(lblSub);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel Kết quả
        JPanel pnResult = new JPanel();
        pnResult.setLayout(new BoxLayout(pnResult, BoxLayout.Y_AXIS));
        tblResult = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));
        
        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblResult.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        Bill selectedBill = listBill.get(row);
                        
                        // "Bơm" thông tin Client và Booking vào Bill
                        // để màn hình BillDetailFrm có thể đọc được
                        booking.setClient(client);
                        selectedBill.setBooking(booking);
                        
                        // Mở màn hình chi tiết hóa đơn (đã có)
                        (new BillDetailFrm(selectedBill, ViewBookingBillsFrm.this)).setVisible(true);
                        setVisible(false);
                    }
                }
            }
        });
        
        pnResult.add(scrollPane);
        pnMain.add(pnResult);
        
        // Nút Back
        btnBack = new JButton("Back to Booking History");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(this);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        pnMain.add(btnBack);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        this.add(pnMain);
        this.setSize(800, 450);
        this.setLocationRelativeTo(parentFrame);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        loadBills();
    }
    
    private void loadBills() {
        // Gọi hàm DAO mới
        listBill = billDAO.getAllBillsForBooking(booking.getId());
        
        String[] columnNames = {"Bill ID", "Payment Date", "Amount", "Payment Type", "Note", "Processed By"};
        String[][] value = new String[listBill.size()][6];
        for (int i = 0; i < listBill.size(); i++) {
            Bill b = listBill.get(i);
            value[i][0] = b.getId() + "";
            value[i][1] = sdf.format(b.getPaymentDate());
            value[i][2] = String.format("%.0f", b.getAmount());
            value[i][3] = b.getPaymentType();
            value[i][4] = b.getNote();
            value[i][5] = b.getCreator().getName();
        }
        DefaultTableModel model = new DefaultTableModel(value, columnNames) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblResult.setModel(model);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            parentFrame.setVisible(true);
            this.dispose();
        }
    }
    
    // Ghi đè dispose để đảm bảo frame cha được hiển thị lại
    @Override
    public void dispose() {
        parentFrame.setVisible(true);
        super.dispose();
    }
}