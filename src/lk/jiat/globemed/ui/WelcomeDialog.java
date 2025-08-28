package lk.jiat.globemed.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Welcome dialog showing system information and default login credentials
 * 
 * @author upekhansaja
 */
public class WelcomeDialog extends JDialog {
    
    public WelcomeDialog(JFrame parent) {
        super(parent, "Welcome to GlobeMed Healthcare Management System", true);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(600, 500);
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(600, 80));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        
        JLabel titleLabel = new JLabel("🏥 GlobeMed Healthcare Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // System Information
        JLabel infoLabel = new JLabel("<html><h2>System Features:</h2></html>");
        contentPanel.add(infoLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        String[] features = {
            "• Complete Patient Management System",
            "• Staff Management with Role-based Access Control",
            "• Advanced Appointment Scheduling",
            "• Comprehensive Billing and Payment Tracking",
            "• Multi-channel Notification System (Email/SMS)",
            "• Audit Logging and Security Features",
            "• Advanced Reporting and Analytics",
            "• Implementation of 11+ Design Patterns"
        };
        
        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            contentPanel.add(featureLabel);
            contentPanel.add(Box.createVerticalStrut(5));
        }
        
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Login Credentials
        JLabel credentialsLabel = new JLabel("<html><h2>Default Login Credentials:</h2></html>");
        contentPanel.add(credentialsLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        String[] credentials = {
            "👨‍💼 Administrator: admin@globemed.lk / admin123",
            "👨‍⚕️ Doctor: doctor@globemed.lk / doctor123",
            "👩‍⚕️ Nurse: nurse@globemed.lk / nurse123",
            "💊 Pharmacist: pharmacist@globemed.lk / pharmacist123",
            "💰 Accountant: accountant@globemed.lk / accountant123"
        };
        
        for (String credential : credentials) {
            JLabel credLabel = new JLabel(credential);
            credLabel.setFont(new Font("Courier New", Font.PLAIN, 11));
            credLabel.setForeground(new Color(52, 73, 94));
            contentPanel.add(credLabel);
            contentPanel.add(Box.createVerticalStrut(5));
        }
        
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Note
        JLabel noteLabel = new JLabel("<html><i>Note: This system demonstrates advanced Object-Oriented Design Patterns<br/>" +
                "including Composite, Bridge, Builder, Chain of Responsibility, Flyweight,<br/>" +
                "Interpreter, Mediator, Prototype, Decorator, Visitor, and Memento patterns.</i></html>");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        noteLabel.setForeground(new Color(127, 140, 141));
        contentPanel.add(noteLabel);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton continueButton = new JButton("Continue to Login");
        continueButton.setPreferredSize(new Dimension(150, 35));
        continueButton.setBackground(new Color(41, 128, 185));
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(continueButton);
        
        // Add panels to dialog
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}