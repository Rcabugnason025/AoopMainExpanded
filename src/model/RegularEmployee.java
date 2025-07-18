package model;

// Concrete class showing INHERITANCE and POLYMORPHISM
public class RegularEmployee extends Employee {
    private static final double REGULAR_RICE_SUBSIDY = 1500.0;
    private static final double REGULAR_PHONE_ALLOWANCE = 2000.0;
    private static final double REGULAR_CLOTHING_ALLOWANCE = 1000.0;
    
    public RegularEmployee() {
        super();
        this.riceSubsidy = REGULAR_RICE_SUBSIDY;
        this.phoneAllowance = REGULAR_PHONE_ALLOWANCE;
        this.clothingAllowance = REGULAR_CLOTHING_ALLOWANCE;
    }
    
    public RegularEmployee(int employeeId, String firstName, String lastName,
                          String position, double basicSalary) {
        super(employeeId, firstName, lastName, position, basicSalary);
        this.riceSubsidy = REGULAR_RICE_SUBSIDY;
        this.phoneAllowance = REGULAR_PHONE_ALLOWANCE;
        this.clothingAllowance = REGULAR_CLOTHING_ALLOWANCE;
    }
    
    // Different implementation for regular employees - POLYMORPHISM
    @Override
    public double calculateGrossPay(int daysWorked, double overtimeHours) {
        double dailyRate = basicSalary / 22;
        double regularPay = dailyRate * daysWorked;
        double overtimePay = (dailyRate / 8) * overtimeHours * 1.25; // 125% for overtime
        return regularPay + overtimePay;
    }
    
    @Override
    public double calculateDeductions() {
        // Regular employees have full deductions
        double sss = calculateSSS();
        double philHealth = calculatePhilHealth();
        double pagibig = calculatePagibig();
        double tax = calculateTax();
        return sss + philHealth + pagibig + tax;
    }
    
    @Override
    public double calculateAllowances() {
        return riceSubsidy + phoneAllowance + clothingAllowance;
    }
    
    @Override
    public boolean isEligibleForBenefits() {
        return true; // Regular employees are eligible for all benefits
    }
    
    @Override
    public String getEmployeeType() {
        return "Regular Employee";
    }
    
    // Private helper methods - ENCAPSULATION
    private double calculateSSS() {
        double monthlyBasicSalary = basicSalary;
        if (monthlyBasicSalary <= 3250) return 135.0;
        if (monthlyBasicSalary <= 3750) return 157.5;
        if (monthlyBasicSalary <= 4250) return 180.0;
        if (monthlyBasicSalary <= 4750) return 202.5;
        if (monthlyBasicSalary <= 5250) return 225.0;
        if (monthlyBasicSalary <= 5750) return 247.5;
        if (monthlyBasicSalary <= 6250) return 270.0;
        if (monthlyBasicSalary <= 6750) return 292.5;
        if (monthlyBasicSalary <= 7250) return 315.0;
        if (monthlyBasicSalary <= 7750) return 337.5;
        if (monthlyBasicSalary <= 8250) return 360.0;
        if (monthlyBasicSalary <= 8750) return 382.5;
        if (monthlyBasicSalary <= 9250) return 405.0;
        if (monthlyBasicSalary <= 9750) return 427.5;
        if (monthlyBasicSalary <= 10250) return 450.0;
        if (monthlyBasicSalary <= 10750) return 472.5;
        if (monthlyBasicSalary <= 11250) return 495.0;
        if (monthlyBasicSalary <= 11750) return 517.5;
        if (monthlyBasicSalary <= 12250) return 540.0;
        if (monthlyBasicSalary <= 12750) return 562.5;
        if (monthlyBasicSalary <= 13250) return 585.0;
        if (monthlyBasicSalary <= 13750) return 607.5;
        if (monthlyBasicSalary <= 14250) return 630.0;
        if (monthlyBasicSalary <= 14750) return 652.5;
        if (monthlyBasicSalary <= 15250) return 675.0;
        if (monthlyBasicSalary <= 15750) return 697.5;
        if (monthlyBasicSalary <= 16250) return 720.0;
        if (monthlyBasicSalary <= 16750) return 742.5;
        if (monthlyBasicSalary <= 17250) return 765.0;
        if (monthlyBasicSalary <= 17750) return 787.5;
        if (monthlyBasicSalary <= 18250) return 810.0;
        if (monthlyBasicSalary <= 18750) return 832.5;
        if (monthlyBasicSalary <= 19250) return 855.0;
        if (monthlyBasicSalary <= 19750) return 877.5;
        return 900.0;
    }
    
    private double calculatePhilHealth() {
        double monthlyBasicSalary = basicSalary;
        if (monthlyBasicSalary <= 10000) {
            return 137.5;
        } else if (monthlyBasicSalary >= 60000) {
            return 1375.0;
        } else {
            return monthlyBasicSalary * 0.0275 / 2;
        }
    }
    
    private double calculatePagibig() {
        double monthlyBasicSalary = basicSalary;
        if (monthlyBasicSalary <= 1500) {
            return monthlyBasicSalary * 0.01 / 2;
        } else {
            double contribution = monthlyBasicSalary * 0.02 / 2;
            return Math.min(contribution, 100.0);
        }
    }
    
    private double calculateTax() {
        double monthlyTaxableIncome = basicSalary;
        double annualTaxableIncome = monthlyTaxableIncome * 12;
        double annualTax = 0;
        
        if (annualTaxableIncome <= 250000) {
            annualTax = 0;
        } else if (annualTaxableIncome <= 400000) {
            annualTax = (annualTaxableIncome - 250000) * 0.20;
        } else if (annualTaxableIncome <= 800000) {
            annualTax = 30000 + (annualTaxableIncome - 400000) * 0.25;
        } else if (annualTaxableIncome <= 2000000) {
            annualTax = 130000 + (annualTaxableIncome - 800000) * 0.30;
        } else if (annualTaxableIncome <= 8000000) {
            annualTax = 490000 + (annualTaxableIncome - 2000000) * 0.32;
        } else {
            annualTax = 2410000 + (annualTaxableIncome - 8000000) * 0.35;
        }
        
        return annualTax / 24;
    }
    
    @Override
    public String toString() {
        return "RegularEmployee{" +
                "employeeId=" + employeeId +
                ", name='" + getFullName() + '\'' +
                ", position='" + position + '\'' +
                ", basicSalary=" + basicSalary +
                ", allowances=" + calculateAllowances() +
                '}';
    }
}