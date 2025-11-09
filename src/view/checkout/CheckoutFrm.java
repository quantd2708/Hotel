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
import java.time.LocalDate; // Import mới để so sánh ngày
import java.time.ZoneId; // Import mới
import java.util.Date;
import java.time.temporal.ChronoUnit;


public class CheckoutFrm extends JFrame implements ActionListener {
    private User user;
    private Booking booking;
    private JFrame parentFrame;
    private ArrayList<BookedRoom> listBookedRoom;
    private ArrayList<UsedService> listUsedService;
    private BookedRoom selectedBookedRoom;
    
    private JTable tblRooms, tblServices;
    private JButton btnAddService, btnDeleteService, btnConfirm, btnBack;
    
    // Tách riêng các JLabel cho giá trị để bỏ bôi đậm
    private JLabel lblClientName, lblBookingID, lblTotalRoomValue, lblTotalServiceValue, lblGrandTotalValue, lblTotalPaidValue, lblRemainingAmountValue;
    
    private JComboBox<String> cmbPaymentType;
    private JTextField txtNote, txtPenaltyFee;
    
    private BookingDAO bookingDAO;
    private UsedServiceDAO usedServiceDAO;
    private BillDAO billDAO;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Date today;

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
        this.today = new Date(); 
        
        JPanel pnMain = new JPanel(new BorderLayout(10, 10));
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top
        JPanel pnTop = new JPanel(new GridLayout(1, 2));
        lblClientName = new JLabel("Client: " + booking.getClient().getFullName());
        lblClientName.setFont(new Font(lblClientName.getFont().getName(), Font.BOLD, 16));
        lblBookingID = new JLabel("Booking ID: " + booking.getId());
        lblBookingID.setFont(new Font(lblBookingID.getFont().getName(), Font.BOLD, 16));
        pnTop.add(lblClientName);
        pnTop.add(lblBookingID);
        pnMain.add(pnTop, BorderLayout.NORTH);

