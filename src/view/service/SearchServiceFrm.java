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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dao.ServiceDAO;
import model.Service;
import model.User;

// Tương tự SearchRoomFrm
public class SearchServiceFrm extends JFrame implements ActionListener {
    private ArrayList<Service> listService;
    private JTextField txtKey;
    private JButton btnSearch, btnBack;
    private JTable tblResult;
    private User user;
    private String mode; // "edit" hoặc "delete"
    private JFrame parentFrame; // ManageServiceFrm
    
    public SearchServiceFrm(User user, String mode, JFrame parent) {
        super("Search service to " + mode);
        this.user = user;
        this.mode = mode; 
        this.parentFrame = parent;
        listService = new ArrayList<>();
        
        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel lblHome = new JLabel("Search a service to " + mode);
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);  
        lblHome.setFont(lblHome.getFont().deriveFont (20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel pn1 = new JPanel();
        pn1.setLayout(new BoxLayout(pn1, BoxLayout.X_AXIS));
        pn1.add(new JLabel("Service name: "));
        txtKey = new JTextField();
        pn1.add(txtKey);
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(this);
        pn1.add(btnSearch);
        pnMain.add(pn1);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel pn2 = new JPanel();
        pn2.setLayout(new BoxLayout(pn2, BoxLayout.Y_AXIS));     
        tblResult = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false); 
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));
        
        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblResult.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    Service selectedService = listService.get(row);

                    if (mode.equals("edit")) {
                        (new EditServiceFrm(user, selectedService, parentFrame)).setVisible(true);
//                        dispose(); // Đóng frame tìm kiếm
                        SearchServiceFrm.super.dispose();
                    } else if (mode.equals("delete")) {
                        int confirm = JOptionPane.showConfirmDialog(SearchServiceFrm.this,
                                "Delete service: " + selectedService.getName() + "?",
                                "Confirm Deletion",
                                JOptionPane.YES_NO_OPTION);
                        
                        if (confirm == JOptionPane.YES_OPTION) {
                            ServiceDAO sd = new ServiceDAO();
                            if (sd.deleteService(selectedService.getId())) {
                                JOptionPane.showMessageDialog(SearchServiceFrm.this, "Service deleted!");
                                listService.remove(row);
                                ((DefaultTableModel)tblResult.getModel()).removeRow(row);
                            } else {
                                JOptionPane.showMessageDialog(SearchServiceFrm.this, "Error: Service might be in use (referenced by a booking).");
                            }
                        }
                    }
                }
            }
        });

        pn2.add(scrollPane);
        pnMain.add(pn2);
        
        btnBack = new JButton("Back");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(this);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        pnMain.add(btnBack);
        
        this.add(pnMain);
        this.setSize(600, 400);       
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            String key = txtKey.getText();
            if (key == null) key = "";
            
            ServiceDAO sd = new ServiceDAO();
            listService = sd.searchService(key.trim());

            String[] columnNames = {"Id", "Name", "Unit", "Price", "Description"};
            String[][] value = new String[listService.size()][5];
            for(int i=0; i<listService.size(); i++){
                value[i][0] = listService.get(i).getId() +"";
                value[i][1] = listService.get(i).getName();
                value[i][2] = listService.get(i).getUnity(); // Tên model là 'unity'
                value[i][3] = listService.get(i).getPrice() +"";
                value[i][4] = listService.get(i).getDescription();
            }
            DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tblResult.setModel(tableModel);
        } else if (e.getSource() == btnBack) {
            parentFrame.setVisible(true);
            SearchServiceFrm.super.dispose();
        }
    }
    
    // Đảm bảo frame cha được hiển thị lại khi đóng
    @Override
    public void dispose() {
        parentFrame.setVisible(true);
        super.dispose();
    }
}