package lk.jiat.globemed.ui;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import lk.jiat.globemed.dao.PatientDao;
import lk.jiat.globemed.dao.StaffDao;
import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.service.AppointmentService;
import lk.jiat.globemed.service.command.CancelAppointmentCommand;
import lk.jiat.globemed.service.command.CommandInvoker;
import lk.jiat.globemed.service.command.CreateAppointmentCommand;
import lk.jiat.globemed.service.command.RescheduleAppointmentCommand;

/**
 * AppointmentForm - Swing UI for managing appointments. Programmatic
 * initComponents so it runs immediately; .form placeholder is included for
 * NetBeans.
 */
public class AppointmentForm extends JFrame {

    private JTable tblAppointments;
    private JButton btnNew;
    private JButton btnCancel;
    private JButton btnReschedule;
    private JButton btnRefresh;

    private JComboBox<String> cmbPatients;
    private JComboBox<String> cmbDoctors;
    private JTextField txtDateTime; // format: yyyy-MM-dd HH:mm
    private JComboBox<String> cmbStatus;

    private final AppointmentService appointmentService = new AppointmentService();
    private final PatientDao patientDao = new PatientDao();
    private final StaffDao staffDao = new StaffDao();
    private final CommandInvoker invoker = new CommandInvoker();

    private List<Patient> patientList;
    private List<Staff> staffList;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentForm() {
        initComponents();
        postInit();
    }

    private void postInit() {
        setTitle("Appointments");
        setLocationRelativeTo(null);
        loadPatients();
        loadDoctors();
        refreshAppointmentsTable();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1200, 650);
        setLayout(new BorderLayout(8, 8));

