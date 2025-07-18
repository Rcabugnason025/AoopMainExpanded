package ui;

import model.Employee;
import model.RegularEmployee;
import service.SimplePayslipService;
import ui.LoginForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

/**
 * Simple Employee Dashboard without complex dependencies
 */
public class SimpleEmployeeDashboard extends JFrame {
    private Employee currentUser;
    private SimplePayslipService payslipService;

    public SimpleEmployeeDashboard(Employee user) {
        this.currentUser = user;
        this.payslipService = new SimplePayslipService();

        initializeComponents();
        setupLayout();
        setupEventHandlers();

        setTitle("MotorPH Payroll System - Employee Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        // Will be implemented in setupLayout
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 25, 112));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("MotorPH Payroll System - Employee Portal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();

        // Employee Info
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(welcomeLabel, gbc);

        gbc.gridy++;
        JLabel idLabel = new JLabel("Employee ID: " + currentUser.getEmployeeId());
        idLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        mainPanel.add(idLabel, gbc);

        gbc.gridy++;
        JLabel positionLabel = new JLabel("Position: " + currentUser.getPosition());
        positionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        mainPanel.add(positionLabel, gbc);

        gbc.gridy++;
        JLabel typeLabel = new JLabel("Employee Type: " + currentUser.getEmployeeType());
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        mainPanel.add(typeLabel, gbc);

        gbc.gridy++;
        JLabel salaryLabel = new JLabel("Basic Salary: ₱" + String.format("%.2f", currentUser.getBasicSalary()));
        salaryLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        mainPanel.add(salaryLabel, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton viewPayslipButton = new JButton("View Payslip");
        viewPayslipButton.setPreferredSize(new Dimension(150, 40));
        viewPayslipButton.setBackground(new Color(70, 130, 180));
        viewPayslipButton.setForeground(Color.WHITE);
        viewPayslipButton.setFont(new Font("Arial", Font.BOLD, 14));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(150, 40));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));

        buttonPanel.add(viewPayslipButton);
        buttonPanel.add(logoutButton);

        gbc.gridy++;
        gbc.insets = new Insets(30, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Store buttons for event handlers
        viewPayslipButton.addActionListener(e -> showPayslip());
        logoutButton.addActionListener(e -> logout());
    }

    private void setupEventHandlers() {
        // Event handlers are set up in setupLayout
    }

    private void showPayslip() {
        try {
            LocalDate now = LocalDate.now();
            LocalDate periodStart = now.withDayOfMonth(1);
            LocalDate periodEnd = now.withDayOfMonth(now.lengthOfMonth());

            String payslipText = payslipService.generateTextPayslip(
                currentUser.getEmployeeId(), periodStart, periodEnd);

            // Show in dialog
            JTextArea textArea = new JTextArea(payslipText);
            textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
            textArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Payslip - " + currentUser.getFullName(), 
                                        JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating payslip: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a test user
            Employee testUser = new RegularEmployee(10001, "Test", "Employee", "Software Developer", 50000);
            new SimpleEmployeeDashboard(testUser).setVisible(true);
        });
    }
}