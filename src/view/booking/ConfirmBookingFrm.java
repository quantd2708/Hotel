package view.booking;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
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
import java.util.ArrayList;
import model.BookedRoom;
import model.Booking;
import model.Client;
import model.User;
import view.user.SellerHomeFrm;

public class ConfirmBookingFrm extends JFrame implements ActionListener {
    private User user;
    private Booking booking;
    private JTextField txtSaleoff, txtNote;
    private JButton btnConfirm, btnCancel;
    private JLabel lblTotalAmount;
    private JFrame parentFrame;
    private JFrame homeFrame;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ConfirmBookingFrm(User user, Booking booking, JFrame parentFrame, JFrame homeFrame) {
        super("Confirm Booking");
        this.user = user;
        this.booking = booking;
        this.parentFrame = parentFrame;
        this.homeFrame = homeFrame;
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblTitle = new JLabel("Confirm Booking Information");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Thông tin Client
        pnMain.add(createTitleLabel("Client Information"));
        pnMain.add(createClientInfoPanel(booking.getClient()));
        pnMain.add(Box.createRigidArea(new Dimension(0, 15)));

        // Thông tin Phòng đã chọn
        pnMain.add(createTitleLabel("Booked Room(s)"));
        pnMain.add(createBookedRoomsPanel());
        pnMain.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Thông tin thêm
        pnMain.add(createTitleLabel("Booking Details"));
        pnMain.add(createDetailsPanel());
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Nút bấm
        JPanel pnButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        btnConfirm = new JButton("Confirm Booking");
        btnConfirm.addActionListener(this);
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        pnButtons.add(btnConfirm);
        pnButtons.add(btnCancel);
        pnMain.add(pnButtons);

        this.setContentPane(pnMain);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private JLabel createTitleLabel(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createClientInfoPanel(Client client) {
        JPanel panel = new JPanel(new GridLayout(3, 4, 5, 5));
        panel.add(new JLabel("Full Name:"));
        panel.add(new JLabel(client.getFullName()));
        panel.add(new JLabel("ID Card:"));
        panel.add(new JLabel(client.getIdCard()));
        panel.add(new JLabel("Address:"));
        panel.add(new JLabel(client.getAddress()));
        panel.add(new JLabel("Telephone:"));
        panel.add(new JLabel(client.getTel()));
        panel.add(new JLabel("Email:"));
        panel.add(new JLabel(client.getEmail()));
        return panel;
    }

    private JScrollPane createBookedRoomsPanel() {
        String[] columnNames = {"Room Name", "Type", "Check-in", "Check-out", "Price/Night"};
        ArrayList<BookedRoom> brs = booking.getBookedRoom();
        String[][] value = new String[brs.size()][5];
        
        for (int i = 0; i < brs.size(); i++) {
            BookedRoom br = brs.get(i);
            value[i][0] = br.getRoom().getName();
            value[i][1] = br.getRoom().getType();
            value[i][2] = sdf.format(br.getCheckin());
            value[i][3] = sdf.format(br.getCheckout());
            value[i][4] = String.format("%.0f", br.getPrice());
        }
        
        DefaultTableModel model = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        return new JScrollPane(table);
    }
    
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Overall Saleoff (%):"));
        txtSaleoff = new JTextField("0.0");
        panel.add(txtSaleoff);
        
        panel.add(new JLabel("Note:"));
        txtNote = new JTextField();
        panel.add(txtNote);
        
        lblTotalAmount = new JLabel("Total Amount: " + String.format("%.0f VND", booking.getTotal()));
        lblTotalAmount.setFont(new Font(lblTotalAmount.getFont().getName(), Font.BOLD, 14));
        panel.add(lblTotalAmount);
        
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirm) {
            // Lấy thông tin cuối cùng
            try {
                booking.setSaleoff(Float.parseFloat(txtSaleoff.getText()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Saleoff must be a number.");
                return;
            }
            booking.setNote(txtNote.getText());
            booking.setBookedDate(new Date()); // Ngày hiện tại

            // Gọi DAO
            BookingDAO bd = new BookingDAO();
            if (bd.addBooking(booking)) {
                JOptionPane.showMessageDialog(this, "Booking successful!");
                homeFrame.setVisible(true);
                this.dispose();
                parentFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Booking failed. One or more rooms might be unavailable. Please try again.");
                // Quay lại màn hình tìm phòng
                homeFrame.setVisible(true);
                this.dispose();
                parentFrame.dispose();
            }

        } else if (e.getSource() == btnCancel) {
            // Quay về màn hình seller home
            homeFrame.setVisible(true);
            this.dispose();
        }
    }
}
