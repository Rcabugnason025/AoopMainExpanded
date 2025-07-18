package ui;

import dao.AttendanceDAO;
import model.Employee;
import model.Attendance;
import model.Payroll;
import service.PayrollCalculator;
import service.JasperPayslipService;
import ui.PayrollDetailsDialog;
import ui.LoginForm;
import ui.LeaveRequestDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Improved Employee Dashboard with better usability and bug fixes
 */
public class ImprovedEmployeeDashboard extends JFrame {
    private Employee currentUser;
    private JTabbedPane tabbedPane;

    // Personal Info Tab
    private JLabel nameLabel, positionLabel, statusLabel, salaryLabel;
    private JLabel phoneLabel, addressLabel, sssLabel, philhealthLabel;

    // Attendance Tab
    private JTable attendanceTable;
    private DefaultTableModel attendanceTableModel;
    private JLabel totalDaysLabel, averageHoursLabel;

    // Payroll Tab
    private JTable payrollTable;
    private DefaultTableModel payrollTableModel;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> yearComboBox;

    // Services
    private AttendanceDAO attendanceDAO;
    private PayrollCalculator payrollCalculator;
    private JasperPayslipService jasperService;

    // Status and loading indicators
    private JLabel statusLabel;
    private JProgressBar loadingBar;

