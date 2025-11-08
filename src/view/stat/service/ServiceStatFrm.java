package view.stat.service;

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
import dao.ServiceStatDAO; // Import DAO mới
import model.ServiceStat; // Import model mới
import model.User;
import view.stat.room.SelectStatFrm; // Nút Back sẽ quay về đây

public class ServiceStatFrm extends JFrame implements ActionListener {
    private User user;
    private ArrayList<ServiceStat> listServiceStat;
    private JTextField txtStartDate, txtEndDate;
    private JButton btnView, btnBack;
    private JTable tblResult;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ServiceStatFrm(User user) {
        super("Service Statistics by Revenue");
        this.user = user;
        listServiceStat = new ArrayList<>();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblTitle = new JLabel("Service Statistics by Revenue");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel chọn ngày
        JPanel pnDate = new JPanel();
        pnDate.setLayout(new BoxLayout(pnDate, BoxLayout.X_AXIS));
        pnDate.add(new JLabel("Start Date (yyyy-MM-dd): "));
        txtStartDate = new JTextField(10);
        pnDate.add(txtStartDate);
        pnDate.add(Box.createRigidArea(new Dimension(10, 0)));
        pnDate.add(new JLabel("End Date (yyyy-MM-dd): "));
        txtEndDate = new JTextField(10);
        pnDate.add(txtEndDate);
        pnDate.add(Box.createRigidArea(new Dimension(10, 0)));
        btnView = new JButton("View Report");
        btnView.addActionListener(this);
        pnDate.add(btnView);
        pnMain.add(pnDate);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Panel kết quả
        JPanel pnResult = new JPanel();
        pnResult.setLayout(new BoxLayout(pnResult, BoxLayout.Y_AXIS));
        tblResult = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 300));
        
        pnResult.add(scrollPane);
        pnMain.add(pnResult);
        
        // Nút Back
        btnBack = new JButton("Back");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(this);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        pnMain.add(btnBack);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        this.setContentPane(pnMain);
        this.setSize(800, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnView) {
            String sStartDate = txtStartDate.getText();
            String sEndDate = txtEndDate.getText();
            if (sStartDate.isEmpty() || sEndDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both start and end dates.");
                return;
            }

            try {
                Date startDate = sdf.parse(sStartDate);
                Date endDate = sdf.parse(sEndDate);

                if (endDate.before(startDate)) {
                    JOptionPane.showMessageDialog(this, "End date must be after start date.");
                    return;
                }
                
                // Gọi DAO
                ServiceStatDAO ssDAO = new ServiceStatDAO();
                listServiceStat = ssDAO.getServiceStat(startDate, endDate);
                
                // Cập nhật JTable
                String[] columnNames = {"ID", "Name", "Unit", "Total Quantity Sold", "Total Revenue"};
                String[][] value = new String[listServiceStat.size()][5];
                for (int i = 0; i < listServiceStat.size(); i++) {
                    ServiceStat s = listServiceStat.get(i);
                    value[i][0] = s.getId() + "";
                    value[i][1] = s.getName();
                    value[i][2] = s.getUnity();
                    value[i][3] = s.getTotalQuantity() + "";
                    value[i][4] = String.format("%.0f", s.getTotalRevenue());
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

        } else if (e.getSource() == btnBack) {
            (new SelectStatFrm(user)).setVisible(true);
            this.dispose();
        }
    }
}