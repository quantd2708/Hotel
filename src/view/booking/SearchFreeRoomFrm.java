package view.booking;

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
import dao.RoomDAO;
import model.Booking;
import model.BookedRoom;
import model.Room;
import model.User;
import view.user.SellerHomeFrm;



public class SearchFreeRoomFrm extends JFrame implements ActionListener {
    private ArrayList<Room> listRoom;
    private JTextField txtCheckin, txtCheckout;
    private JButton btnSearch;
    private JTable tblResult;
    private User user;
    private Booking booking;
    private JButton btnBack;
    private JFrame homeFrame;
    // Sử dụng SimpleDateFormat để chuẩn hóa định dạng ngày
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public SearchFreeRoomFrm(User user, JFrame homeFrame) {
        super("Search available rooms");
        this.user = user;
        this.homeFrame = homeFrame;
        this.booking = new Booking(); // Khởi tạo booking mới
        this.booking.setCreator(user);
        
        listRoom = new ArrayList<Room>();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Search Available Rooms");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

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
                    // Lấy phòng đã chọn
                    Room selectedRoom = listRoom.get(row);
                    
                    // Lấy ngày checkin/checkout từ text field
                    Date checkin, checkout;
                    try {
                        checkin = sdf.parse(txtCheckin.getText());
                        checkout = sdf.parse(txtCheckout.getText());
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid date format. Please use yyyy-MM-dd.");
                        return;
                    }
                    
                    // Tạo một BookedRoom mới
                    BookedRoom br = new BookedRoom();
                    br.setRoom(selectedRoom);
                    br.setCheckin(checkin);
                    br.setCheckout(checkout);
                    br.setPrice(selectedRoom.getPrice()); // Giá gốc
                    br.setSaleoff(0); // Mặc định không giảm giá
                    br.setChecked(false); // Chưa checkin

                    // Thêm vào booking
                    booking.getBookedRoom().add(br);

                    // Chuyển sang màn hình tìm kiếm khách hàng
//                    (new SearchClientFrm(user, booking)).setVisible(true);
//                    dispose();
                    (new SearchClientFrm(user, booking, SearchFreeRoomFrm.this, homeFrame)).setVisible(true);
                setVisible(false);
//                    setVisible(false);
                }
            }
        });

        pnResult.add(scrollPane);
        pnMain.add(pnResult);
        JPanel pnBack = new JPanel();
        pnBack.setLayout(new BoxLayout(pnBack, BoxLayout.X_AXIS));
        pnBack.add(Box.createHorizontalGlue()); // Đẩy nút về bên phải
        
        btnBack = new JButton("Back to Home"); // Khởi tạo nút
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa
        btnBack.addActionListener(this);       // Thêm sự kiện
        
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        pnMain.add(btnBack); // Thêm thẳng nút Back vào main panel
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        this.add(pnMain);
        this.setSize(700, 450); // Tăng chiều cao
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
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
                listRoom = rd.searchFreeRoom(checkin, checkout);

                if(listRoom.isEmpty()){
                    JOptionPane.showMessageDialog(this, "No available rooms found for this period.");
                }
                
                // Cập nhật JTable
                String[] columnNames = {"Id", "Name", "Type", "Price", "Description"};
                String[][] value = new String[listRoom.size()][5];
                for (int i = 0; i < listRoom.size(); i++) {
                    value[i][0] = listRoom.get(i).getId() + "";
                    value[i][1] = listRoom.get(i).getName();
                    value[i][2] = listRoom.get(i).getType();
                    value[i][3] = listRoom.get(i).getPrice() + "";
                    value[i][4] = listRoom.get(i).getDes();
                }
                DefaultTableModel model = new DefaultTableModel(value, columnNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                tblResult.setModel(model);

            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd.");
            }
        }else if (e.getSource() == btnBack) {
            homeFrame.setVisible(true); // Mở lại menu chính (Seller hoặc Receptionist)
            this.dispose();// Đóng cửa sổ tìm phòng
        }
    }
}