    public ImprovedEmployeeDashboard(Employee user) {
        this.currentUser = user;

        try {
            // Initialize services
            this.attendanceDAO = new AttendanceDAO();
            this.payrollCalculator = new PayrollCalculator();
            
            // Initialize JasperReports service if available
            if (JasperPayslipService.isJasperReportsAvailable()) {
                this.jasperService = new JasperPayslipService();
                System.out.println("✅ JasperReports service initialized successfully");
            } else {
                System.out.println("⚠️ JasperReports not available, using fallback text payslips");
            }

            // Initialize UI components
            initializeComponents();
            setupLayout();
            setupEventHandlers();

            // Load initial data with progress indication
            loadDataWithProgress();

            System.out.println("✅ Improved Employee Dashboard initialized successfully for: " + user.getFullName());

        } catch (Exception e) {
            System.err.println("❌ Employee Dashboard initialization failed: " + e.getMessage());
            e.printStackTrace();
            createErrorInterface(e);
        }

        setTitle("MotorPH Payroll System - Employee Dashboard (Enhanced)");
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void createErrorInterface(Exception error) {
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(220, 53, 69));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("⚠️ Dashboard Initialization Failed", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        errorPanel.setBackground(Color.WHITE);

        String errorMessage = "<html><center>" +
                "<h2>🔧 System Error</h2>" +
                "<p><b>Dashboard initialization failed. Please contact IT support.</b></p>" +
                "<br>" +
                "<p><b>User:</b> " + (currentUser != null ? currentUser.getFullName() : "Unknown") + "</p>" +
                "<p><b>Error:</b> " + error.getClass().getSimpleName() + "</p>" +
                "<p><b>Message:</b> " + error.getMessage() + "</p>" +
                "<br>" +
                "<p><i>You can try logging out and logging back in,<br>" +
                "or contact your system administrator for assistance.</i></p>" +
                "</center></html>";

        JLabel messageLabel = new JLabel(errorMessage, JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton retryButton = new JButton("🔄 Retry");
        JButton logoutButton = new JButton("🚪 Logout");
        JButton exitButton = new JButton("❌ Exit");

        retryButton.setBackground(new Color(40, 167, 69));
        retryButton.setForeground(Color.WHITE);
        retryButton.setFont(new Font("Arial", Font.BOLD, 12));

        logoutButton.setBackground(new Color(108, 117, 125));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));

        exitButton.setBackground(new Color(220, 53, 69));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));

        retryButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new ImprovedEmployeeDashboard(currentUser).setVisible(true);
            });
        });

        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginForm().setVisible(true);
            });
        });

        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit the application?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(1);
            }
        });

        buttonPanel.add(retryButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(exitButton);

        errorPanel.add(messageLabel, BorderLayout.CENTER);
        errorPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(errorPanel, BorderLayout.CENTER);
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();

        // Personal Info Labels
        nameLabel = new JLabel();
        positionLabel = new JLabel();
        statusLabel = new JLabel();
        salaryLabel = new JLabel();
        phoneLabel = new JLabel();
        addressLabel = new JLabel();
        sssLabel = new JLabel();
        philhealthLabel = new JLabel();

        // Attendance Table
        String[] attendanceColumns = {"Date", "Log In", "Log Out", "Work Hours", "Status"};
        attendanceTableModel = new DefaultTableModel(attendanceColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendanceTable = new JTable(attendanceTableModel);
        setupTableStyling(attendanceTable);

        totalDaysLabel = new JLabel();
        averageHoursLabel = new JLabel();

        // Payroll Table
        String[] payrollColumns = {"Period", "Days Worked", "Gross Pay", "Deductions", "Net Pay", "Actions"};
        payrollTableModel = new DefaultTableModel(payrollColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Actions column
            }
        };
        payrollTable = new JTable(payrollTableModel);
        setupTableStyling(payrollTable);

        // Month/Year selectors
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        String[] years = {"2023", "2024", "2025"};
        yearComboBox = new JComboBox<>(years);
        yearComboBox.setSelectedItem("2024");

        // Status and loading components
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);

        loadingBar = new JProgressBar();
        loadingBar.setIndeterminate(true);
        loadingBar.setVisible(false);
        loadingBar.setString("Loading...");
        loadingBar.setStringPainted(true);
    }

    private void setupTableStyling(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header Panel with improved styling
        JPanel headerPanel = createImprovedHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create enhanced tabs
        tabbedPane.addTab("📋 Personal Information", createPersonalInfoTab());
        tabbedPane.addTab("📅 My Attendance", createAttendanceTab());
        tabbedPane.addTab("💰 My Payroll", createPayrollTab());

        // Style the tabbed pane
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);

        add(tabbedPane, BorderLayout.CENTER);

        // Enhanced status bar
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createImprovedHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 112));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Left side - Title and user info
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(25, 25, 112));

        JLabel titleLabel = new JLabel("MotorPH Payroll System - Employee Portal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel userInfoLabel = new JLabel("Welcome, " + currentUser.getFullName() + " | ID: " + currentUser.getEmployeeId());
        userInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userInfoLabel.setForeground(Color.LIGHT_GRAY);

        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(userInfoLabel, BorderLayout.SOUTH);

        // Right side - Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(25, 25, 112));

        JButton refreshButton = createHeaderButton("🔄 Refresh", new Color(40, 167, 69));
        JButton helpButton = createHeaderButton("❓ Help", new Color(108, 117, 125));
        JButton logoutButton = createHeaderButton("🚪 Logout", new Color(220, 53, 69));

        refreshButton.addActionListener(e -> refreshAllData());
        helpButton.addActionListener(e -> showHelpDialog());
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(refreshButton);
        buttonPanel.add(helpButton);
        buttonPanel.add(logoutButton);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JButton createHeaderButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(100, 35));
        return button;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.setBackground(Color.WHITE);

        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftStatus.setBackground(Color.WHITE);
        leftStatus.add(statusLabel);

        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightStatus.setBackground(Color.WHITE);
        rightStatus.add(loadingBar);

        statusPanel.add(leftStatus, BorderLayout.WEST);
        statusPanel.add(rightStatus, BorderLayout.EAST);

        return statusPanel;
    }

    private JPanel createPersonalInfoTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(Color.WHITE);

        // Create main info panel with better layout
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        // Title with improved styling
        JLabel titleLabel = new JLabel("Personal Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(25, 25, 112));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 40, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        infoPanel.add(titleLabel, gbc);

        // Reset grid settings
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Add fields with improved styling
        addImprovedInfoField(infoPanel, gbc, "Full Name:", nameLabel, 1);
        addImprovedInfoField(infoPanel, gbc, "Position:", positionLabel, 2);
        addImprovedInfoField(infoPanel, gbc, "Employment Status:", statusLabel, 3);
        addImprovedInfoField(infoPanel, gbc, "Basic Salary:", salaryLabel, 4);
        addImprovedInfoField(infoPanel, gbc, "Phone Number:", phoneLabel, 5);
        addImprovedInfoField(infoPanel, gbc, "Address:", addressLabel, 6);
        addImprovedInfoField(infoPanel, gbc, "SSS Number:", sssLabel, 7);
        addImprovedInfoField(infoPanel, gbc, "PhilHealth Number:", philhealthLabel, 8);

        // Add allowances panel
        JPanel allowancesPanel = createImprovedAllowancesPanel();

        // Action panel with enhanced buttons
        JPanel actionPanel = createImprovedActionPanel();

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(allowancesPanel, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addImprovedInfoField(JPanel parent, GridBagConstraints gbc, String labelText, JLabel valueLabel, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.insets = new Insets(8, 0, 8, 25);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(70, 70, 70));
        parent.add(label, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(8, 0, 8, 0);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        valueLabel.setForeground(new Color(50, 50, 50));
        parent.add(valueLabel, gbc);
    }

    private JPanel createImprovedAllowancesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2), 
                "Monthly Allowances",
                0, 0, new Font("Arial", Font.BOLD, 18), new Color(70, 130, 180)));
        panel.setBackground(Color.WHITE);

        JPanel allowanceGrid = new JPanel(new GridLayout(2, 2, 25, 15));
        allowanceGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        allowanceGrid.setBackground(Color.WHITE);

        // Enhanced allowance cards
        JPanel ricePanel = createEnhancedAllowanceCard("🍚 Rice Subsidy",
                String.format("₱%.2f", currentUser.getRiceSubsidy()),
                new Color(144, 238, 144), "Monthly rice allowance");

        JPanel phonePanel = createEnhancedAllowanceCard("📱 Phone Allowance",
                String.format("₱%.2f", currentUser.getPhoneAllowance()),
                new Color(173, 216, 230), "Communication allowance");

        JPanel clothingPanel = createEnhancedAllowanceCard("👔 Clothing Allowance",
                String.format("₱%.2f", currentUser.getClothingAllowance()),
                new Color(255, 182, 193), "Uniform allowance");

        double totalAllowances = currentUser.getRiceSubsidy() +
                currentUser.getPhoneAllowance() +
                currentUser.getClothingAllowance();
        JPanel totalPanel = createEnhancedAllowanceCard("💰 Total Allowances",
                String.format("₱%.2f", totalAllowances),
                new Color(255, 215, 0), "Sum of all allowances");

        allowanceGrid.add(ricePanel);
        allowanceGrid.add(phonePanel);
        allowanceGrid.add(clothingPanel);
        allowanceGrid.add(totalPanel);

        panel.add(allowanceGrid, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEnhancedAllowanceCard(String title, String amount, Color bgColor, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.BLACK);

        JLabel amountLabel = new JLabel(amount, JLabel.CENTER);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        amountLabel.setForeground(Color.BLACK);

        JLabel descLabel = new JLabel(description, JLabel.CENTER);
        descLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        descLabel.setForeground(Color.DARK_GRAY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(amountLabel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createImprovedActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        actionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Quick Actions",
                0, 0, new Font("Arial", Font.BOLD, 16)));
        actionPanel.setBackground(Color.WHITE);

        JButton leaveRequestButton = createActionButton("📝 Submit Leave Request", new Color(70, 130, 180));
        JButton viewPayslipButton = createActionButton("📄 View Latest Payslip", new Color(40, 167, 69));
        JButton updateInfoButton = createActionButton("✏️ Update Information", new Color(255, 140, 0));

        leaveRequestButton.addActionListener(e -> showLeaveRequestDialog());
        viewPayslipButton.addActionListener(e -> showLatestPayslip());
        updateInfoButton.addActionListener(e -> showUpdateInfoDialog());

        actionPanel.add(leaveRequestButton);
        actionPanel.add(viewPayslipButton);
        actionPanel.add(updateInfoButton);

        return actionPanel;
    }

    private JButton createActionButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 45));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    private JPanel createAttendanceTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Enhanced summary panel
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "Attendance Summary",
                0, 0, new Font("Arial", Font.BOLD, 16), new Color(70, 130, 180)));
        summaryPanel.setBackground(Color.WHITE);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.add(totalDaysLabel);
        statsPanel.add(averageHoursLabel);

        JButton refreshAttendanceButton = new JButton("🔄 Refresh Attendance");
        refreshAttendanceButton.setBackground(new Color(70, 130, 180));
        refreshAttendanceButton.setForeground(Color.WHITE);
        refreshAttendanceButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshAttendanceButton.addActionListener(e -> loadAttendanceData());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshAttendanceButton);

        summaryPanel.add(statsPanel, BorderLayout.WEST);
        summaryPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPayrollTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Enhanced period selection panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        selectionPanel.setBackground(Color.WHITE);
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Select Payroll Period"));

        selectionPanel.add(new JLabel("Month:"));
        selectionPanel.add(monthComboBox);
        selectionPanel.add(new JLabel("Year:"));
        selectionPanel.add(yearComboBox);

        JButton calculateButton = new JButton("💰 Calculate Payroll");
        calculateButton.setBackground(new Color(40, 167, 69));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 12));
        calculateButton.addActionListener(e -> calculatePayroll());

        JButton exportButton = new JButton("📤 Export Payslip");
        exportButton.setBackground(new Color(255, 140, 0));
        exportButton.setForeground(Color.WHITE);
        exportButton.setFont(new Font("Arial", Font.BOLD, 12));
        exportButton.addActionListener(e -> exportPayslip());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.add(calculateButton);
        actionPanel.add(exportButton);

        topPanel.add(selectionPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(payrollTable), BorderLayout.CENTER);

        return panel;
    }

    private void setupEventHandlers() {
        monthComboBox.addActionListener(e -> loadPayrollData());
        yearComboBox.addActionListener(e -> loadPayrollData());
    }

    private void loadDataWithProgress() {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Loading personal information...");
                loadPersonalInfo();
                Thread.sleep(500);

                publish("Loading attendance data...");
                loadAttendanceData();
                Thread.sleep(500);

                publish("Loading payroll data...");
                loadPayrollData();
                Thread.sleep(500);

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                String latestMessage = chunks.get(chunks.size() - 1);
                statusLabel.setText(latestMessage);
                loadingBar.setVisible(true);
            }

            @Override
            protected void done() {
                loadingBar.setVisible(false);
                statusLabel.setText("Ready - All data loaded successfully");
            }
        };

        worker.execute();
    }

    private void loadPersonalInfo() {
        SwingUtilities.invokeLater(() -> {
            nameLabel.setText(currentUser.getFullName());
            positionLabel.setText(currentUser.getPosition());
            statusLabel.setText(currentUser.getStatus());
            salaryLabel.setText(String.format("₱%.2f", currentUser.getBasicSalary()));
            phoneLabel.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "N/A");
            addressLabel.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "N/A");
            sssLabel.setText(currentUser.getSssNumber() != null ? currentUser.getSssNumber() : "N/A");
            philhealthLabel.setText(currentUser.getPhilhealthNumber() != null ? currentUser.getPhilhealthNumber() : "N/A");
        });
    }

    private void loadAttendanceData() {
        SwingUtilities.invokeLater(() -> {
            attendanceTableModel.setRowCount(0);

            try {
                List<Attendance> attendanceList = attendanceDAO.getAttendanceByEmployeeId(currentUser.getEmployeeId());

                double totalHours = 0;
                int totalDays = attendanceList.size();

                for (Attendance att : attendanceList) {
                    double workHours = att.getWorkHours();
                    totalHours += workHours;

                    String status = "Present";
                    if (att.isLate() && att.hasUndertime()) {
                        status = "Late & Undertime";
                    } else if (att.isLate()) {
                        status = "Late";
                    } else if (att.hasUndertime()) {
                        status = "Undertime";
                    } else if (att.isFullDay()) {
                        status = "Full Day";
                    }

                    Object[] row = {
                            att.getDate(),
                            att.getLogIn() != null ? att.getLogIn() : "N/A",
                            att.getLogOut() != null ? att.getLogOut() : "N/A",
                            String.format("%.2f hrs", workHours),
                            status
                    };
                    attendanceTableModel.addRow(row);
                }

                // Update summary labels
                totalDaysLabel.setText("📊 Total Days: " + totalDays);
                if (totalDays > 0) {
                    double avgHours = totalHours / totalDays;
                    averageHoursLabel.setText(String.format("⏱️ Average Hours: %.2f", avgHours));
                } else {
                    averageHoursLabel.setText("⏱️ Average Hours: 0.00");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading attendance data: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }

    private void loadPayrollData() {
        SwingUtilities.invokeLater(() -> {
            payrollTableModel.setRowCount(0);

            try {
                int selectedMonth = monthComboBox.getSelectedIndex() + 1;
                int selectedYear = Integer.parseInt((String) yearComboBox.getSelectedItem());

                LocalDate periodStart = LocalDate.of(selectedYear, selectedMonth, 1);
                LocalDate periodEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());

                PayrollCalculator.PayrollData payrollData = payrollCalculator.calculatePayroll(currentUser.getEmployeeId(), periodStart, periodEnd);

                Object[] row = {
                        periodStart.format(DateTimeFormatter.ofPattern("MMM yyyy")),
                        payrollData.getDaysWorked(),
                        String.format("₱%.2f", payrollData.getGrossPay()),
                        String.format("₱%.2f", payrollData.getTotalDeductions()),
                        String.format("₱%.2f", payrollData.getNetPay()),
                        "View Payslip"
                };
                payrollTableModel.addRow(row);

            } catch (Exception e) {
                Object[] row = {
                        "Error calculating payroll",
                        "Error",
                        "Error",
                        "Error",
                        "Error",
                        "View Error"
                };
                payrollTableModel.addRow(row);

                JOptionPane.showMessageDialog(this, "Error loading payroll data: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }

    private void showLeaveRequestDialog() {
        try {
            LeaveRequestDialog dialog = new LeaveRequestDialog(this, currentUser);
            dialog.setVisible(true);
            loadAttendanceData(); // Refresh attendance data after potential leave submission
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening leave request dialog: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLatestPayslip() {
        try {
            int selectedMonth = LocalDate.now().getMonthValue();
            int selectedYear = LocalDate.now().getYear();

            LocalDate periodStart = LocalDate.of(selectedYear, selectedMonth, 1);
            LocalDate periodEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());

            PayrollCalculator.PayrollData payrollData = payrollCalculator.calculatePayroll(currentUser.getEmployeeId(), periodStart, periodEnd);
            
            // Convert PayrollData to Payroll for dialog
            Payroll payroll = new Payroll();
            payroll.setEmployeeId(payrollData.getEmployeeId());
            payroll.setPeriodStart(java.sql.Date.valueOf(payrollData.getPeriodStart()));
            payroll.setPeriodEnd(java.sql.Date.valueOf(payrollData.getPeriodEnd()));
            payroll.setMonthlyRate(payrollData.getMonthlyRate());
            payroll.setDaysWorked(payrollData.getDaysWorked());
            payroll.setGrossPay(payrollData.getGrossPay());
            payroll.setTotalDeductions(payrollData.getTotalDeductions());
            payroll.setNetPay(payrollData.getNetPay());
            payroll.setRiceSubsidy(payrollData.getRiceSubsidy());
            payroll.setPhoneAllowance(payrollData.getPhoneAllowance());
            payroll.setClothingAllowance(payrollData.getClothingAllowance());
            payroll.setSss(payrollData.getSss());
            payroll.setPhilhealth(payrollData.getPhilhealth());
            payroll.setPagibig(payrollData.getPagibig());
            payroll.setTax(payrollData.getTax());

            PayrollDetailsDialog dialog = new PayrollDetailsDialog(this, currentUser, payroll);
            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating payslip: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showUpdateInfoDialog() {
        JOptionPane.showMessageDialog(this, 
                "Contact HR to update your personal information.\n\n" +
                "HR Contact: (028) 911-5071\n" +
                "Email: hr@motorph.com",
                "Update Information", 
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void calculatePayroll() {
        try {
            int selectedMonth = monthComboBox.getSelectedIndex() + 1;
            int selectedYear = Integer.parseInt((String) yearComboBox.getSelectedItem());

            LocalDate periodStart = LocalDate.of(selectedYear, selectedMonth, 1);
            LocalDate periodEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());

            PayrollCalculator.PayrollData payrollData = payrollCalculator.calculatePayroll(currentUser.getEmployeeId(), periodStart, periodEnd);
            
            // Convert to Payroll object for dialog
            Payroll payroll = new Payroll();
            payroll.setEmployeeId(payrollData.getEmployeeId());
            payroll.setPeriodStart(java.sql.Date.valueOf(payrollData.getPeriodStart()));
            payroll.setPeriodEnd(java.sql.Date.valueOf(payrollData.getPeriodEnd()));
            payroll.setMonthlyRate(payrollData.getMonthlyRate());
            payroll.setDaysWorked(payrollData.getDaysWorked());
            payroll.setGrossPay(payrollData.getGrossPay());
            payroll.setTotalDeductions(payrollData.getTotalDeductions());
            payroll.setNetPay(payrollData.getNetPay());
            payroll.setRiceSubsidy(payrollData.getRiceSubsidy());
            payroll.setPhoneAllowance(payrollData.getPhoneAllowance());
            payroll.setClothingAllowance(payrollData.getClothingAllowance());
            payroll.setSss(payrollData.getSss());
            payroll.setPhilhealth(payrollData.getPhilhealth());
            payroll.setPagibig(payrollData.getPagibig());
            payroll.setTax(payrollData.getTax());

            PayrollDetailsDialog dialog = new PayrollDetailsDialog(this, currentUser, payroll);
            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error calculating payroll: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void exportPayslip() {
        if (jasperService == null) {
            JOptionPane.showMessageDialog(this, 
                    "JasperReports not available. Please install JasperReports library for PDF export.",
                    "Export Not Available", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int selectedMonth = monthComboBox.getSelectedIndex() + 1;
            int selectedYear = Integer.parseInt((String) yearComboBox.getSelectedItem());

            LocalDate periodStart = LocalDate.of(selectedYear, selectedMonth, 1);
            LocalDate periodEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());

            String outputDir = System.getProperty("user.home") + "/MotorPH_Payslips/";
            
            java.io.File payslipFile = jasperService.generatePayslipToFile(
                    currentUser.getEmployeeId(), 
                    periodStart, 
                    periodEnd, 
                    JasperPayslipService.ExportFormat.PDF, 
                    outputDir);

            int option = JOptionPane.showConfirmDialog(this,
                    "Payslip exported successfully!\n\n" +
                    "File: " + payslipFile.getName() + "\n" +
                    "Location: " + payslipFile.getParent() + "\n\n" +
                    "Do you want to open the file now?",
                    "Export Successful",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(payslipFile);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting payslip: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshAllData() {
        statusLabel.setText("Refreshing all data...");
        loadingBar.setVisible(true);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadPersonalInfo();
                loadAttendanceData();
                loadPayrollData();
                return null;
            }

            @Override
            protected void done() {
                loadingBar.setVisible(false);
                statusLabel.setText("All data refreshed successfully");
            }
        };

        worker.execute();
    }

    private void showHelpDialog() {
        String helpText = "<html><body style='width: 400px; font-family: Arial;'>" +
                "<h2>MotorPH Employee Dashboard Help</h2>" +
                "<h3>Navigation:</h3>" +
                "<ul>" +
                "<li><b>Personal Information:</b> View your employee details and allowances</li>" +
                "<li><b>My Attendance:</b> Track your daily attendance and work hours</li>" +
                "<li><b>My Payroll:</b> Calculate and view your payroll information</li>" +
                "</ul>" +
                "<h3>Quick Actions:</h3>" +
                "<ul>" +
                "<li><b>Submit Leave Request:</b> Apply for various types of leave</li>" +
                "<li><b>View Latest Payslip:</b> See your current month's payroll</li>" +
                "<li><b>Export Payslip:</b> Download PDF payslip using MotorPH template</li>" +
                "</ul>" +
                "<h3>Support:</h3>" +
                "<p>For technical support, contact IT at: it@motorph.com</p>" +
                "<p>For HR inquiries, contact: hr@motorph.com</p>" +
                "</body></html>";

        JOptionPane.showMessageDialog(this, helpText, "Help - Employee Dashboard", JOptionPane.INFORMATION_MESSAGE);
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
            Employee testUser = new RegularEmployee();
            testUser.setEmployeeId(10001);
            testUser.setFirstName("Test");
            testUser.setLastName("Employee");
            testUser.setPosition("Software Developer");
            testUser.setStatus("Regular");
            testUser.setBasicSalary(50000.0);
            testUser.setRiceSubsidy(1500.0);
            testUser.setPhoneAllowance(1000.0);
            testUser.setClothingAllowance(800.0);

            new ImprovedEmployeeDashboard(testUser).setVisible(true);
        });
    }
}