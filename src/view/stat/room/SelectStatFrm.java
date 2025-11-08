package view.stat.room;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.User;
import view.user.ManagerHomeFrm;
import view.stat.client.ClientStatFrm;
import view.stat.service.ServiceStatFrm;
import view.stat.income.IncomeStatFrm;

public class SelectStatFrm extends JFrame implements ActionListener {
    private User user;
    private JButton btnRoomStat, btnClientStat, btnServiceStat, btnRevenueStat, btnBack;

    public SelectStatFrm(User user) {
        super("Select Statistics");
        this.user = user;

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblTitle = new JLabel("Select Statistics Report");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Nút Thống kê Phòng (theo kịch bản)
        btnRoomStat = new JButton("Room Statistics (by Revenue/Filled Rate)");
        btnRoomStat.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRoomStat.addActionListener(this);
        pnMain.add(btnRoomStat);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        // Các nút khác (chưa làm)
        btnClientStat = new JButton("Client Statistics (by Revenue)");
        btnClientStat.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClientStat.addActionListener(this);
        pnMain.add(btnClientStat);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        btnServiceStat = new JButton("Service Statistics (by Revenue)");
        btnServiceStat.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnServiceStat.addActionListener(this);
        pnMain.add(btnServiceStat);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        
        btnRevenueStat = new JButton("Revenue Statistics (by Time)");
        btnRevenueStat.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRevenueStat.addActionListener(this);
        pnMain.add(btnRevenueStat);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        btnBack = new JButton("Back to Home");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(this);
        pnMain.add(btnBack);

        this.setContentPane(pnMain);
        this.setSize(600, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRoomStat) {
            // Mở màn hình Thống kê phòng
            (new RoomStatFrm(user)).setVisible(true);
            this.dispose();
            
        // SỬA ĐỔI TẠI ĐÂY
        } else if (e.getSource() == btnClientStat) {
            // Mở màn hình Thống kê Client
            (new ClientStatFrm(user)).setVisible(true);
            this.dispose();
        // KẾT THÚC SỬA ĐỔI
            
        }else if (e.getSource() == btnServiceStat) {
            (new ServiceStatFrm(user)).setVisible(true);
            this.dispose();
        // ================= KẾT THÚC SỬA =================
            
        }else if (e.getSource() == btnRevenueStat) {
            (new IncomeStatFrm(user)).setVisible(true);
            this.dispose();
        // ================= KẾT THÚC SỬA =================
            
        } else if (e.getSource() == btnBack) {
            (new ManagerHomeFrm(user)).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "This function is under construction!");
        }
    }
}
