package lk.jiat.globemed.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.service.AppointmentService;
import lk.jiat.globemed.service.PatientService;

public class DoctorDashboardForm extends JFrame {

    private final Staff loggedInDoctor;
    private final AppointmentService appointmentService;
    private final PatientService patientService;

    private JTable appointmentTable;
    private JTable patientTable;
    private DefaultTableModel appointmentTableModel;
    private DefaultTableModel patientTableModel;

    private JTabbedPane tabbedPane;

    public DoctorDashboardForm(Staff doctor) {
        if (doctor == null || !"Doctor".equalsIgnoreCase(doctor.getRole().getName())) {
            throw new IllegalArgumentException("DoctorDashboardForm requires a Staff with role=Doctor");
        }

        this.loggedInDoctor = doctor;
        this.appointmentService = new AppointmentService();
        this.patientService = new PatientService();

        initUI();
        loadAppointments();
        loadPatients();
    }

    private void initUI() {
        setTitle("Doctor Dashboard - Dr. " + loggedInDoctor.getName());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        JPanel appointmentsPanel = new JPanel(new BorderLayout());

        appointmentTableModel = new DefaultTableModel(
                new Object[]{"ID", "Patient", "Date/Time", "Status"}, 0
        );
        appointmentTable = new JTable(appointmentTableModel);

        JScrollPane appointmentScroll = new JScrollPane(appointmentTable);

        JPanel appointmentBtnPanel = new JPanel();
        JButton btnNew = new JButton("New Appointment");
        JButton btnCancel = new JButton("Cancel");
        JButton btnReschedule = new JButton("Reschedule");

        btnNew.addActionListener(this::onNewAppointment);
        btnCancel.addActionListener(this::onCancelAppointment);
        btnReschedule.addActionListener(this::onRescheduleAppointment);

        appointmentBtnPanel.add(btnNew);
        appointmentBtnPanel.add(btnCancel);
        appointmentBtnPanel.add(btnReschedule);

        appointmentsPanel.add(appointmentScroll, BorderLayout.CENTER);
        appointmentsPanel.add(appointmentBtnPanel, BorderLayout.SOUTH);

        JPanel patientsPanel = new JPanel(new BorderLayout());

        patientTableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Mobile"}, 0
        );
        patientTable = new JTable(patientTableModel);

        JScrollPane patientScroll = new JScrollPane(patientTable);

        // Patient management buttons
        JPanel patientBtnPanel = new JPanel();
        JButton btnAddPatient = new JButton("Add Patient");
        JButton btnViewPatient = new JButton("View Details");
        JButton btnRefreshPatients = new JButton("Refresh");
        JButton btnMyPatients = new JButton("My Patients Only");
        JButton btnAllPatients = new JButton("All Patients");

        btnAddPatient.addActionListener(this::onAddPatient);
        btnViewPatient.addActionListener(this::onViewPatient);
        btnRefreshPatients.addActionListener(e -> loadPatients());
        btnMyPatients.addActionListener(e -> loadMyPatients());
        btnAllPatients.addActionListener(e -> loadPatients());

        patientBtnPanel.add(btnAddPatient);
        patientBtnPanel.add(btnViewPatient);
        patientBtnPanel.add(btnRefreshPatients);
        patientBtnPanel.add(btnMyPatients);
        patientBtnPanel.add(btnAllPatients);

        patientsPanel.add(patientScroll, BorderLayout.CENTER);
        patientsPanel.add(patientBtnPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Appointments", appointmentsPanel);
        tabbedPane.addTab("Patients", patientsPanel);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());

        topPanel.add(new JLabel("Doctor Dashboard - Dr. " + loggedInDoctor.getName()));
        topPanel.add(btnLogout);

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    private void loadAppointments() {
        appointmentTableModel.setRowCount(0);
        List<Appointment> appointments = appointmentService.getAppointmentsForDoctor(loggedInDoctor);
        for (Appointment a : appointments) {
            appointmentTableModel.addRow(new Object[]{
                a.getId(),
                a.getPatient().getFullName(),
                a.getAppointmentDateTime(),
                a.getStatus()
            });
        }
    }

    private void loadPatients() {
        patientTableModel.setRowCount(0);
        // Show all patients instead of just those with appointments
        List<Patient> patients = patientService.findAll();
        for (Patient p : patients) {
            patientTableModel.addRow(new Object[]{
                p.getId(),
                p.getFullName(),
                p.getContactNumber()
            });
        }
    }
    
    private void loadMyPatients() {
        patientTableModel.setRowCount(0);
        // Show only patients who have appointments with this doctor
        List<Patient> patients = patientService.findByDoctor(loggedInDoctor.getId());
        for (Patient p : patients) {
            patientTableModel.addRow(new Object[]{
                p.getId(),
                p.getFullName(),
                p.getContactNumber()
            });
        }
    }

    private void onNewAppointment(ActionEvent e) {
//        JOptionPane.showMessageDialog(this,
//                "TODO: Open NewAppointmentDialog and save via AppointmentService",
//                "Create appointment", JOptionPane.INFORMATION_MESSAGE);

        AppointmentForm apf = new AppointmentForm();
        apf.setVisible(true);

    }

    private void onCancelAppointment(ActionEvent e) {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment first.");
            return;
        }
        Long appointmentId = (Long) appointmentTableModel.getValueAt(row, 0);
        appointmentService.cancelAppointment(appointmentId, "Doctor / Patient unable to attend.");
        loadAppointments();
    }

    private void onRescheduleAppointment(ActionEvent e) {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment first.");
            return;
        }
        Long appointmentId = (Long) appointmentTableModel.getValueAt(row, 0);

        String newDateTime = JOptionPane.showInputDialog(this,
                "Enter new date/time (YYYY-MM-DD HH:mm):");

        if (newDateTime != null && !newDateTime.trim().isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime ldt = LocalDateTime.parse(newDateTime.trim(), formatter);
                appointmentService.rescheduleAppointment(appointmentId, ldt);
                loadAppointments();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date/time format. Please use YYYY-MM-DD HH:mm",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private void onAddPatient(ActionEvent e) {
        AddPatientDialog dialog = new AddPatientDialog(this);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadPatients(); // Refresh the patient list
            JOptionPane.showMessageDialog(this,
                    "Patient added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onViewPatient(ActionEvent e) {
        int row = patientTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient first.");
            return;
        }

        Long patientId = (Long) patientTableModel.getValueAt(row, 0);
        Patient patient = patientService.findById(patientId);

        if (patient != null) {
            String info = String.format(
                    "Patient Details:\n\n"
                    + "ID: %d\n"
                    + "Name: %s\n"
                    + "Phone: %s\n"
                    + "Date of Birth: %s\n"
                    + "Gender: %s\n"
                    + "Address: %s\n",
                    patient.getId(),
                    patient.getFullName(),
                    patient.getContactNumber(),
                    patient.getDob(),
                    patient.getGender(),
                    patient.getAddress() != null ? patient.getAddress() : "N/A"
            );

            JOptionPane.showMessageDialog(this, info, "Patient Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void logout() {
        this.dispose();
        new LoginForm().setVisible(true);
    }

    public static void main(String[] args) {

        Staff doc = new Staff();
        doc.setId(1L);
        doc.setName("Alice Fernando");
        doc.setEmail("alice@globemed.lk");

        lk.jiat.globemed.model.Role doctorRole = new lk.jiat.globemed.model.Role();
        doctorRole.setName("Doctor");
        doc.setRole(doctorRole);

        SwingUtilities.invokeLater(() -> new DoctorDashboardForm(doc).setVisible(true));
    }
}
