package lk.jiat.globemed.ui;

import lk.jiat.globemed.model.Staff;

import javax.swing.*;
import java.awt.*;

public class DoctorDashboardForm extends JFrame {

    private Staff currentUser;
    private JLabel lblTitle;
    private JButton btnLogout;
    private JTabbedPane tabMain;
    private JTable tblAppointments;
    private JButton btnNewAppointment;
    private JButton btnCompleteAppointment;
    private JButton btnCancelAppointment;
    private JTable tblPatients;
    private JButton btnOpenPatient;

    public DoctorDashboardForm() {
        initComponents();
        postInit(null);
    }

    public DoctorDashboardForm(Staff user) {
        initComponents();
        postInit(user);
    }

    private void postInit(Staff user) {
        this.currentUser = user;
        setTitle("Doctor Dashboard - " + (currentUser != null ? currentUser.getName() : ""));
        setLocationRelativeTo(null);

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        btnNewAppointment.addActionListener(e -> JOptionPane.showMessageDialog(this, "Create appointment (implement)."));
        btnCompleteAppointment.addActionListener(e -> JOptionPane.showMessageDialog(this, "Mark completed (implement)."));
        btnCancelAppointment.addActionListener(e -> JOptionPane.showMessageDialog(this, "Cancel appointment (implement)."));
        btnOpenPatient.addActionListener(e -> JOptionPane.showMessageDialog(this, "Open patient details (implement)."));
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        lblTitle = new JLabel("Doctor Dashboard");
        lblTitle.setFont(lblTitle.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        btnLogout = new JButton("Logout");
        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        add(header, BorderLayout.NORTH);

        tabMain = new JTabbedPane();

        JPanel tabAppointments = new JPanel(new BorderLayout());
        tblAppointments = new JTable();
        tabAppointments.add(new JScrollPane(tblAppointments), BorderLayout.CENTER);
        JPanel apButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNewAppointment = new JButton("New Appointment");
        btnCompleteAppointment = new JButton("Complete");
        btnCancelAppointment = new JButton("Cancel");
        apButtons.add(btnNewAppointment);
        apButtons.add(btnCompleteAppointment);
        apButtons.add(btnCancelAppointment);
        tabAppointments.add(apButtons, BorderLayout.SOUTH);

        JPanel tabPatients = new JPanel(new BorderLayout());
        tblPatients = new JTable();
        tabPatients.add(new JScrollPane(tblPatients), BorderLayout.CENTER);
        JPanel ptButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnOpenPatient = new JButton("Open Patient");
        ptButtons.add(btnOpenPatient);
        tabPatients.add(ptButtons, BorderLayout.SOUTH);

        tabMain.addTab("Appointments", tabAppointments);
        tabMain.addTab("Patients", tabPatients);

        add(tabMain, BorderLayout.CENTER);
    }
}