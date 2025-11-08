package view.bill;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import model.Bill;

public class BillDetailFrm extends JFrame implements ActionListener {
    private Bill bill;
    private JFrame parentFrame; // Frame cha
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BillDetailFrm(Bill bill, JFrame parent) {
        super("Bill Detail: " + bill.getId());
        this.bill = bill;
        this.parentFrame = parent;

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Bill Detail");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font(lblTitle.getFont().getName(), Font.BOLD, 24));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel Thông tin
        JPanel pnInfo = new JPanel(new GridLayout(8, 2, 10, 10));
        
        pnInfo.add(createBoldLabel("Bill ID:"));
        pnInfo.add(new JLabel(String.valueOf(bill.getId())));
        
        pnInfo.add(createBoldLabel("Payment Date:"));
        pnInfo.add(new JLabel(sdf.format(bill.getPaymentDate())));
        
        pnInfo.add(createBoldLabel("Payment Type:"));
        pnInfo.add(new JLabel(bill.getPaymentType()));

        pnInfo.add(createBoldLabel("Client Name:"));
        pnInfo.add(new JLabel(bill.getBooking().getClient().getFullName()));
        
        pnInfo.add(createBoldLabel("Client ID Card:"));
        pnInfo.add(new JLabel(bill.getBooking().getClient().getIdCard()));
        
        pnInfo.add(createBoldLabel("Processed by:"));
        pnInfo.add(new JLabel(bill.getCreator().getName() + " (" + bill.getCreator().getPosition() + ")"));
        
        pnInfo.add(createBoldLabel("Note:"));
        pnInfo.add(new JLabel(bill.getNote()));
        
        pnInfo.add(createBoldLabel("FINAL AMOUNT:"));
        JLabel lblAmount = createBoldLabel(String.format("%.0f VND", bill.getAmount()));
        lblAmount.setFont(new Font(lblAmount.getFont().getName(), Font.BOLD, 16));
        pnInfo.add(lblAmount);

        pnMain.add(pnInfo);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Nút Back
        JButton btnBack = new JButton("Back");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(this);
        pnMain.add(btnBack);

        this.setContentPane(pnMain);
        this.pack(); // Tự động điều chỉnh kích thước
        this.setLocationRelativeTo(parentFrame);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 14));
        return label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Chỉ có nút Back
        parentFrame.setVisible(true);
        this.dispose();
    }
    
    // Ghi đè phương thức dispose để đảm bảo frame cha được hiển thị lại
    @Override
    public void dispose() {
        parentFrame.setVisible(true);
        super.dispose();
    }
}