        // Center
        JPanel pnCenter = new JPanel(new GridLayout(2, 1, 10, 10));
        // ... (Code Bảng 1: pnRooms và tblRooms giữ nguyên y hệt) ...
        JPanel pnRooms = new JPanel(new BorderLayout());
        pnRooms.add(new JLabel("Checked-in Rooms (Select a room to manage services)"), BorderLayout.NORTH);
        tblRooms = new JTable();
        tblRooms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tblRooms.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && tblRooms.getSelectedRow() != -1) {
                    int row = tblRooms.getSelectedRow();
                    selectedBookedRoom = listBookedRoom.get(row);
                    loadUsedServices(selectedBookedRoom.getId());
                }
            }
        });
        pnRooms.add(new JScrollPane(tblRooms), BorderLayout.CENTER);
        pnCenter.add(pnRooms);
        
        // ... (Code Bảng 2: pnServices và tblServices giữ nguyên y hệt) ...
        JPanel pnServices = new JPanel(new BorderLayout());
        pnServices.add(new JLabel("Used Services (for selected room)"), BorderLayout.NORTH);
        tblServices = new JTable();
        pnServices.add(new JScrollPane(tblServices), BorderLayout.CENTER);
        
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

        // ====================== BẮT ĐẦU SỬA ĐỔI PANEL THANH TOÁN ======================
        
        // Panel BÊN PHẢI (EAST) - Dùng BoxLayout để chứa panel Grid và các nút
        JPanel pnEastContainer = new JPanel();
        pnEastContainer.setLayout(new BoxLayout(pnEastContainer, BoxLayout.Y_AXIS));
        pnEastContainer.setBorder(new EmptyBorder(0, 10, 0, 0));
        pnEastContainer.setPreferredSize(new Dimension(350, 600)); // Cố định chiều rộng

        // Panel Thanh toán chính (Dùng GridLayout 2 cột)
        JPanel pnPayment = new JPanel(new GridLayout(0, 2, 10, 10)); // 0 hàng, 2 cột, gap 10px

        // Khởi tạo các JLabel giá trị (văn bản thường)
        // Đặt font chữ thường, cỡ 14
        lblTotalRoomValue = new JLabel("0 VND");
        lblTotalRoomValue.setFont(new Font(lblTotalRoomValue.getFont().getName(), Font.PLAIN, 14));
        
        lblTotalServiceValue = new JLabel("0 VND");
        lblTotalServiceValue.setFont(new Font(lblTotalServiceValue.getFont().getName(), Font.PLAIN, 14));
        
        lblGrandTotalValue = new JLabel("0 VND");
        // Giữ in đậm cho SUB-TOTAL và REMAINING
        lblGrandTotalValue.setFont(new Font(lblGrandTotalValue.getFont().getName(), Font.PLAIN, 14)); 
        
        lblTotalPaidValue = new JLabel("0 VND");
        lblTotalPaidValue.setFont(new Font(lblTotalPaidValue.getFont().getName(), Font.PLAIN, 14)); // Bỏ in đậm
        
        lblRemainingAmountValue = new JLabel("0 VND");
        lblRemainingAmountValue.setFont(new Font(lblRemainingAmountValue.getFont().getName(),Font.PLAIN, 14));
        
        // Hàng 1
        pnPayment.add(createBoldLabel("Total Room Cost:"));
        pnPayment.add(lblTotalRoomValue);
        
        // Hàng 2
        pnPayment.add(createBoldLabel("Total Service Cost:"));
        pnPayment.add(lblTotalServiceValue);
        
        // Hàng 3
        pnPayment.add(createBoldLabel("Booking Saleoff:"));
        JLabel lblSaleoffValue = new JLabel(booking.getSaleoff() + "%"); // Chữ thường
        lblSaleoffValue.setFont(new Font(lblSaleoffValue.getFont().getName(), Font.PLAIN, 14));
        pnPayment.add(lblSaleoffValue);

        // Hàng 4 (SUB-TOTAL)
        JLabel lblSubTotalTitle = createBoldLabel("SUB-TOTAL:");
        lblSubTotalTitle.setFont(new Font(lblSubTotalTitle.getFont().getName(), Font.BOLD, 14));
        pnPayment.add(lblSubTotalTitle);
        // Dòng này đã được sửa ở trên (vẫn bôi đậm)
        pnPayment.add(lblGrandTotalValue);

        // Hàng 5
        pnPayment.add(createBoldLabel("Penalty Fee:"));
        txtPenaltyFee = new JTextField("0");
        txtPenaltyFee.setFont(new Font(txtPenaltyFee.getFont().getName(), Font.PLAIN, 14));
        pnPayment.add(txtPenaltyFee);

        // Hàng 6 (Total Paid)
        JLabel lblTotalPaidTitle = createBoldLabel("Total Paid (Deposit):");
        pnPayment.add(lblTotalPaidTitle);
        // Dòng này đã được sửa ở trên (bỏ bôi đậm)
        pnPayment.add(lblTotalPaidValue);

        // Hàng 7 (REMAINING)
        JLabel lblRemainingTitle = createBoldLabel("REMAINING:");
        lblRemainingTitle.setFont(new Font(lblRemainingTitle.getFont().getName(), Font.BOLD, 14));
        pnPayment.add(lblRemainingTitle);
        // Dòng này đã được sửa ở trên (vẫn bôi đậm)
        pnPayment.add(lblRemainingAmountValue);

        // Hàng 8
        pnPayment.add(createBoldLabel("Payment Type:"));
        String[] paymentTypes = {"Cash", "Credit Card", "Bank Transfer"};
        cmbPaymentType = new JComboBox<>(paymentTypes);
        cmbPaymentType.setFont(new Font(cmbPaymentType.getFont().getName(), Font.PLAIN, 14));
        pnPayment.add(cmbPaymentType);
        
        // Hàng 9
        pnPayment.add(createBoldLabel("Payment Note:"));
        txtNote = new JTextField();
        txtNote.setFont(new Font(txtNote.getFont().getName(), Font.PLAIN, 14));
        pnPayment.add(txtNote);
        
        // Thêm panel GridLayout vào panel container
        pnEastContainer.add(pnPayment);
        pnEastContainer.add(Box.createVerticalGlue()); // Đẩy các nút xuống dưới

        // Thêm các Nút vào panel container (căn giữa)
        btnConfirm = createPaymentButton("Confirm & Finalize Bill");
        pnEastContainer.add(btnConfirm);
        pnEastContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        btnBack = createPaymentButton("Back to Search");
        pnEastContainer.add(btnBack);
        
        // Thêm panel container vào Main
        pnMain.add(pnEastContainer, BorderLayout.EAST);
        
        // ====================== KẾT THÚC SỬA ĐỔI PANEL THANH TOÁN ======================
        
        this.setContentPane(pnMain);
        this.setSize(1000, 700);
        this.setLocationRelativeTo(parentFrame);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        loadBookedRooms();
        calculateTotal();
    }
    
    // Helper mới: Chỉ tạo JLabel in đậm
    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 14));
        return label;
    }
    
    // Helper sửa: Căn giữa các nút
    private JButton createPaymentButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa
        button.addActionListener(this);
        return button;
    }
    
    // ... (Hàm loadBookedRooms() giữ nguyên) ...
    private void loadBookedRooms() {
        listBookedRoom = bookingDAO.getActiveBookedRooms(booking.getId());
        String[] columnNames = {"ID", "Room Name", "Booked Check-in", "Booked Check-out", "Price/Night"};
        String[][] value = new String[listBookedRoom.size()][5];
        
        for(int i=0; i<listBookedRoom.size(); i++) {
            BookedRoom br = listBookedRoom.get(i);
            value[i][0] = br.getId() + "";
            value[i][1] = br.getRoom().getName();
            value[i][2] = sdf.format(br.getCheckin());
            value[i][3] = sdf.format(br.getCheckout());
            value[i][4] = String.format("%.0f", br.getPrice());
        }
        tblRooms.setModel(new DefaultTableModel(value, columnNames) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        
        if(!listBookedRoom.isEmpty()) {
            tblRooms.setRowSelectionInterval(0, 0);
        }
    }
    
    // ... (Hàm loadUsedServices() giữ nguyên - đã sửa ở lần trước) ...
    private void loadUsedServices(int bookedRoomID) {
        listUsedService = usedServiceDAO.getUsedServices(bookedRoomID);
        String[] columnNames = {"ID", "Service Name", "Qty", "Price", "SellOff (%)", "Total"};
        String[][] value = new String[listUsedService.size()][6];
        
        for(int i=0; i<listUsedService.size(); i++) {
            UsedService us = listUsedService.get(i);
            float total = us.getPrice() * us.getQuantity() * (1 - us.getSellOff()/100);
            value[i][0] = us.getId() + "";
            value[i][1] = us.getService().getName();
            value[i][2] = us.getQuantity() + "";
            value[i][3] = String.format("%.0f", us.getPrice());
            value[i][4] = String.format("%.1f", us.getSellOff());
            value[i][5] = String.format("%.0f", total);
        }
        tblServices.setModel(new DefaultTableModel(value, columnNames) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        
        calculateTotal();
    }
    
    // ... (Hàm calculateTotal() - Sửa lại phần Cập nhật UI) ...
    private void calculateTotal() {
        float totalRoom = 0;
        float totalService = 0;
        float totalPenalty = 0;
        float totalPaid = 0;
        
        for (BookedRoom br : listBookedRoom) {
            Date checkin = br.getCheckin();
            Date bookedCheckout = br.getCheckout();
            Date actualCheckoutDate = bookedCheckout;
            if (isDateBefore(today, bookedCheckout)) {
                actualCheckoutDate = today; 
            }
            long days = getDaysBetween(checkin, actualCheckoutDate);
            totalRoom += (br.getPrice() * days * (1 - br.getSaleoff()/100));
        }
        
        for (BookedRoom br : listBookedRoom) {
            ArrayList<UsedService> services = usedServiceDAO.getUsedServices(br.getId());
            for (UsedService us : services) {
                totalService += (us.getPrice() * us.getQuantity() * (1 - us.getSellOff()/100));
            }
        }
        
        float grandTotal = (totalRoom + totalService) * (1 - booking.getSaleoff()/100);
        
        try {
            totalPenalty = Float.parseFloat(txtPenaltyFee.getText());
        } catch (NumberFormatException e) {
            totalPenalty = 0;
        }
        
        totalPaid = billDAO.getTotalPaidForBooking(booking.getId());
        
        float remainingAmount = grandTotal + totalPenalty - totalPaid;
        
        // ====================== SỬA CÁCH CẬP NHẬT UI ======================
        lblTotalRoomValue.setText(String.format("%.0f VND", totalRoom));
        lblTotalServiceValue.setText(String.format("%.0f VND", totalService));
        lblGrandTotalValue.setText(String.format("%.0f VND", grandTotal));
        lblTotalPaidValue.setText(String.format("%.0f VND", totalPaid));
        lblRemainingAmountValue.setText(String.format("%.0f VND", remainingAmount));
        // ====================== KẾT THÚC SỬA UI ======================
    }
    
    // ... (Hàm refreshData() giữ nguyên) ...
    public void refreshData() {
        if(selectedBookedRoom != null) {
            loadUsedServices(selectedBookedRoom.getId());
        } else {
            calculateTotal();
        }
    }

    // ... (Hàm getDaysBetween() giữ nguyên) ...
    private long getDaysBetween(Date d1, Date d2) {
        // Chuyển sang LocalDate để bỏ qua thành phần GIỜ, PHÚT, GIÂY
        LocalDate date1 = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Tính số ngày (số đêm) chính xác
        long days = ChronoUnit.DAYS.between(date1, date2);
        
        return (days <= 0) ? 1 : days; // Ở ít nhất 1 ngày
    }
    
    // ... (Hàm isDateBefore() giữ nguyên) ...
    private boolean isDateBefore(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate1.isBefore(localDate2);
    }

    // ... (Hàm actionPerformed() giữ nguyên) ...
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
            AddServiceToRoomFrm addServiceFrm = new AddServiceToRoomFrm(this, selectedBookedRoom.getId());
            addServiceFrm.setVisible(true);
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
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting service.");
                }
            }
        }
        else if (e.getSource() == btnConfirm) {
            calculateTotal(); 
            // Lấy giá trị từ JLabel ĐÃ CẬP NHẬT
            float finalAmount = Float.parseFloat(lblRemainingAmountValue.getText().replaceAll("[^\\d.]", ""));
            
            if (finalAmount < 0) {
                 JOptionPane.showMessageDialog(this, "Error: Remaining amount is negative. Check deposits.");
                 return;
            }

            for (BookedRoom br : listBookedRoom) {
                if (isDateBefore(today, br.getCheckout())) {
                    bookingDAO.updateBookedRoomCheckout(br.getId(), today);
                }
            }
            
            Bill bill = new Bill();
            bill.setPaymentDate(today);
            bill.setAmount(finalAmount);
            bill.setPaymentType((String) cmbPaymentType.getSelectedItem());
            bill.setNote(txtNote.getText() + " (Penalty: " + txtPenaltyFee.getText() + ")");
            bill.setBooking(booking);
            bill.setCreator(user);
            
            if(billDAO.addBill(bill)) {
                JOptionPane.showMessageDialog(this, "Check-out successful! Bill finalized.");
                (new ReceptionistHomeFrm(user)).setVisible(true);
                CheckoutFrm.super.dispose();
                parentFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not save the bill.");
            }
        }
    }
    
    // ... (Hàm dispose() giữ nguyên) ...
    @Override
    public void dispose() {
        parentFrame.setVisible(true);
        CheckoutFrm.super.dispose();
    }
}