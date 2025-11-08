package view.checkout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import dao.BillDAO;
import dao.BookingDAO;
import dao.UsedServiceDAO;
import model.Bill;
import model.BookedRoom;
import model.Booking;
import model.UsedService;
import model.User;
import view.user.ReceptionistHomeFrm;


public class CheckoutFrm extends JFrame implements ActionListener {
    private User user;
    private Booking booking;
    private JFrame parentFrame;
    private ArrayList<BookedRoom> listBookedRoom;
    private ArrayList<UsedService> listUsedService;
    private BookedRoom selectedBookedRoom;
    
    private JTable tblRooms, tblServices;
    private JButton btnAddService, btnDeleteService, btnConfirm, btnBack;
    private JLabel lblClientName, lblBookingID, lblTotalRoom, lblTotalService, lblGrandTotal;
    private JComboBox<String> cmbPaymentType;
    private JTextField txtNote;
    
    private BookingDAO bookingDAO;
    private UsedServiceDAO usedServiceDAO;
    private BillDAO billDAO;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public CheckoutFrm(User user, Booking booking, JFrame parent) {
        super("Check-out & Payment (Booking ID: " + booking.getId() + ")");
        this.user = user;
        this.booking = booking;
        this.parentFrame = parent;
        
        bookingDAO = new BookingDAO();
        usedServiceDAO = new UsedServiceDAO();
        billDAO = new BillDAO();
        listBookedRoom = new ArrayList<>();
        listUsedService = new ArrayList<>();
        
        // Cấu trúc layout
        JPanel pnMain = new JPanel(new BorderLayout(10, 10));
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top: Thông tin chung
        JPanel pnTop = new JPanel(new GridLayout(1, 2));
        lblClientName = new JLabel("Client: " + booking.getClient().getFullName());
        lblClientName.setFont(new Font(lblClientName.getFont().getName(), Font.BOLD, 16));
        lblBookingID = new JLabel("Booking ID: " + booking.getId());
        lblBookingID.setFont(new Font(lblBookingID.getFont().getName(), Font.BOLD, 16));
        pnTop.add(lblClientName);
        pnTop.add(lblBookingID);
        pnMain.add(pnTop, BorderLayout.NORTH);

        // Center: Hai bảng dữ liệu
        JPanel pnCenter = new JPanel(new GridLayout(2, 1, 10, 10));
        
        // Bảng 1: Các phòng đã check-in
        JPanel pnRooms = new JPanel(new BorderLayout());
        pnRooms.add(new JLabel("Checked-in Rooms (Select a room to manage services)"), BorderLayout.NORTH);
        tblRooms = new JTable();
        tblRooms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Sự kiện khi chọn một phòng
        tblRooms.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && tblRooms.getSelectedRow() != -1) {
                    int row = tblRooms.getSelectedRow();
                    selectedBookedRoom = listBookedRoom.get(row);
                    // Tải lại bảng dịch vụ
                    loadUsedServices(selectedBookedRoom.getId());
                }
            }
        });
        pnRooms.add(new JScrollPane(tblRooms), BorderLayout.CENTER);
        pnCenter.add(pnRooms);
        
        // Bảng 2: Các dịch vụ đã sử dụng
        JPanel pnServices = new JPanel(new BorderLayout());
        pnServices.add(new JLabel("Used Services (for selected room)"), BorderLayout.NORTH);
        tblServices = new JTable();
        pnServices.add(new JScrollPane(tblServices), BorderLayout.CENTER);
        
        // Panel nút cho dịch vụ
        JPanel pnServiceButtons = new JPanel();
        btnAddService = new JButton("Add Service");
        btnAddService.addActionListener(this);
        btnDeleteService = new JButton("Delete Service");
        btnDeleteService.addActionListener(this);
        pnServiceButtons.add(btnAddService);
        pnServiceButtons.add(btnDeleteService);
        pnServices.add(pnServiceButtons, BorderLayout.SOUTH);
        pnCenter.add(pnServices);
        
        pnMain.add(pnCenter, BorderLayout.CENTER);

        // Right (Bottom): Thanh toán
        JPanel pnPayment = new JPanel();
        pnPayment.setLayout(new BoxLayout(pnPayment, BoxLayout.Y_AXIS));
        pnPayment.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        lblTotalRoom = new JLabel("Total Room Cost: 0 VND");
        lblTotalRoom.setFont(new Font(lblTotalRoom.getFont().getName(), Font.BOLD, 14));
        pnPayment.add(lblTotalRoom);
        pnPayment.add(Box.createRigidArea(new Dimension(0, 10)));
        
        lblTotalService = new JLabel("Total Service Cost: 0 VND");
        lblTotalService.setFont(new Font(lblTotalService.getFont().getName(), Font.BOLD, 14));
        pnPayment.add(lblTotalService);
        pnPayment.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel lblDiscount = new JLabel("Booking Saleoff: " + booking.getSaleoff() + "%");
        pnPayment.add(lblDiscount);
        pnPayment.add(Box.createRigidArea(new Dimension(0, 10)));
        
        lblGrandTotal = new JLabel("GRAND TOTAL: 0 VND");
        lblGrandTotal.setFont(new Font(lblGrandTotal.getFont().getName(), Font.BOLD, 18));
        pnPayment.add(lblGrandTotal);
        pnPayment.add(Box.createRigidArea(new Dimension(0, 20)));

        pnPayment.add(new JLabel("Payment Type:"));
        String[] paymentTypes = {"Cash", "Credit Card", "Bank Transfer"};
        cmbPaymentType = new JComboBox<>(paymentTypes);
        pnPayment.add(cmbPaymentType);
        pnPayment.add(Box.createRigidArea(new Dimension(0, 10)));
        
        pnPayment.add(new JLabel("Payment Note:"));
        txtNote = new JTextField();
        pnPayment.add(txtNote);
        pnPayment.add(Box.createRigidArea(new Dimension(0, 20)));

        btnConfirm = new JButton("Confirm & Finalize Bill");
        btnConfirm.addActionListener(this);
        pnPayment.add(btnConfirm);
        pnPayment.add(Box.createRigidArea(new Dimension(0, 10)));
        
        btnBack = new JButton("Back to Search");
        btnBack.addActionListener(this);
        pnPayment.add(btnBack);
        
        pnMain.add(pnPayment, BorderLayout.EAST);
        
        this.setContentPane(pnMain);
        this.setSize(1000, 700);
        this.setLocationRelativeTo(parentFrame);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        loadBookedRooms();
    }
    
    // Tải danh sách các phòng
    private void loadBookedRooms() {
        listBookedRoom = bookingDAO.getActiveBookedRooms(booking.getId());
        String[] columnNames = {"ID", "Room Name", "Check-in", "Check-out", "Days", "Price/Night"};
        String[][] value = new String[listBookedRoom.size()][6];
        
        for(int i=0; i<listBookedRoom.size(); i++) {
            BookedRoom br = listBookedRoom.get(i);
            long days = getDaysBetween(br.getCheckin(), br.getCheckout());
            value[i][0] = br.getId() + "";
            value[i][1] = br.getRoom().getName();
            value[i][2] = sdf.format(br.getCheckin());
            value[i][3] = sdf.format(br.getCheckout());
            value[i][4] = days + "";
            value[i][5] = String.format("%.0f", br.getPrice());
        }
        tblRooms.setModel(new DefaultTableModel(value, columnNames) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        
        // Tự động chọn phòng đầu tiên
        if(!listBookedRoom.isEmpty()) {
            tblRooms.setRowSelectionInterval(0, 0);
        }
    }
    
    // Tải danh sách dịch vụ cho phòng đã chọn
    private void loadUsedServices(int bookedRoomID) {
        listUsedService = usedServiceDAO.getUsedServices(bookedRoomID);
        String[] columnNames = {"ID", "Service Name", "Quantity", "Price", "Total"};
        String[][] value = new String[listUsedService.size()][5];
        
        for(int i=0; i<listUsedService.size(); i++) {
            UsedService us = listUsedService.get(i);
            float total = us.getPrice() * us.getQuantity() * (1 - us.getSellOff()/100);
            value[i][0] = us.getId() + "";
            value[i][1] = us.getService().getName();
            value[i][2] = us.getQuantity() + "";
            value[i][3] = String.format("%.0f", us.getPrice());
            value[i][4] = String.format("%.0f", total);
        }
        tblServices.setModel(new DefaultTableModel(value, columnNames) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        
        // Tính toán lại tổng
        calculateTotal();
    }
    
    // Tính toán lại toàn bộ hóa đơn
    private void calculateTotal() {
        float totalRoom = 0;
        float totalService = 0;
        
        // 1. Tính tiền phòng
        for (BookedRoom br : listBookedRoom) {
            long days = getDaysBetween(br.getCheckin(), br.getCheckout());
            totalRoom += (br.getPrice() * days * (1 - br.getSaleoff()/100));
        }
        
        // 2. Tính tiền dịch vụ (cho tất cả các phòng)
        for (BookedRoom br : listBookedRoom) {
            ArrayList<UsedService> services = usedServiceDAO.getUsedServices(br.getId());
            for (UsedService us : services) {
                totalService += (us.getPrice() * us.getQuantity() * (1 - us.getSellOff()/100));
            }
        }
        
        // 3. Tính tổng
        float grandTotal = (totalRoom + totalService) * (1 - booking.getSaleoff()/100);
        
        // 4. Cập nhật UI
        lblTotalRoom.setText("Total Room Cost: " + String.format("%.0f VND", totalRoom));
        lblTotalService.setText("Total Service Cost: " + String.format("%.0f VND", totalService));
        lblGrandTotal.setText("GRAND TOTAL: " + String.format("%.0f VND", grandTotal));
    }
    
    // Hàm này được gọi bởi JDialog con khi nó đóng
    public void refreshData() {
        if(selectedBookedRoom != null) {
            loadUsedServices(selectedBookedRoom.getId());
        }
    }

    // Helper tính số ngày
    private long getDaysBetween(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return (days == 0) ? 1 : days; // Ở ít nhất 1 ngày
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            parentFrame.setVisible(true);
            CheckoutFrm.super.dispose();
        } 
        else if (e.getSource() == btnAddService) {
            if (selectedBookedRoom == null) {
                JOptionPane.showMessageDialog(this, "Please select a room first.");
                return;
            }
            // Mở JDialog (pop-up)
            AddServiceToRoomFrm addServiceFrm = new AddServiceToRoomFrm(this, selectedBookedRoom.getId());
            addServiceFrm.setVisible(true);
            // Code sẽ dừng ở đây cho đến khi JDialog đóng
            
        } 
        else if (e.getSource() == btnDeleteService) {
            int row = tblServices.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a service to delete.");
                return;
            }
            UsedService us = listUsedService.get(row);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete service: " + us.getService().getName() + "?", 
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            
            if(confirm == JOptionPane.YES_OPTION) {
                if(usedServiceDAO.deleteUsedService(us.getId())) {
                    JOptionPane.showMessageDialog(this, "Service deleted.");
                    refreshData(); // Tải lại
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting service.");
                }
            }
        }
        else if (e.getSource() == btnConfirm) {
            // 1. Lấy tổng tiền lần cuối
            calculateTotal(); // Cập nhật tổng
            float finalAmount = Float.parseFloat(lblGrandTotal.getText().replaceAll("[^\\d.]", ""));
            
            // 2. Tạo đối tượng Bill
            Bill bill = new Bill();
            bill.setPaymentDate(new Date()); // Ngày hiện tại
            bill.setAmount(finalAmount);
            bill.setPaymentType((String) cmbPaymentType.getSelectedItem());
            bill.setNote(txtNote.getText());
            bill.setBooking(booking);
            bill.setCreator(user);
            
            // 3. Gọi DAO
            if(billDAO.addBill(bill)) {
                JOptionPane.showMessageDialog(this, "Check-out successful! Bill finalized.");
                (new ReceptionistHomeFrm(user)).setVisible(true);
                CheckoutFrm.super.dispose();
                parentFrame.dispose(); // Đóng cả màn hình tìm kiếm
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not save the bill.");
            }
        }
    }
    
    // Ghi đè dispose
    @Override
    public void dispose() {
        parentFrame.setVisible(true);
        CheckoutFrm.super.dispose();
    }
}