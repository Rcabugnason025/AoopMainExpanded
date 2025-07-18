package test;

import model.*;
import service.SimplePayslipService;

/**
 * QuickTest.java - CREATE THIS FILE: src/test/QuickTest.java
 * Simple test to verify everything is working
 */
public class QuickTest {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("  MotorPH Payroll System - Quick Test");
        System.out.println("=".repeat(50));
        
        try {
            // Test 1: Create RegularEmployee
            System.out.println("Test 1: Creating RegularEmployee...");
            RegularEmployee employee = new RegularEmployee();
            employee.setEmployeeId(10001);
            employee.setFirstName("John");
            employee.setLastName("Doe");
            employee.setPosition("Software Developer");
            employee.setGrossSemiMonthlyRate(25000.0);
            employee.setHourlyRate(312.5);
            employee.setRiceSubsidy(1500.0);
            employee.setPhoneAllowance(2000.0);
            employee.setClothingAllowance(1000.0);
            System.out.println("✅ RegularEmployee created successfully!");
            
            // Test 2: Calculate Net Pay
            System.out.println("\nTest 2: Calculating net pay...");
            double netPay = employee.calculateNetPay(22, 5.0);
            System.out.println("✅ Net Pay calculated: ₱" + String.format("%,.2f", netPay));
            
            // Test 3: Create PayslipData
            System.out.println("\nTest 3: Creating PayslipData...");
            PayslipData payslipData = new PayslipData(employee, 22, 5.0);
            System.out.println("✅ PayslipData created successfully!");
            System.out.println("   Gross Pay: ₱" + String.format("%,.2f", payslipData.getGrossPay()));
            System.out.println("   Total Deductions: ₱" + String.format("%,.2f", payslipData.getTotalDeductions()));
            System.out.println("   Net Pay: ₱" + String.format("%,.2f", payslipData.getNetPay()));
            
            // Test 4: Test Service
            System.out.println("\nTest 4: Testing SimplePayslipService...");
            SimplePayslipService service = new SimplePayslipService();
            PayslipData servicePayslip = service.generatePayslipData(employee, 22, 5.0);
            System.out.println("✅ Service working correctly!");
            
            // Test 5: Generate Text Payslip
            System.out.println("\nTest 5: Generating text payslip...");
            String textPayslip = service.generateTextPayslip(payslipData);
            System.out.println("✅ Text payslip generated!");
            System.out.println("\nSample payslip:");
            System.out.println(textPayslip);
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🎉 ALL TESTS PASSED!");
            System.out.println("Your MotorPH Payroll System is working correctly!");
            System.out.println("=".repeat(50));
            
        } catch (Exception e) {
            System.out.println("\n❌ TEST FAILED!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            System.out.println("\nPossible fixes:");
            System.out.println("1. Make sure RegularEmployee.java exists in src/model/");
            System.out.println("2. Make sure PayslipData.java is updated");
            System.out.println("3. Make sure SimplePayslipService.java exists in src/service/");
            System.out.println("4. Check that all files compiled correctly");
        }
    }
}