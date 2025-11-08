package view.booking;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import dao.BillDAO;
import model.Bill;
import model.Booking;
import model.User;

/**
 * Đây là một JDialog (pop-up) modal
 * Dùng để nhập thông tin đặt cọc (Số tiền và Ghi chú)
 */
public class TakeDepositDialog extends JDialog implements ActionListener {
    
    private JTextField txtAmount, txtNote;
    private JButton btnConfirm, btnCancel;
    
    private ConfirmBookingFrm parentFrame; // Frame cha (ConfirmBookingFrm)
    private Booking booking;
    private User user;
    private BillDAO billDAO;

    public TakeDepositDialog(ConfirmBookingFrm parent, Booking booking, User user) {
        // 'true' = modal (chặn tương tác với frame cha)
        super(parent, "Take Deposit for Booking ID: " + booking.getId(), true); 
        
        this.parentFrame = parent;
        this.booking = booking;
        this.user = user;
        this.billDAO = new BillDAO();
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10)); // Thêm padding

        JLabel lblTitle = new JLabel("Take Deposit");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel Form
        JPanel pnForm = new JPanel(new GridLayout(3, 2, 5, 5));
        
        pnForm.add(new JLabel("Amount*:"));
        txtAmount = new JTextField(15);
        pnForm.add(txtAmount);
        
        pnForm.add(new JLabel("Note:"));
        txtNote = new JTextField(15);
        pnForm.add(txtNote);
        
        btnConfirm = new JButton("Confirm Deposit");
        btnConfirm.addActionListener(this);
        pnForm.add(btnConfirm);
        
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        pnForm.add(btnCancel);
        
        pnMain.add(pnForm);
        
        this.setContentPane(pnMain);
        this.pack(); // Tự động điều chỉnh kích thước
        this.setLocationRelativeTo(parent); // Hiển thị giữa frame cha
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirm) {
            actionConfirmDeposit();
        } else if (e.getSource() == btnCancel) {
            this.dispose(); // Đóng cửa sổ
        }
    }
    
    private void actionConfirmDeposit() {
        String sAmount = txtAmount.getText();
        String note = txtNote.getText();
        float amount;
        
        try {
            amount = Float.parseFloat(sAmount);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
            return;
        }
        
        // Tạo hóa đơn đặt cọc
        Bill depositBill = new Bill();
        depositBill.setBooking(booking);
        depositBill.setCreator(user);
    
        depositBill.setAmount(amount);
        depositBill.setPaymentDate(new Date());
        depositBill.setPaymentType("Deposit"); // Đánh dấu là cọc
        depositBill.setNote(note); // Dùng ghi chú mới
        
        if (billDAO.addBill(depositBill)) {
            JOptionPane.showMessageDialog(this, "Deposit of " + String.format("%.0f", amount) + " VND saved!");
            
            // Gọi hàm trong frame cha để cập nhật lại số tiền đã trả
            parentFrame.updateTotalPaid(amount); 
            
            this.dispose(); // Đóng cửa sổ
        } else {
            JOptionPane.showMessageDialog(this, "Error: Could not save deposit bill.");
        }
    }
}