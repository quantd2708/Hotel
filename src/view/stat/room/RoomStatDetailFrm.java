package view.stat.room;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import dao.BookingDAO;
import model.Booking;
import model.RoomStat;
import model.User;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import dao.BillDAO;
import model.Bill;
import view.bill.BillDetailFrm;


public class RoomStatDetailFrm extends JFrame implements ActionListener {
    private User user;
    private RoomStat roomStat;
    private Date startDate, endDate;
    private JFrame parentFrame; // Frame cha (RoomStatFrm)
    private JTable tblResult;
    private JButton btnBack;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private ArrayList<Booking> listBooking;
    
    public RoomStatDetailFrm(User user, RoomStat roomStat, Date startDate, Date endDate, JFrame parent) {
        super("Detail Statistics for Room: " + roomStat.getName());
        this.user = user;
        this.roomStat = roomStat;
        this.startDate = startDate;
        this.endDate = endDate;
        this.parentFrame = parent;

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Statistics Detail for Room: " + roomStat.getName());
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        // Thông tin phòng và ngày
        JLabel lblRoomInfo = new JLabel("Type: " + roomStat.getType() + " | Period: " + sdf.format(startDate) + " to " + sdf.format(endDate));
        lblRoomInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMain.add(lblRoomInfo);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Bảng kết quả
        JPanel pnResult = new JPanel(new BorderLayout());
        tblResult = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false);
        pnResult.add(scrollPane, BorderLayout.CENTER);
        
        // Gọi DAO và điền vào bảng
        fillTable();
        
         tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Kiểm tra nếu click đúp
                if (e.getClickCount() == 2) {
                    int row = tblResult.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        // Lấy bookingID từ danh sách đã lưu
                        int bookingID = listBooking.get(row).getId();
                        
                        // Gọi BillDAO
                        BillDAO billDAO = new BillDAO();
                        Bill bill = billDAO.getBillByBookingID(bookingID);
                        
                        if (bill != null) {
                            // Mở cửa sổ chi tiết hóa đơn
                            (new BillDetailFrm(bill, RoomStatDetailFrm.this)).setVisible(true);
                            setVisible(false); // Ẩn cửa sổ này đi
                        } else {
                            JOptionPane.showMessageDialog(RoomStatDetailFrm.this,
                                "This booking has not been billed yet.",
                                "Bill Not Found",
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });
        
        pnMain.add(pnResult);
        
        // Tổng
        JPanel pnTotal = new JPanel();
        pnTotal.setLayout(new BoxLayout(pnTotal, BoxLayout.X_AXIS));
        pnTotal.add(Box.createHorizontalGlue());
        JLabel lblTotal = new JLabel("Total Days: " + roomStat.getTotalDay() + " | Total Income: " + String.format("%.0f", roomStat.getTotalIncome()));
        lblTotal.setFont(new Font(lblTotal.getFont().getName(), Font.BOLD, 14));
        pnTotal.add(lblTotal);
        pnTotal.add(Box.createRigidArea(new Dimension(10, 0)));
        pnMain.add(pnTotal);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        // Nút Back
        btnBack = new JButton("Back");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(this);
        pnMain.add(btnBack);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        
        this.setContentPane(pnMain);
        this.setSize(800, 400);
        this.setLocationRelativeTo(parentFrame); // Hiển thị gần frame cha
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void fillTable() {
        BookingDAO bkDAO = new BookingDAO();
        listBooking = bkDAO.getBookingOfRoom(roomStat.getId(), startDate, endDate);
        
        String[] columnNames = {"Client Name", "Check-in", "Check-out", "Price/Night", "Days", "Total Income"};
        String[][] value = new String[listBooking.size()][6];
        
        for (int i = 0; i < listBooking.size(); i++) {
            Booking b = listBooking.get(i);
            // Vì DAO trả về 1 list Booking, mỗi booking chỉ có 1 BookedRoom trong list
            model.BookedRoom br = b.getBookedRoom().get(0);
            
            // Tính toán số ngày
            long diff = br.getCheckout().getTime() - br.getCheckin().getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            if(days == 0) days = 1; // Logic của DATEDIFF(LEAST, GREATEST)
            
            float price = br.getPrice();
            float total = (price - br.getSaleoff()) * days * (1 - b.getSaleoff()/100);
            
            value[i][0] = b.getClient().getFullName();
            value[i][1] = sdf.format(br.getCheckin());
            value[i][2] = sdf.format(br.getCheckout());
            value[i][3] = String.format("%.0f", price);
            value[i][4] = String.valueOf(days);
            value[i][5] = String.format("%.0f", total);
        }
        
        DefaultTableModel model = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblResult.setModel(model);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            parentFrame.setVisible(true); // Hiển thị lại frame cha
            this.dispose(); // Đóng frame chi tiết
        }
    }
    
    // Ghi đè phương thức dispose để đảm bảo frame cha được hiển thị lại
    @Override
    public void dispose() {
        parentFrame.setVisible(true);
        super.dispose();
    }
}
