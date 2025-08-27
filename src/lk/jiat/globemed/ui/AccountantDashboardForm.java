package lk.jiat.globemed.ui;

import lk.jiat.globemed.model.Staff;

import javax.swing.*;
import java.awt.*;

public class AccountantDashboardForm extends JFrame {

    private Staff currentUser;
    private JLabel lblTitle;
    private JButton btnLogout;
    private JTabbedPane tabMain;
    private JTable tblBilling;
    private JButton btnProcessPayment;
    private JButton btnGenerateInvoice;
    private JButton btnGenerateFinancialReport;

    public AccountantDashboardForm() {
        initComponents();
        postInit(null);
    }

    public AccountantDashboardForm(Staff user) {
        initComponents();
        postInit(user);
    }

    private void postInit(Staff user) {
        this.currentUser = user;
        setTitle("Accountant Dashboard - " + (currentUser != null ? currentUser.getName() : ""));
        setLocationRelativeTo(null);
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        btnProcessPayment.addActionListener(e -> JOptionPane.showMessageDialog(this, "Process payment (implement)."));
        btnGenerateInvoice.addActionListener(e -> JOptionPane.showMessageDialog(this, "Generate invoice (implement)."));
        btnGenerateFinancialReport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Generate financial report (implement)."));
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        lblTitle = new JLabel("Accountant Dashboard");
        lblTitle.setFont(lblTitle.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        btnLogout = new JButton("Logout");
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        add(header, BorderLayout.NORTH);

        tabMain = new JTabbedPane();

        JPanel tabBilling = new JPanel(new BorderLayout());
        tblBilling = new JTable();
        tabBilling.add(new JScrollPane(tblBilling), BorderLayout.CENTER);
        JPanel billingButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnProcessPayment = new JButton("Process Payment");
        btnGenerateInvoice = new JButton("Generate Invoice");
        billingButtons.add(btnProcessPayment);
        billingButtons.add(btnGenerateInvoice);
        tabBilling.add(billingButtons, BorderLayout.SOUTH);

        JPanel tabReports = new JPanel(new BorderLayout());
        btnGenerateFinancialReport = new JButton("Generate Financial Report");
        tabReports.add(btnGenerateFinancialReport, BorderLayout.NORTH);

        tabMain.addTab("Billing", tabBilling);
        tabMain.addTab("Reports", tabReports);

        add(tabMain, BorderLayout.CENTER);
    }
}