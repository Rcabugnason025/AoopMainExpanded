package service;

import model.Employee;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Simple Payslip Service without JasperReports dependencies
 * Generates text-based payslips for the system
 */
public class JasperPayslipService {
    private static final Logger LOGGER = Logger.getLogger(JasperPayslipService.class.getName());
    
    public JasperPayslipService() {
        LOGGER.info("Simple Payslip Service initialized");
    }
    
    /**
     * Generate simple text payslip
     */
    public String generateTextPayslip(int employeeId, LocalDate periodStart, LocalDate periodEnd) {
        // Create a sample employee for demonstration
        Employee employee = createSampleEmployee(employeeId);
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("=".repeat(60)).append("\n");
        sb.append("                    MOTORPH PAYSLIP                     \n");
        sb.append("=".repeat(60)).append("\n");
        sb.append(String.format("Employee ID: %d\n", employee.getEmployeeId()));
        sb.append(String.format("Name: %s\n", employee.getFullName()));
        sb.append(String.format("Position: %s\n", employee.getPosition()));
        sb.append(String.format("Period: %s to %s\n", periodStart, periodEnd));
        sb.append("-".repeat(60)).append("\n");
        sb.append("EARNINGS:\n");
        sb.append(String.format("  Basic Salary:        ₱%,.2f\n", employee.getBasicSalary()));
        sb.append(String.format("  Allowances:          ₱%,.2f\n", employee.calculateAllowances()));
        sb.append(String.format("  GROSS PAY:           ₱%,.2f\n", employee.calculateGrossPay(22, 0)));
        sb.append("-".repeat(60)).append("\n");
        sb.append("DEDUCTIONS:\n");
        sb.append(String.format("  Total Deductions:    ₱%,.2f\n", employee.calculateDeductions()));
        sb.append("=".repeat(60)).append("\n");
        sb.append(String.format("  NET PAY:             ₱%,.2f\n", employee.calculateNetPay(22, 0)));
        sb.append("=".repeat(60)).append("\n");
        sb.append("Generated: " + LocalDate.now()).append("\n");
        sb.append("This is a computer-generated payslip.\n");
        
        return sb.toString();
    }
    
    /**
     * Generate payslip and save to file
     */
    public File generatePayslipToFile(int employeeId, LocalDate periodStart, LocalDate periodEnd, String outputDir) throws IOException {
        String payslipText = generateTextPayslip(employeeId, periodStart, periodEnd);
        
        // Create output directory if it doesn't exist
        File directory = new File(outputDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // Create filename
        String filename = String.format("Payslip_%d_%s.txt", employeeId, periodStart.toString().replace("-", "_"));
        File outputFile = new File(directory, filename);
        
        // Write to file
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(payslipText);
        }
        
        return outputFile;
    }
    
    /**
     * Create a sample employee for demonstration
     */
    private Employee createSampleEmployee(int employeeId) {
        // Create different employee types based on ID
        if (employeeId % 2 == 0) {
            return new model.ContractualEmployee(employeeId, "Sample", "Contractor", "Consultant", 45000, "2025-12-31", "Project X");
        } else {
            return new model.RegularEmployee(employeeId, "Sample", "Employee", "Developer", 50000);
        }
    }
}