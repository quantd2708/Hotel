package view.service;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dao.ServiceDAO;
import dao.UsedServiceDAO;
import model.Service;
import model.UsedService;

// Đây là một JDialog (cửa sổ pop-up)
public class AddServiceDialog extends JDialog implements ActionListener {
    private JTextField txtKey;
    private JButton btnSearch, btnCancel;
    private JTable tblResult;
    private ArrayList<Service> listService;
    private int bookedRoomID;
    private JFrame parentFrame; // Frame cha (SearchOccupiedRoomFrm)

    public AddServiceDialog(JFrame parent, int bookedRoomID) {
        // 'true' nghĩa là modal (chặn tương tác với cửa sổ cha)
        super(parent, "Add Service to Room", true); 
        
        this.parentFrame = parent;
        this.bookedRoomID = bookedRoomID;
        listService = new ArrayList<>();

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHome = new JLabel("Search and Select a Service");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHome.setFont(lblHome.getFont().deriveFont(16.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        // Panel Tìm kiếm
        JPanel pnSearch = new JPanel();
        pnSearch.setLayout(new BoxLayout(pnSearch, BoxLayout.X_AXIS));
        pnSearch.add(new JLabel("Service Name: "));
        txtKey = new JTextField(15);
        pnSearch.add(txtKey);
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(this);
        pnSearch.add(btnSearch);
        pnMain.add(pnSearch);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        // Panel Kết quả
        JPanel pnResult = new JPanel();
        pnResult.setLayout(new BoxLayout(pnResult, BoxLayout.Y_AXIS));
        tblResult = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        
        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblResult.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    Service selectedService = listService.get(row);
                    addService(selectedService);
                }
            }
        });
        
        pnResult.add(scrollPane);
        pnMain.add(pnResult);
        
        // Nút Cancel
        btnCancel = new JButton("Cancel");
        btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCancel.addActionListener(this);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        pnMain.add(btnCancel);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        
        this.setContentPane(pnMain);
        this.setSize(500, 350);
        this.setLocationRelativeTo(parent); // Hiển thị giữa frame cha
    }
    
    // Hàm xử lý khi nhấn vào một dịch vụ
    private void addService(Service service) {
        // 1. Hỏi số lượng
        String sQuantity = JOptionPane.showInputDialog(this, 
                "Enter quantity for: " + service.getName(), "1");
        int quantity;
        try {
            quantity = Integer.parseInt(sQuantity);
            if(quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }
        
        // 2. Tạo đối tượng UsedService
        UsedService us = new UsedService();
        us.setQuantity(quantity);
        us.setPrice(service.getPrice()); // Lấy giá gốc
        us.setSellOff(0); // Mặc định không giảm giá
        
        // 3. Gọi DAO
        UsedServiceDAO usDAO = new UsedServiceDAO();
        if(usDAO.addUsedService(us, bookedRoomID, service.getId())) {
            JOptionPane.showMessageDialog(this, "Service added!");
            
            // Không cần gọi refreshData() như ở CheckoutFrm
            
            this.dispose(); // Đóng pop-up
        } else {
            JOptionPane.showMessageDialog(this, "Error adding service.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            String key = txtKey.getText();
            ServiceDAO sd = new ServiceDAO();
            listService = sd.searchService(key.trim());

            String[] columnNames = {"ID", "Name", "Unit", "Price"};
            String[][] value = new String[listService.size()][4];
            for (int i = 0; i < listService.size(); i++) {
                Service s = listService.get(i);
                value[i][0] = s.getId() + "";
                value[i][1] = s.getName();
                value[i][2] = s.getUnity();
                value[i][3] = String.format("%.0f", s.getPrice());
            }
            DefaultTableModel model = new DefaultTableModel(value, columnNames) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            tblResult.setModel(model);
        } else if (e.getSource() == btnCancel) {
            this.dispose();
        }
    }
}