package service;

import model.PayslipData;
import model.Employee;
import java.util.*;
import java.io.*;
import java.time.LocalDate;

/**
 * SimplePayslipService.java - CREATE THIS FILE: src/service/SimplePayslipService.java
 * Simple service without Spring or JasperReports dependencies
 */
public class SimplePayslipService {
    
    /**
     * Generate payslip data for single employee
     */
    public PayslipData generatePayslipData(Employee employee, int daysWorked, double overtimeHours) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        
        return new PayslipData(employee, daysWorked, overtimeHours);
    }
    
    /**
     * Generate batch payslip data for multiple employees
     */
    public List<PayslipData> generateBatchPayslipData(List<Employee> employees, 
                                                     Map<Integer, Integer> daysWorkedMap,
                                                     Map<Integer, Double> overtimeHoursMap) {
        List<PayslipData> payslipDataList = new ArrayList<>();
        
        for (Employee employee : employees) {
            int daysWorked = daysWorkedMap.getOrDefault(employee.getEmployeeId(), 22);
            double overtimeHours = overtimeHoursMap.getOrDefault(employee.getEmployeeId(), 0.0);
            
            PayslipData payslipData = new PayslipData(employee, daysWorked, overtimeHours);
            payslipDataList.add(payslipData);
        }
        
        return payslipDataList;
    }
    
    /**
     * Generate simple text payslip (replacement for PDF when JasperReports not available)
     */
    public String generateTextPayslip(PayslipData payslipData) {
        if (payslipData == null || payslipData.getEmployee() == null) {
            return "Invalid payslip data";
        }
        
        Employee emp = payslipData.getEmployee();
        StringBuilder sb = new StringBuilder();
        
        sb.append("=".repeat(60)).append("\n");
        sb.append("                    MOTORPH PAYSLIP                     \n");
        sb.append("=".repeat(60)).append("\n");
        sb.append(String.format("Employee ID: %d\n", emp.getEmployeeId()));
        sb.append(String.format("Name: %s %s\n", emp.getFirstName(), emp.getLastName()));
        sb.append(String.format("Position: %s\n", emp.getPosition()));
        sb.append(String.format("Days Worked: %d\n", payslipData.getDaysWorked()));
        sb.append(String.format("Overtime Hours: %.1f\n", payslipData.getOvertimeHours()));
        sb.append("-".repeat(60)).append("\n");
        sb.append("EARNINGS:\n");
        sb.append(String.format("  Basic Salary:        ₱%,.2f\n", payslipData.getBasicSalary()));
        sb.append(String.format("  Overtime Pay:        ₱%,.2f\n", payslipData.getOvertimePay()));
        sb.append(String.format("  Rice Subsidy:        ₱%,.2f\n", payslipData.getRiceSubsidy()));
        sb.append(String.format("  Phone Allowance:     ₱%,.2f\n", payslipData.getPhoneAllowance()));
        sb.append(String.format("  Clothing Allowance:  ₱%,.2f\n", payslipData.getClothingAllowance()));
        sb.append(String.format("  GROSS PAY:           ₱%,.2f\n", payslipData.getGrossPay()));
        sb.append("-".repeat(60)).append("\n");
        sb.append("DEDUCTIONS:\n");
        sb.append(String.format("  SSS Contribution:    ₱%,.2f\n", payslipData.getSssDeduction()));
        sb.append(String.format("  PhilHealth:          ₱%,.2f\n", payslipData.getPhilhealthDeduction()));
        sb.append(String.format("  Pag-IBIG:            ₱%,.2f\n", payslipData.getPagibigDeduction()));
        sb.append(String.format("  Withholding Tax:     ₱%,.2f\n", payslipData.getTaxDeduction()));
        sb.append(String.format("  TOTAL DEDUCTIONS:    ₱%,.2f\n", payslipData.getTotalDeductions()));
        sb.append("=".repeat(60)).append("\n");
        sb.append(String.format("  NET PAY:             ₱%,.2f\n", payslipData.getNetPay()));
        sb.append("=".repeat(60)).append("\n");
        sb.append("Generated: " + LocalDate.now()).append("\n");
        sb.append("This is a computer-generated payslip.\n");
        
        return sb.toString();
    }
    
    /**
     * Save text payslip to file
     */
    public void saveTextPayslipToFile(PayslipData payslipData, String filePath) throws IOException {
        String textPayslip = generateTextPayslip(payslipData);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(textPayslip);
        }
    }
    
    /**
     * Generate CSV payslip data
     */
    public String generateCSVPayslip(List<PayslipData> payslipDataList) {
        StringBuilder csv = new StringBuilder();
        
        // CSV Header
        csv.append("Employee ID,Name,Position,Days Worked,Overtime Hours,")
           .append("Basic Salary,Overtime Pay,Gross Pay,SSS,PhilHealth,Pag-IBIG,Tax,Total Deductions,Net Pay\n");
        
        // CSV Data
        for (PayslipData data : payslipDataList) {
            Employee emp = data.getEmployee();
            csv.append(String.format("%d,\"%s %s\",%s,%d,%.1f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f\n",
                emp.getEmployeeId(),
                emp.getFirstName(), emp.getLastName(),
                emp.getPosition(),
                data.getDaysWorked(),
                data.getOvertimeHours(),
                data.getBasicSalary(),
                data.getOvertimePay(),
                data.getGrossPay(),
                data.getSssDeduction(),
                data.getPhilhealthDeduction(),
                data.getPagibigDeduction(),
                data.getTaxDeduction(),
                data.getTotalDeductions(),
                data.getNetPay()
            ));
        }
        
        return csv.toString();
    }
    
    /**
     * Save CSV payslip to file
     */
    public void saveCSVPayslipToFile(List<PayslipData> payslipDataList, String filePath) throws IOException {
        String csvContent = generateCSVPayslip(payslipDataList);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(csvContent);
        }
    }
    
    /**
     * Generate HTML payslip
     */
    public String generateHTMLPayslip(PayslipData payslipData) {
        if (payslipData == null || payslipData.getEmployee() == null) {
            return "<html><body><h1>Invalid payslip data</h1></body></html>";
        }
        
        Employee emp = payslipData.getEmployee();
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n<html>\n<head>\n")
            .append("<title>MotorPH Payslip</title>\n")
            .append("<style>\n")
            .append("body { font-family: Arial, sans-serif; margin: 20px; }\n")
            .append(".header { text-align: center; background-color: #f0f0f0; padding: 10px; }\n")
            .append(".section { margin: 10px 0; }\n")
            .append(".earnings { background-color: #e6ffe6; padding: 10px; }\n")
            .append(".deductions { background-color: #ffe6e6; padding: 10px; }\n")
            .append(".net-pay { background-color: #e6f3ff; padding: 10px; font-weight: bold; }\n")
            .append("table { width: 100%; border-collapse: collapse; }\n")
            .append("td { padding: 5px; border-bottom: 1px solid #ddd; }\n")
            .append(".amount { text-align: right; }\n")
            .append("</style>\n</head>\n<body>\n")
            .append("<div class='header'>\n")
            .append("<h1>MotorPH</h1>\n")
            .append("<h2>Employee Payslip</h2>\n")
            .append("</div>\n")
            .append("<div class='section'>\n")
            .append("<table>\n")
            .append(String.format("<tr><td>Employee ID:</td><td>%d</td></tr>\n", emp.getEmployeeId()))
            .append(String.format("<tr><td>Name:</td><td>%s %s</td></tr>\n", emp.getFirstName(), emp.getLastName()))
            .append(String.format("<tr><td>Position:</td><td>%s</td></tr>\n", emp.getPosition()))
            .append(String.format("<tr><td>Days Worked:</td><td>%d</td></tr>\n", payslipData.getDaysWorked()))
            .append(String.format("<tr><td>Overtime Hours:</td><td>%.1f</td></tr>\n", payslipData.getOvertimeHours()))
            .append("</table>\n</div>\n")
            .append("<div class='section earnings'>\n")
            .append("<h3>Earnings</h3>\n")
            .append("<table>\n")
            .append(String.format("<tr><td>Basic Salary:</td><td class='amount'>₱%,.2f</td></tr>\n", payslipData.getBasicSalary()))
            .append(String.format("<tr><td>Overtime Pay:</td><td class='amount'>₱%,.2f</td></tr>\n", payslipData.getOvertimePay()))
            .append(String.format("<tr><td>Rice Subsidy:</td><td class='amount'>₱%,.2f</td></tr>\n", payslipData.getRiceSubsidy()))
            .append(String.format("<tr><td>Phone Allowance:</td><td class='amount'>₱%,.2f</td></tr>\n", payslipData.getPhoneAllowance()))
            .append(String.format("<tr><td>Clothing Allowance:</td><td class='amount'>₱%,.2f</td></tr>\n", payslipData.getClothingAllowance()))
            .append(String.format("<tr><td><strong>GROSS PAY:</strong></td><td class='amount'><strong>₱%,.2f</strong></td></tr>\n", payslipData.getGrossPay()))
            .append("</table>\n</div>\n")
            .append("<div class='section deductions'>\n")
            .append("<h3>Deductions</h3>\n")
            .append("<table>\n")
            .append(String.format("<tr><td>SSS Contribution:</td><td class='amount'>₱%,.2f</td></tr>\n", payslipData.getSssDeduction()))
            .append(String.format("<tr><td>PhilHealth:</td><td class='amount'>₱%,.2f</td></tr>\n", payslipData.getPhilhealthDeduction()))
            .append(String.format("<tr><td>Pag-IBIG:</td><td class='amount'>₱%,.2f</td></tr>\n", payslipData.getPagibigDeduction()))
            .append(String.format("<tr><td>Withholding Tax:</td><td class='amount'>₱%,.2f</td></tr>\n", payslipData.getTaxDeduction()))
            .append(String.format("<tr><td><strong>TOTAL DEDUCTIONS:</strong></td><td class='amount'><strong>₱%,.2f</strong></td></tr>\n", payslipData.getTotalDeductions()))
            .append("</table>\n</div>\n")
            .append("<div class='section net-pay'>\n")
            .append("<table>\n")
            .append(String.format("<tr><td><h2>NET PAY:</h2></td><td class='amount'><h2>₱%,.2f</h2></td></tr>\n", payslipData.getNetPay()))
            .append("</table>\n</div>\n")
            .append("<div class='section' style='text-align: center; margin-top: 30px; font-size: 12px;'>\n")
            .append("<p>Generated: " + LocalDate.now() + "</p>\n")
            .append("<p>This is a computer-generated payslip.</p>\n")
            .append("</div>\n")
            .append("</body>\n</html>");
        
        return html.toString();
    }
    
    /**
     * Save HTML payslip to file
     */
    public void saveHTMLPayslipToFile(PayslipData payslipData, String filePath) throws IOException {
        String htmlContent = generateHTMLPayslip(payslipData);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(htmlContent);
        }
    }
}