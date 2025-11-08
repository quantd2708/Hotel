package view.booking;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import dao.RoomDAO;
import model.Booking;
import model.BookedRoom;
import model.Room;
import model.User;
import view.user.SellerHomeFrm;

public class SearchFreeRoomFrm extends JFrame implements ActionListener {
    private ArrayList<Room> listAvailableRoom;
    private JTextField txtCheckin, txtCheckout;
    private JButton btnSearch, btnAddRoom, btnRemoveRoom, btnGoToClient, btnBack;
    private JTable tblAvailableRooms, tblSelectedRooms;
    private DefaultTableModel modelAvailable, modelSelected;
    
    private User user;
    private Booking booking; // Booking object (giỏ hàng)
    private JFrame homeFrame;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public SearchFreeRoomFrm(User user, JFrame homeFrame) {
        super("Search available rooms");
        this.user = user;
        this.homeFrame = homeFrame;
        this.booking = new Booking(); // Khởi tạo booking mới
        this.booking.setCreator(user);
        
        listAvailableRoom = new ArrayList<Room>();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Search Available Rooms");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel tìm kiếm
        JPanel pnSearch = new JPanel();
        pnSearch.setLayout(new BoxLayout(pnSearch, BoxLayout.X_AXIS));
        pnSearch.add(new JLabel("Check-in (yyyy-MM-dd): "));
        txtCheckin = new JTextField(10);
        pnSearch.add(txtCheckin);
        pnSearch.add(Box.createRigidArea(new Dimension(10, 0)));
        pnSearch.add(new JLabel("Check-out (yyyy-MM-dd): "));
        txtCheckout = new JTextField(10);
        pnSearch.add(txtCheckout);
        pnSearch.add(Box.createRigidArea(new Dimension(10, 0)));
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(this);
        pnSearch.add(btnSearch);
        pnMain.add(pnSearch);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        // Panel chứa 2 bảng
        JPanel pnTables = new JPanel();
        pnTables.setLayout(new BoxLayout(pnTables, BoxLayout.X_AXIS));

        // Panel Bảng Trái (Phòng trống)
        JPanel pnAvailable = new JPanel(new BorderLayout());
        pnAvailable.add(new JLabel("Available Rooms:"), BorderLayout.NORTH);
        tblAvailableRooms = new JTable();
        String[] colAvailable = {"Id", "Name", "Type", "Price", "Description"};
        modelAvailable = new DefaultTableModel(colAvailable, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblAvailableRooms.setModel(modelAvailable);
        JScrollPane scrollAvailable = new JScrollPane(tblAvailableRooms);
        scrollAvailable.setPreferredSize(new Dimension(400, 250));
        pnAvailable.add(scrollAvailable, BorderLayout.CENTER);
        pnTables.add(pnAvailable);

        // Panel Nút Giữa (Thêm/Bớt)
        JPanel pnMidButtons = new JPanel();
        pnMidButtons.setLayout(new BoxLayout(pnMidButtons, BoxLayout.Y_AXIS));
        btnAddRoom = new JButton("Add >>");
        btnAddRoom.addActionListener(this);
        btnRemoveRoom = new JButton("<< Remove");
        btnRemoveRoom.addActionListener(this);
        pnMidButtons.add(Box.createVerticalGlue());
        pnMidButtons.add(btnAddRoom);
        pnMidButtons.add(Box.createRigidArea(new Dimension(0, 10)));
        pnMidButtons.add(btnRemoveRoom);
        pnMidButtons.add(Box.createVerticalGlue());
        pnTables.add(pnMidButtons);

        // Panel Bảng Phải (Phòng đã chọn)
        JPanel pnSelected = new JPanel(new BorderLayout());
        pnSelected.add(new JLabel("Selected Rooms (Cart):"), BorderLayout.NORTH);
        tblSelectedRooms = new JTable();
        String[] colSelected = {"Id", "Name", "Type", "Price"};
        modelSelected = new DefaultTableModel(colSelected, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblSelectedRooms.setModel(modelSelected);
        JScrollPane scrollSelected = new JScrollPane(tblSelectedRooms);
        scrollSelected.setPreferredSize(new Dimension(300, 250));
        pnSelected.add(scrollSelected, BorderLayout.CENTER);
        pnTables.add(pnSelected);
        
        pnMain.add(pnTables);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Panel Nút Dưới (Back, Continue)
        JPanel pnBottomButtons = new JPanel();
        pnBottomButtons.setLayout(new BoxLayout(pnBottomButtons, BoxLayout.X_AXIS));
        btnBack = new JButton("Back to Home");
        btnBack.addActionListener(this);
        btnGoToClient = new JButton("Continue to Client Info");
        btnGoToClient.addActionListener(this);
        
        pnBottomButtons.add(btnBack);
        pnBottomButtons.add(Box.createHorizontalGlue());
        pnBottomButtons.add(btnGoToClient);
        pnMain.add(pnBottomButtons);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        this.add(pnMain);
        this.setSize(850, 500); // Tăng kích thước
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            actionSearch();
        } else if (e.getSource() == btnAddRoom) {
            actionAddRoom();
        } else if (e.getSource() == btnRemoveRoom) {
            actionRemoveRoom();
        } else if (e.getSource() == btnGoToClient) {
            actionGoToClient();
        } else if (e.getSource() == btnBack) {
            homeFrame.setVisible(true);
            this.dispose();
        }
    }

    private void actionSearch() {
        String sCheckin = txtCheckin.getText();
        String sCheckout = txtCheckout.getText();
        if (sCheckin.isEmpty() || sCheckout.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both check-in and check-out dates.");
            return;
        }

        try {
            Date checkin = sdf.parse(sCheckin);
            Date checkout = sdf.parse(sCheckout);
            
            if(checkout.before(checkin) || checkout.equals(checkin)){
                 JOptionPane.showMessageDialog(this, "Check-out date must be after check-in date.");
                 return;
            }

            RoomDAO rd = new RoomDAO();
            listAvailableRoom = rd.searchFreeRoom(checkin, checkout);

            if(listAvailableRoom.isEmpty()){
                JOptionPane.showMessageDialog(this, "No available rooms found for this period.");
            }
            
            // Cập nhật JTable trái
            // Xóa dữ liệu cũ
            modelAvailable.setRowCount(0);
            modelSelected.setRowCount(0);
            booking.getBookedRoom().clear();
            
            for (Room r : listAvailableRoom) {
                modelAvailable.addRow(new Object[]{
                    r.getId(), r.getName(), r.getType(), r.getPrice(), r.getDes()
                });
            }
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    private void actionAddRoom() {
        int row = tblAvailableRooms.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an available room to add.");
            return;
        }

        Date checkin, checkout;
        try {
            checkin = sdf.parse(txtCheckin.getText());
            checkout = sdf.parse(txtCheckout.getText());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please re-check.");
            return;
        }
        
        Room selectedRoom = listAvailableRoom.get(row);
        
        // Tạo một BookedRoom mới
        BookedRoom br = new BookedRoom();
        br.setRoom(selectedRoom);
        br.setCheckin(checkin);
        br.setCheckout(checkout);
        br.setPrice(selectedRoom.getPrice()); // Giá gốc
        br.setSaleoff(0);
        br.setChecked(false); // Chưa checkin

        // Thêm vào booking (giỏ hàng)
        booking.getBookedRoom().add(br);
        // Thêm vào bảng bên phải
        modelSelected.addRow(new Object[]{
            selectedRoom.getId(), selectedRoom.getName(), selectedRoom.getType(), selectedRoom.getPrice()
        });
        
        // Xóa khỏi danh sách và bảng bên trái
        listAvailableRoom.remove(row);
        modelAvailable.removeRow(row);
    }
    
    private void actionRemoveRoom() {
        int row = tblSelectedRooms.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room from the cart to remove.");
            return;
        }

        // Lấy BookedRoom từ giỏ hàng
        BookedRoom br = booking.getBookedRoom().get(row);
        Room room = br.getRoom();
        
        // Thêm lại vào danh sách và bảng bên trái
        listAvailableRoom.add(room);
        modelAvailable.addRow(new Object[]{
            room.getId(), room.getName(), room.getType(), room.getPrice(), room.getDes()
        });
        
        // Xóa khỏi giỏ hàng (booking) và bảng bên phải
        booking.getBookedRoom().remove(row);
        modelSelected.removeRow(row);
    }

    private void actionGoToClient() {
        if (booking.getBookedRoom().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one room.");
            return;
        }
        
        // Chuyển sang màn hình tìm kiếm khách hàng
        (new SearchClientFrm(user, booking, SearchFreeRoomFrm.this, homeFrame)).setVisible(true);
        setVisible(false);
    }
}