        // header
        JPanel header = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("Appointment Management");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 18f));
        header.add(lbl, BorderLayout.WEST);

        JPanel headerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRefresh = new JButton("Refresh");
        headerBtns.add(btnRefresh);
        header.add(headerBtns, BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(header, BorderLayout.NORTH);

        // center split: left = form, right = table
        JPanel left = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        left.add(new JLabel("Patient:"), gbc);
        cmbPatients = new JComboBox<>();
        cmbPatients.setPreferredSize(new Dimension(300, 24));
        gbc.gridx = 1;
        gbc.gridy = 0;
        left.add(cmbPatients, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        left.add(new JLabel("Doctor:"), gbc);
        cmbDoctors = new JComboBox<>();
        cmbDoctors.setPreferredSize(new Dimension(300, 24));
        gbc.gridx = 1;
        gbc.gridy = 1;
        left.add(cmbDoctors, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        left.add(new JLabel("Date & Time (yyyy-MM-dd HH:mm):"), gbc);
        txtDateTime = new JTextField(20);
        txtDateTime.setText(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).format(dtf));
        gbc.gridx = 1;
        gbc.gridy = 2;
        left.add(txtDateTime, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        left.add(new JLabel("Status:"), gbc);
        cmbStatus = new JComboBox<>(new String[]{"Scheduled", "Completed", "Cancelled", "Rescheduled"});
        gbc.gridx = 1;
        gbc.gridy = 3;
        left.add(cmbStatus, gbc);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNew = new JButton("Book Appointment");
        btnCancel = new JButton("Cancel Appointment");
        btnReschedule = new JButton("Reschedule Appointment");
        actionBtns.add(btnNew);
        actionBtns.add(btnReschedule);
        actionBtns.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        left.add(actionBtns, gbc);

        // right: table
        tblAppointments = new JTable();
        JScrollPane tableScroll = new JScrollPane(tblAppointments);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, tableScroll);
        split.setDividerLocation(580);
        add(split, BorderLayout.CENTER);

        // wiring listeners
        btnNew.addActionListener(e -> onCreateAppointment());
        btnCancel.addActionListener(e -> onCancelAppointment());
        btnReschedule.addActionListener(e -> onRescheduleAppointment());
        btnRefresh.addActionListener(e -> refreshAppointmentsTable());
    }

    private void loadPatients() {
        try {
            patientList = patientDao.findAll();
            cmbPatients.removeAllItems();
//            cmbPatients.addItem("-- Select Patient --");
            for (Patient p : patientList) {
                cmbPatients.addItem(displayPatient(p));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load patients: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDoctors() {
        try {
            staffList = staffDao.findAll();
            cmbDoctors.removeAllItems();
            for (Staff s : staffList) {
                if (s.getRole() != null && "Doctor".equals(s.getRole().getName())) {
                    cmbDoctors.addItem(displayStaff(s));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load staff: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String displayPatient(Patient p) {
        return p.getId() + " - " + p.getFirstName() + " " + p.getLastName();
    }

    private String displayStaff(Staff s) {
        return s.getId() + " - " + s.getName();
    }

    private void refreshAppointmentsTable() {
        try {
            List<Appointment> appts = appointmentService.findAll();
            String[] cols = {"ID", "DateTime", "Status", "Patient", "Doctor"};
            DefaultTableModel m = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            for (Appointment a : appts) {
                String dt = a.getAppointmentDateTime() != null ? a.getAppointmentDateTime().format(dtf) : "";
                String patient = a.getPatient() != null ? (a.getPatient().getFirstName() + " " + a.getPatient().getLastName()) : "";
                String doc = a.getDoctor() != null ? a.getDoctor().getName() : "";
                m.addRow(new Object[]{a.getId(), dt, a.getStatus(), patient, doc});
            }
            tblAppointments.setModel(m);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load appointments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCreateAppointment() {
        try {
            int pIndex = cmbPatients.getSelectedIndex();
            int dIndex = cmbDoctors.getSelectedIndex();
            if (pIndex < 0 || dIndex < 0) {
                JOptionPane.showMessageDialog(this, "Select patient and doctor.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Patient selectedPatient = patientList.get(pIndex);

            // find doctor by ID extracted from combo display
            String docDisplay = (String) cmbDoctors.getSelectedItem();
            Long docId = Long.valueOf(docDisplay.split(" - ")[0]);
            Staff doctor = staffList.stream().filter(s -> s.getId().equals(docId)).findFirst().orElse(null);
            if (doctor == null) {
                JOptionPane.showMessageDialog(this, "Doctor not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String dtStr = txtDateTime.getText().trim();
            LocalDateTime dt = LocalDateTime.parse(dtStr, dtf);

            Appointment ap = new Appointment();
            ap.setPatient(selectedPatient);
            ap.setDoctor(doctor);
            ap.setAppointmentDateTime(dt);
            ap.setStatus((String) cmbStatus.getSelectedItem());

            CreateAppointmentCommand cmd = new CreateAppointmentCommand(appointmentService, ap, "system");
            invoker.executeCommand(cmd);

            refreshAppointmentsTable();
            JOptionPane.showMessageDialog(this, "Appointment created.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to create appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void onCancelAppointment() {
        try {
            int row = tblAppointments.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an appointment first.");
                return;
            }
            Long id = ((Number) tblAppointments.getValueAt(row, 0)).longValue();
            int confirm = JOptionPane.showConfirmDialog(this, "Cancel appointment id=" + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            CancelAppointmentCommand cmd = new CancelAppointmentCommand(appointmentService, id, "Cancelled via UI", "system");
            invoker.executeCommand(cmd);
            refreshAppointmentsTable();
            JOptionPane.showMessageDialog(this, "Appointment cancelled.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to cancel appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void onRescheduleAppointment() {
        try {
            int row = tblAppointments.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an appointment first.");
                return;
            }
            Long id = ((Number) tblAppointments.getValueAt(row, 0)).longValue();

            String input = JOptionPane.showInputDialog(this, "Enter new date-time (yyyy-MM-dd HH:mm):");
            if (input == null || input.trim().isEmpty()) {
                return;
            }
            LocalDateTime newDt = LocalDateTime.parse(input.trim(), dtf);

            RescheduleAppointmentCommand cmd = new RescheduleAppointmentCommand(appointmentService, id, newDt, "system");
            invoker.executeCommand(cmd);
            refreshAppointmentsTable();
            JOptionPane.showMessageDialog(this, "Appointment rescheduled.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to reschedule appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // main for quick test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AppointmentForm().setVisible(true);
        });
    }
}
