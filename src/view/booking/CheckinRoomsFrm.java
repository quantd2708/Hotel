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
import javax.swing.table.DefaultTableModel;
import dao.BookingDAO;
import model.BookedRoom;
import model.User;

public class CheckinRoomsFrm extends JFrame implements ActionListener {
    private ArrayList<BookedRoom> listBookedRoom;
    private JTable tblResult;
    private User user;
    private int bookingID;
    private JFrame parentFrame; // Frame cha (SearchCheckinBookingFrm)
    private JButton btnBack;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public CheckinRoomsFrm(User user, int bookingID, JFrame parent) {
        super("Rooms to Check-in (Booking ID: " + bookingID + ")");
        this.user = user;
        this.bookingID = bookingID;
        this.parentFrame = parent;

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Rooms Pending Check-in");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel lblSub = new JLabel("Click on a room to perform check-in");
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
                int row = tblResult.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    BookedRoom selectedRoom = listBookedRoom.get(row);
                    
                    int confirm = JOptionPane.showConfirmDialog(CheckinRoomsFrm.this,
                            "Confirm check-in for Room: " + selectedRoom.getRoom().getName() + "?",
                            "Confirm Check-in",
                            JOptionPane.YES_NO_OPTION);
                        
                    if (confirm == JOptionPane.YES_OPTION) {
                        BookingDAO bd = new BookingDAO();
                        if (bd.performCheckin(selectedRoom.getId())) {
                            JOptionPane.showMessageDialog(CheckinRoomsFrm.this, "Room " + selectedRoom.getRoom().getName() + " checked-in!");
                            // Xóa khỏi bảng
                            listBookedRoom.remove(row);
                            ((DefaultTableModel)tblResult.getModel()).removeRow(row);
                        } else {
                            JOptionPane.showMessageDialog(CheckinRoomsFrm.this, "Error: Could not perform check-in.");
                        }
                    }
                }
            }
        });
        
        pnResult.add(scrollPane);
        pnMain.add(pnResult);
        
        // Nút Back
        btnBack = new JButton("Back to Search");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(this);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        pnMain.add(btnBack);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        this.add(pnMain);
        this.setSize(700, 450);
        this.setLocationRelativeTo(parentFrame);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Tải dữ liệu
        loadRooms();
    }
    
    private void loadRooms() {
        BookingDAO bd = new BookingDAO();
        listBookedRoom = bd.getBookedRoomsForCheckin(bookingID);
        
        String[] columnNames = {"BookedRoom ID", "Room Name", "Room Type", "Check-in Date", "Check-out Date"};
        String[][] value = new String[listBookedRoom.size()][5];
        for (int i = 0; i < listBookedRoom.size(); i++) {
            BookedRoom br = listBookedRoom.get(i);
            value[i][0] = br.getId() + "";
            value[i][1] = br.getRoom().getName();
            value[i][2] = br.getRoom().getType();
            value[i][3] = sdf.format(br.getCheckin());
            value[i][4] = sdf.format(br.getCheckout());
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
            parentFrame.setVisible(true); // Hiển thị lại frame tìm kiếm
            CheckinRoomsFrm.super.dispose(); // Đóng frame này
        }
    }
    
    // Ghi đè dispose để đảm bảo frame cha được hiển thị lại
    @Override
    public void dispose() {
        parentFrame.setVisible(true);
        CheckinRoomsFrm.super.dispose();
    }
}