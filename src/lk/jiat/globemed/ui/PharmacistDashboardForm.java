package lk.jiat.globemed.ui;

import lk.jiat.globemed.model.Staff;

import javax.swing.*;
import java.awt.*;

public class PharmacistDashboardForm extends JFrame {

    private Staff currentUser;
    private JLabel lblTitle;
    private JButton btnLogout;
    private JTabbedPane tabMain;
    private JTable tblMedications;
    private JButton btnAddMedication;
    private JButton btnDispense;
    private JTable tblPrescriptions;
    private JButton btnFillPrescription;

    public PharmacistDashboardForm() {
        initComponents();
        postInit(null);
    }

    public PharmacistDashboardForm(Staff user) {
        initComponents();
        postInit(user);
    }

    private void postInit(Staff user) {
        this.currentUser = user;
        setTitle("Pharmacist Dashboard - " + (currentUser != null ? currentUser.getName() : ""));
        setLocationRelativeTo(null);
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        btnDispense.addActionListener(e -> JOptionPane.showMessageDialog(this, "Dispense medication (implement)."));
        btnFillPrescription.addActionListener(e -> JOptionPane.showMessageDialog(this, "Fill prescription (implement)."));
        btnAddMedication.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add medication (implement)."));
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        lblTitle = new JLabel("Pharmacist Dashboard");
        lblTitle.setFont(lblTitle.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        btnLogout = new JButton("Logout");
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        add(header, BorderLayout.NORTH);

        tabMain = new JTabbedPane();

        JPanel tabInventory = new JPanel(new BorderLayout());
        tblMedications = new JTable();
        tabInventory.add(new JScrollPane(tblMedications), BorderLayout.CENTER);
        JPanel invButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAddMedication = new JButton("Add Medication");
        btnDispense = new JButton("Dispense");
        invButtons.add(btnAddMedication);
        invButtons.add(btnDispense);
        tabInventory.add(invButtons, BorderLayout.SOUTH);

        JPanel tabPrescriptions = new JPanel(new BorderLayout());
        tblPrescriptions = new JTable();
        tabPrescriptions.add(new JScrollPane(tblPrescriptions), BorderLayout.CENTER);
        JPanel prButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnFillPrescription = new JButton("Fill Prescription");
        prButtons.add(btnFillPrescription);
        tabPrescriptions.add(prButtons, BorderLayout.SOUTH);

        tabMain.addTab("Inventory", tabInventory);
        tabMain.addTab("Prescriptions", tabPrescriptions);

        add(tabMain, BorderLayout.CENTER);
    }
}