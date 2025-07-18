package model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * PayslipData.java - REPLACE your existing PayslipData.java with this entire file
 */
public class PayslipData {
    private Employee employee;
    private int daysWorked;
    private double overtimeHours;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private double basicSalary;
    private double overtimePay;
    private double grossPay;
    private double netPay;
    private double sssDeduction;
    private double philhealthDeduction;
    private double pagibigDeduction;
    private double taxDeduction;
    private double totalDeductions;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double totalAllowances;
    
    // DEFAULT CONSTRUCTOR - FIXES THE CONSTRUCTOR ERROR
    public PayslipData() {
        this.daysWorked = 0;
        this.overtimeHours = 0.0;
        this.basicSalary = 0.0;
        this.overtimePay = 0.0;
        this.grossPay = 0.0;
        this.netPay = 0.0;
        this.sssDeduction = 0.0;
        this.philhealthDeduction = 0.0;
        this.pagibigDeduction = 0.0;
        this.taxDeduction = 0.0;
        this.totalDeductions = 0.0;
        this.riceSubsidy = 0.0;
        this.phoneAllowance = 0.0;
        this.clothingAllowance = 0.0;
        this.totalAllowances = 0.0;
    }
    
    // PARAMETERIZED CONSTRUCTOR - FIXES THE CONSTRUCTOR ERROR
    public PayslipData(Employee employee, int daysWorked, double overtimeHours) {
        this();
        this.employee = employee;
        this.daysWorked = daysWorked;
        this.overtimeHours = overtimeHours;
        
        if (employee != null) {
            calculatePayslipData();
        }
    }
    
    // CALCULATE ALL PAYSLIP DATA
    private void calculatePayslipData() {
        if (employee == null) return;
        
        this.basicSalary = employee.getGrossSemiMonthlyRate();
        this.overtimePay = overtimeHours * employee.getHourlyRate() * 1.25;
        this.riceSubsidy = employee.getRiceSubsidy();
        this.phoneAllowance = employee.getPhoneAllowance();
        this.clothingAllowance = employee.getClothingAllowance();
        this.totalAllowances = riceSubsidy + phoneAllowance + clothingAllowance;
        
        this.grossPay = basicSalary + overtimePay + totalAllowances;
        
        this.sssDeduction = calculateSSS(grossPay);
        this.philhealthDeduction = calculatePhilHealth(grossPay);
        this.pagibigDeduction = calculatePagibig(grossPay);
        this.taxDeduction = calculateTax(grossPay);
        this.totalDeductions = sssDeduction + philhealthDeduction + pagibigDeduction + taxDeduction;
        
        this.netPay = grossPay - totalDeductions;
    }
    
    // DATE CONVERSION METHODS - FIXES THE DATE ERROR
    public void setPeriodStartDate(Date date) {
        if (date != null) {
            this.periodStartDate = date.toInstant()
                                     .atZone(ZoneId.systemDefault())
                                     .toLocalDate();
        }
    }
    
    public void setPeriodEndDate(Date date) {
        if (date != null) {
            this.periodEndDate = date.toInstant()
                                   .atZone(ZoneId.systemDefault())
                                   .toLocalDate();
        }
    }
    
    // GETTERS AND SETTERS
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { 
        this.employee = employee; 
        if (employee != null) calculatePayslipData();
    }
    
    public int getDaysWorked() { return daysWorked; }
    public void setDaysWorked(int daysWorked) { 
        this.daysWorked = daysWorked; 
        calculatePayslipData();
    }
    
    public double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(double overtimeHours) { 
        this.overtimeHours = overtimeHours; 
        calculatePayslipData();
    }
    
    public LocalDate getPeriodStartDate() { return periodStartDate; }
    public void setPeriodStartDate(LocalDate periodStartDate) { this.periodStartDate = periodStartDate; }
    
    public LocalDate getPeriodEndDate() { return periodEndDate; }
    public void setPeriodEndDate(LocalDate periodEndDate) { this.periodEndDate = periodEndDate; }
    
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    
    public double getOvertimePay() { return overtimePay; }
    public void setOvertimePay(double overtimePay) { this.overtimePay = overtimePay; }
    
    public double getGrossPay() { return grossPay; }
    public void setGrossPay(double grossPay) { this.grossPay = grossPay; }
    
    public double getNetPay() { return netPay; }
    public void setNetPay(double netPay) { this.netPay = netPay; }
    
    public double getSssDeduction() { return sssDeduction; }
    public void setSssDeduction(double sssDeduction) { this.sssDeduction = sssDeduction; }
    
    public double getPhilhealthDeduction() { return philhealthDeduction; }
    public void setPhilhealthDeduction(double philhealthDeduction) { this.philhealthDeduction = philhealthDeduction; }
    
    public double getPagibigDeduction() { return pagibigDeduction; }
    public void setPagibigDeduction(double pagibigDeduction) { this.pagibigDeduction = pagibigDeduction; }
    
    public double getTaxDeduction() { return taxDeduction; }
    public void setTaxDeduction(double taxDeduction) { this.taxDeduction = taxDeduction; }
    
    public double getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(double totalDeductions) { this.totalDeductions = totalDeductions; }
    
    public double getRiceSubsidy() { return riceSubsidy; }
    public void setRiceSubsidy(double riceSubsidy) { this.riceSubsidy = riceSubsidy; }
    
    public double getPhoneAllowance() { return phoneAllowance; }
    public void setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = phoneAllowance; }
    
    public double getClothingAllowance() { return clothingAllowance; }
    public void setClothingAllowance(double clothingAllowance) { this.clothingAllowance = clothingAllowance; }
    
    public double getTotalAllowances() { return totalAllowances; }
    public void setTotalAllowances(double totalAllowances) { this.totalAllowances = totalAllowances; }
    
    // CALCULATION HELPER METHODS
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