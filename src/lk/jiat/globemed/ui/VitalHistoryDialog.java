package lk.jiat.globemed.ui;

import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.PatientVitals;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VitalHistoryDialog extends JDialog {
    
    private final Patient patient;
    private final List<PatientVitals> vitals;
    
    public VitalHistoryDialog(Frame parent, Patient patient, List<PatientVitals> vitals) {
        super(parent, "Vital History", true);
        this.patient = patient;
        this.vitals = vitals;
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(800, 600);
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel lblTitle = new JLabel("Vital History for: " + patient.getFullName());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        titlePanel.add(lblTitle);
        add(titlePanel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"Date", "Nurse", "Temp (°C)", "BP (mmHg)", "HR (bpm)", "RR (/min)", "O2 Sat (%)", "Weight (kg)", "Height (cm)", "Notes"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Populate table
        for (PatientVitals vital : vitals) {
            Object[] row = {
                vital.getRecordedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                vital.getNurse().getName(),
                vital.getTemperature() != null ? vital.getTemperature() : "",
                vital.getBloodPressure(),
                vital.getHeartRate() != null ? vital.getHeartRate() : "",
                vital.getRespiratoryRate() != null ? vital.getRespiratoryRate() : "",
                vital.getOxygenSaturation() != null ? vital.getOxygenSaturation() : "",
                vital.getWeight() != null ? vital.getWeight() : "",
                vital.getHeight() != null ? vital.getHeight() : "",
                vital.getNotes() != null ? vital.getNotes() : ""
            };
            tableModel.addRow(row);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}