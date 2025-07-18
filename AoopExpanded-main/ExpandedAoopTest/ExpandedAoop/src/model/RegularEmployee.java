package model;

/**
 * RegularEmployee class - CREATE THIS FILE: src/model/RegularEmployee.java
 */
public class RegularEmployee extends Employee {
    
    public RegularEmployee() {
        super();
    }
    
    public RegularEmployee(int employeeId, String lastName, String firstName, 
                          String birthday, String address, String phoneNumber, 
                          String sssNo, String philhealthNo, String tinNo, 
                          String pagibigNo, String status, String position, 
                          String immediateSupervisor, double basicSalary, 
                          double riceSubsidy, double phoneAllowance, 
                          double clothingAllowance, double grossSemiMonthlyRate, 
                          double hourlyRate) {
        super(employeeId, lastName, firstName, birthday, address, phoneNumber, 
              sssNo, philhealthNo, tinNo, pagibigNo, status, position, 
              immediateSupervisor, basicSalary, riceSubsidy, phoneAllowance, 
              clothingAllowance, grossSemiMonthlyRate, hourlyRate);
    }
    
    @Override
    public double calculateNetPay(int daysWorked, double overtimeHours) {
        double grossPay = getGrossSemiMonthlyRate();
        double overtimePay = overtimeHours * getHourlyRate() * 1.25; // 125% for overtime
        double totalGross = grossPay + overtimePay + getRiceSubsidy() + getPhoneAllowance() + getClothingAllowance();
        
        // Calculate deductions
        double sssDeduction = calculateSSS(totalGross);
        double philhealthDeduction = calculatePhilHealth(totalGross);
        double pagibigDeduction = calculatePagibig(totalGross);
        double taxDeduction = calculateTax(totalGross);
        
        double totalDeductions = sssDeduction + philhealthDeduction + pagibigDeduction + taxDeduction;
        
        return totalGross - totalDeductions;
    }
    
    @Override
    public String getEmployeeType() {
        return "Regular";
    }
    
    private double calculateSSS(double grossPay) {
        if (grossPay <= 3250) return 135.0;
        if (grossPay <= 3750) return 157.5;
        if (grossPay <= 4250) return 180.0;
        if (grossPay <= 4750) return 202.5;
        if (grossPay <= 5250) return 225.0;
        if (grossPay <= 5750) return 247.5;
        if (grossPay <= 6250) return 270.0;
        if (grossPay <= 6750) return 292.5;
        if (grossPay <= 7250) return 315.0;
        if (grossPay <= 7750) return 337.5;
        if (grossPay <= 8250) return 360.0;
        if (grossPay <= 8750) return 382.5;
        if (grossPay <= 9250) return 405.0;
        if (grossPay <= 9750) return 427.5;
        if (grossPay <= 10250) return 450.0;
        if (grossPay <= 10750) return 472.5;
        if (grossPay <= 11250) return 495.0;
        if (grossPay <= 11750) return 517.5;
        if (grossPay <= 12250) return 540.0;
        if (grossPay <= 12750) return 562.5;
        if (grossPay <= 13250) return 585.0;
        if (grossPay <= 13750) return 607.5;
        if (grossPay <= 14250) return 630.0;
        if (grossPay <= 14750) return 652.5;
        if (grossPay <= 15250) return 675.0;
        if (grossPay <= 15750) return 697.5;
        if (grossPay <= 16250) return 720.0;
        if (grossPay <= 16750) return 742.5;
        if (grossPay <= 17250) return 765.0;
        if (grossPay <= 17750) return 787.5;
        if (grossPay <= 18250) return 810.0;
        if (grossPay <= 18750) return 832.5;
        if (grossPay <= 19250) return 855.0;
        if (grossPay <= 19750) return 877.5;
        return 900.0;
    }
    
    private double calculatePhilHealth(double grossPay) {
        double monthlyBasicSalary = grossPay * 2;
        if (monthlyBasicSalary <= 10000) {
            return 137.5;
        } else if (monthlyBasicSalary >= 60000) {
            return 1375.0;
        } else {
            return monthlyBasicSalary * 0.0275 / 2;
        }
    }
    
    private double calculatePagibig(double grossPay) {
        double monthlyBasicSalary = grossPay * 2;
        if (monthlyBasicSalary <= 1500) {
            return monthlyBasicSalary * 0.01 / 2;
        } else {
            double contribution = monthlyBasicSalary * 0.02 / 2;
            return Math.min(contribution, 100.0);
        }
    }
    
    private double calculateTax(double grossPay) {
        double monthlyTaxableIncome = grossPay * 2;
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
}