package view.history;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import dao.UsedServiceDAO; // DAO có sẵn
import model.BookedRoom;
import model.Booking;
import model.UsedService;
import model.User;

/**
 * Giao diện Giai đoạn 3b:
 * Hiển thị chi tiết các phòng và dịch vụ đã dùng của 1 booking.
 */
public class ViewBookingDetailsFrm extends JFrame implements ActionListener {
    private User user;
    private Booking booking;
    private JFrame parentFrame; // Frame cha (ViewClientHistoryFrm)
    
    // Danh sách này được lấy trực tiếp từ đối tượng Booking
    private ArrayList<BookedRoom> listBookedRoom; 
    
    private JTable tblRooms, tblServices;
    private DefaultTableModel modelRooms, modelServices;
    private JButton btnBack;
    
    private UsedServiceDAO usedServiceDAO;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ViewBookingDetailsFrm(User user, Booking booking, JFrame parentFrame) {
        super("Booking Details: " + booking.getId());
        this.user = user;
        this.booking = booking;
        this.parentFrame = parentFrame;
        this.listBookedRoom = booking.getBookedRoom(); // Lấy dữ liệu đã có
        this.usedServiceDAO = new UsedServiceDAO();

        JPanel pnMain = new JPanel(new BorderLayout(10, 10));
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top: Tiêu đề
        JLabel lblTitle = new JLabel("Booking Details (ID: " + booking.getId() + ")");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font(lblTitle.getFont().getName(), Font.BOLD, 20));
        pnMain.add(lblTitle, BorderLayout.NORTH);

        // Center: Hai bảng (giống CheckoutFrm)
        JPanel pnCenter = new JPanel(new GridLayout(2, 1, 10, 10));
        
        // Bảng 1: Các phòng đã đặt
        JPanel pnRooms = new JPanel(new BorderLayout());
        pnRooms.add(new JLabel("Booked Rooms (Select a room to see services)"), BorderLayout.NORTH);
        tblRooms = new JTable();
        tblRooms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modelRooms = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblRooms.setModel(modelRooms);
        
        tblRooms.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && tblRooms.getSelectedRow() != -1) {
                    int row = tblRooms.getSelectedRow();
                    BookedRoom selectedRoom = listBookedRoom.get(row);
                    // Tải dịch vụ cho phòng đã chọn
                    loadUsedServices(selectedRoom.getId());
                }
            }
        });
        pnRooms.add(new JScrollPane(tblRooms), BorderLayout.CENTER);
        pnCenter.add(pnRooms);
        
        // Bảng 2: Các dịch vụ đã sử dụng
        JPanel pnServices = new JPanel(new BorderLayout());
        pnServices.add(new JLabel("Used Services (for selected room)"), BorderLayout.NORTH);
        tblServices = new JTable();
        modelServices = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblServices.setModel(modelServices);
        pnServices.add(new JScrollPane(tblServices), BorderLayout.CENTER);
        
        pnCenter.add(pnServices);
        pnMain.add(pnCenter, BorderLayout.CENTER);

        // Bottom: Nút Back
        btnBack = new JButton("Back to Booking History");
        btnBack.addActionListener(this);
        pnMain.add(btnBack, BorderLayout.SOUTH);
        
        this.setContentPane(pnMain);
        this.setSize(800, 600);
        this.setLocationRelativeTo(parentFrame);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        loadBookedRooms();
    }
    
    /**
     * Tải danh sách phòng từ booking (đã có sẵn)
     */
    private void loadBookedRooms() {
        String[] columnNames = {"Room Name", "Room Type", "Check-in", "Check-out", "Price", "Was Checked-in"};
        modelRooms.setColumnIdentifiers(columnNames);
        modelRooms.setRowCount(0); // Xóa dữ liệu cũ
        
        for(BookedRoom br : listBookedRoom) {
            modelRooms.addRow(new Object[]{
                br.getRoom().getName(),
                br.getRoom().getType(),
                sdf.format(br.getCheckin()),
                sdf.format(br.getCheckout()),
                String.format("%.0f", br.getPrice()),
                br.isChecked() ? "Yes" : "No"
            });
        }
        
        // Tự động chọn phòng đầu tiên nếu có
        if(!listBookedRoom.isEmpty()) {
            tblRooms.setRowSelectionInterval(0, 0);
        }
    }
    
    /**
     * Tải danh sách dịch vụ cho phòng đã chọn (gọi DAO)
     */
    private void loadUsedServices(int bookedRoomID) {
        // Gọi DAO có sẵn
        ArrayList<UsedService> listService = usedServiceDAO.getUsedServices(bookedRoomID);
        
        String[] columnNames = {"Service Name", "Quantity", "Price", "SellOff (%)", "Total"};
        modelServices.setColumnIdentifiers(columnNames);
        modelServices.setRowCount(0); // Xóa dữ liệu cũ
        
        for(UsedService us : listService) {
            float total = us.getPrice() * us.getQuantity() * (1 - us.getSellOff()/100);
            modelServices.addRow(new Object[]{
                us.getService().getName(),
                us.getQuantity(),
                String.format("%.0f", us.getPrice()),
                String.format("%.1f", us.getSellOff()),
                String.format("%.0f", total)
            });
        }
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