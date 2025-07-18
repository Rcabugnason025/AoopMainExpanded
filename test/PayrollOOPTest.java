package test;

import model.*;

/**
 * Simple payroll test without JUnit dependencies
 */
public class PayrollOOPTest {
    
    public static void main(String[] args) {
        PayrollOOPTest test = new PayrollOOPTest();
        test.runPayrollTests();
    }
    
    public void runPayrollTests() {
        System.out.println("=".repeat(60));
        System.out.println("    PAYROLL CALCULATION OOP TEST");
        System.out.println("=".repeat(60));
        
        int passed = 0;
        int total = 0;
        
        total++; if (testRegularEmployeePayroll()) { passed++; System.out.println("✅ PASS: Regular Employee Payroll"); } else { System.out.println("❌ FAIL: Regular Employee Payroll"); }
        total++; if (testContractualEmployeePayroll()) { passed++; System.out.println("✅ PASS: Contractual Employee Payroll"); } else { System.out.println("❌ FAIL: Contractual Employee Payroll"); }
        total++; if (testPolymorphicBehavior()) { passed++; System.out.println("✅ PASS: Polymorphic Behavior"); } else { System.out.println("❌ FAIL: Polymorphic Behavior"); }
        
        System.out.println("\nResults: " + passed + "/" + total + " tests passed");
        if (passed == total) {
            System.out.println("🎉 ALL PAYROLL TESTS PASSED!");
        }
    }
    
    private boolean testRegularEmployeePayroll() {
        try {
            RegularEmployee emp = new RegularEmployee(1001, "Test", "Regular", "Developer", 50000);
            
            double grossPay = emp.calculateGrossPay(22, 8);
            double allowances = emp.calculateAllowances();
            double deductions = emp.calculateDeductions();
            boolean benefits = emp.isEligibleForBenefits();
            
            return (grossPay > 50000) && // Should include overtime
                   (allowances > 0) && // Should have allowances
                   (deductions > 0) && // Should have deductions
                   benefits; // Should be eligible for benefits
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean testContractualEmployeePayroll() {
        try {
            ContractualEmployee emp = new ContractualEmployee(1002, "Test", "Contractual", "Consultant", 60000, "2025-12-31", "Project X");
            
            double grossPay = emp.calculateGrossPay(22, 8);
            double allowances = emp.calculateAllowances();
            double deductions = emp.calculateDeductions();
            boolean benefits = emp.isEligibleForBenefits();
            
            return (grossPay <= 60000) && // No overtime
                   (allowances == 0) && // No allowances
                   (deductions > 0) && // Some deductions
                   !benefits; // Not eligible for benefits
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean testPolymorphicBehavior() {
        try {
            Employee[] employees = {
                new RegularEmployee(2001, "Alice", "Johnson", "Manager", 70000),
                new ContractualEmployee(2002, "Bob", "Wilson", "Consultant", 80000, "2025-12-31", "Project B")
            };
            
            for (Employee emp : employees) {
                double grossPay = emp.calculateGrossPay(22, 8);
                double allowances = emp.calculateAllowances();
                boolean benefits = emp.isEligibleForBenefits();
                String type = emp.getEmployeeType();
                
                if (grossPay <= 0 || type == null) return false;
                
                if (emp instanceof RegularEmployee) {
                    if (allowances <= 0 || !benefits) return false;
                } else if (emp instanceof ContractualEmployee) {
                    if (allowances != 0 || benefits) return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}