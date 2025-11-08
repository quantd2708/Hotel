package view.stat.income;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
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
import dao.IncomeStatDAO; // Import DAO mới
import model.IncomeStat; // Import model
import model.User;
import view.stat.room.SelectStatFrm; // Nút Back sẽ quay về đây
import javax.swing.JComboBox;

public class IncomeStatFrm extends JFrame implements ActionListener {
    private User user;
    private ArrayList<IncomeStat> listIncomeStat;
    private JTextField txtYear;
    private JComboBox<String> cmbPeriodType;
    private JButton btnView, btnBack;
    private JTable tblResult;

    public IncomeStatFrm(User user) {
        super("Revenue Statistics by Time");
        this.user = user;
        listIncomeStat = new ArrayList<>();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblTitle = new JLabel("Revenue Statistics by Time");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel pnDate = new JPanel();
        pnDate.setLayout(new BoxLayout(pnDate, BoxLayout.X_AXIS));
        
        // Thêm ComboBox
        pnDate.add(new JLabel("Group by: "));
        String[] periodOptions = {"Monthly", "Quarterly", "Yearly"};
        cmbPeriodType = new JComboBox<>(periodOptions);
        pnDate.add(cmbPeriodType);
        pnDate.add(Box.createRigidArea(new Dimension(10, 0))); 

        pnDate.add(new JLabel("Enter Year: "));
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        txtYear = new JTextField(currentYear, 8); // Giảm kích thước
        pnDate.add(txtYear);
        
        pnDate.add(Box.createRigidArea(new Dimension(10, 0)));
        btnView = new JButton("View Report");
        btnView.addActionListener(this);
        pnDate.add(btnView);
        pnMain.add(pnDate);
        // --- KẾT THÚC SỬA ---
        
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
            String sYear = txtYear.getText();
            if (sYear.isEmpty() || sYear.length() != 4) {
                JOptionPane.showMessageDialog(this, "Please enter a valid 4-digit year.");
                return;
            }
            
            // Lấy kiểu thống kê
            String periodType = (String) cmbPeriodType.getSelectedItem();

            try {
                int year = Integer.parseInt(sYear);
                
                // Gọi DAO đã cập nhật
                IncomeStatDAO isDAO = new IncomeStatDAO();
                listIncomeStat = isDAO.getIncomeStat(year, periodType);
                
                // Cập nhật JTable
                String[] columnNames = {"Period", "Total Unique Clients", "Total Revenue"};
                String[][] value = new String[listIncomeStat.size()][3];
                for (int i = 0; i < listIncomeStat.size(); i++) {
                    IncomeStat is = listIncomeStat.get(i);
                    value[i][0] = is.getPeriodName();
                    value[i][1] = is.getTotalClient() + "";
                    value[i][2] = String.format("%.0f", is.getTotalIncome());
                }
                
                DefaultTableModel model = new DefaultTableModel(value, columnNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                tblResult.setModel(model);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid year format. Please enter a number.");
            }

        } else if (e.getSource() == btnBack) {
            (new SelectStatFrm(user)).setVisible(true);
            this.dispose();
        }
    }
}