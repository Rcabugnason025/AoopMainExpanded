package test;

import model.*;
/**
 * Simple OOP test without JUnit dependencies
 */
public class EmployeeOOPTest {
    
    private RegularEmployee regularEmployee;
    private ContractualEmployee contractualEmployee;
    
    public static void main(String[] args) {
        EmployeeOOPTest test = new EmployeeOOPTest();
        test.runAllTests();
    }
    
    public void runAllTests() {
        setUp();
        
        System.out.println("=".repeat(60));
        System.out.println("    EMPLOYEE OOP PRINCIPLES TEST");
        System.out.println("=".repeat(60));
        
        int passed = 0;
        int total = 0;
        
        total++; if (testInheritance()) { passed++; System.out.println("✅ PASS: Inheritance Test"); } else { System.out.println("❌ FAIL: Inheritance Test"); }
        total++; if (testPolymorphism()) { passed++; System.out.println("✅ PASS: Polymorphism Test"); } else { System.out.println("❌ FAIL: Polymorphism Test"); }
        total++; if (testAbstraction()) { passed++; System.out.println("✅ PASS: Abstraction Test"); } else { System.out.println("❌ FAIL: Abstraction Test"); }
        total++; if (testEncapsulation()) { passed++; System.out.println("✅ PASS: Encapsulation Test"); } else { System.out.println("❌ FAIL: Encapsulation Test"); }
        
        System.out.println("\nResults: " + passed + "/" + total + " tests passed");
        if (passed == total) {
            System.out.println("🎉 ALL OOP TESTS PASSED!");
        }
    }
    
    private void setUp() {
        regularEmployee = new RegularEmployee(10001, "John", "Doe", "Software Developer", 50000);
        contractualEmployee = new ContractualEmployee(10002, "Jane", "Smith", "Consultant", 60000, "2025-12-31", "Project Alpha");
    }
    
    private boolean testInheritance() {
        try {
            return (regularEmployee instanceof Employee) && 
                   (contractualEmployee instanceof Employee) &&
                   (regularEmployee instanceof BaseEntity) &&
                   (contractualEmployee instanceof BaseEntity) &&
                   "John Doe".equals(regularEmployee.getFullName()) &&
                   "Jane Smith".equals(contractualEmployee.getFullName());
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean testPolymorphism() {
        try {
            double regularGrossPay = regularEmployee.calculateGrossPay(22, 8);
            double contractualGrossPay = contractualEmployee.calculateGrossPay(22, 8);
            double regularAllowances = regularEmployee.calculateAllowances();
            double contractualAllowances = contractualEmployee.calculateAllowances();
            
            return (regularGrossPay > 50000) && // Regular gets overtime
                   (contractualGrossPay <= 60000) && // Contractual no overtime
                   (regularAllowances > 0) && // Regular gets allowances
                   (contractualAllowances == 0) && // Contractual no allowances
                   !regularEmployee.getEmployeeType().equals(contractualEmployee.getEmployeeType());
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean testAbstraction() {
        try {
            double grossPay = regularEmployee.calculateGrossPay(22, 0);
            double deductions = regularEmployee.calculateDeductions();
            double allowances = regularEmployee.calculateAllowances();
            boolean benefits = regularEmployee.isEligibleForBenefits();
            double netPay = regularEmployee.calculateNetPay(22, 0);
            double expectedNetPay = grossPay + allowances - deductions;
            
            return (grossPay > 0) && (deductions > 0) && (allowances > 0) && 
                   benefits && (Math.abs(netPay - expectedNetPay) < 0.01);
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean testEncapsulation() {
        try {
            int originalId = regularEmployee.getEmployeeId();
            regularEmployee.setEmployeeId(4002);
            boolean test1 = regularEmployee.getEmployeeId() == 4002;
            
            boolean test2 = false;
            try {
                regularEmployee.setEmployeeId(-1);
            } catch (IllegalArgumentException e) {
                test2 = true;
            }
            
            boolean test3 = regularEmployee.getFirstName().equals("John");
            regularEmployee.setFirstName("Wonder");
            boolean test4 = regularEmployee.getFirstName().equals("Wonder");
            
            boolean test5 = false;
            try {
                regularEmployee.setFirstName("");
            } catch (IllegalArgumentException e) {
                test5 = true;
            }
            
            return test1 && test2 && test3 && test4 && test5;
        } catch (Exception e) {
            return false;
        }
    }
}