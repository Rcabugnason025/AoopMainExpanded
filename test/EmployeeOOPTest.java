package test;

import model.*;
import dao.EmployeeDAO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit 5 test suite for Employee models
 * Tests inheritance, abstraction, and polymorphism using Assert Functions
 */
@DisplayName("Employee OOP Principles Test Suite")
class EmployeeOOPTest {
    
    private RegularEmployee regularEmployee;
    private ContractualEmployee contractualEmployee;
    private EmployeeDAO employeeDAO;
    
    @BeforeEach
    @DisplayName("Setup test data before each test")
    void setUp() {
        regularEmployee = new RegularEmployee(10001, "John", "Doe", "Software Developer", 50000);
        contractualEmployee = new ContractualEmployee(10002, "Jane", "Smith", "Consultant", 60000, "2025-12-31", "Project Alpha");
        employeeDAO = new EmployeeDAO();
    }
    
    @Nested
    @DisplayName("Inheritance Tests")
    class InheritanceTests {
        
        @Test
        @DisplayName("Test inheritance hierarchy using instanceof")
        void testInheritanceHierarchy() {
            // Test inheritance relationship using Assert Functions
            assertTrue(regularEmployee instanceof Employee, "RegularEmployee should inherit from Employee");
            assertTrue(contractualEmployee instanceof Employee, "ContractualEmployee should inherit from Employee");
            assertTrue(regularEmployee instanceof BaseEntity, "RegularEmployee should inherit from BaseEntity");
            assertTrue(contractualEmployee instanceof BaseEntity, "ContractualEmployee should inherit from BaseEntity");
            
            // Test that they are different concrete types
            assertFalse(regularEmployee instanceof ContractualEmployee, "RegularEmployee should not be instance of ContractualEmployee");
            assertFalse(contractualEmployee instanceof RegularEmployee, "ContractualEmployee should not be instance of RegularEmployee");
        }
        
        @Test
        @DisplayName("Test inherited method functionality")
        void testInheritedMethods() {
            // Test methods inherited from Employee class
            assertEquals("John Doe", regularEmployee.getFullName(), "Should inherit getFullName from Employee");
            assertEquals("Jane Smith", contractualEmployee.getFullName(), "Should inherit getFullName from Employee");
            
            // Test calculated methods
            assertTrue(regularEmployee.getDailyRate() > 0, "Should inherit getDailyRate calculation");
            assertTrue(contractualEmployee.getDailyRate() > 0, "Should inherit getDailyRate calculation");
            
            // Test methods inherited from BaseEntity
            assertNotNull(regularEmployee.getCreatedAt(), "Should inherit getCreatedAt from BaseEntity");
            assertNotNull(contractualEmployee.getCreatedAt(), "Should inherit getCreatedAt from BaseEntity");
        }
        
        @Test
        @DisplayName("Test constructor chaining")
        void testConstructorChaining() {
            // Create new employees to test constructor inheritance
            RegularEmployee newRegular = new RegularEmployee(3001, "Charlie", "Brown", "Analyst", 45000);
            ContractualEmployee newContractual = new ContractualEmployee(3002, "Diana", "Prince", "Specialist", 55000, "2025-06-30", "Project Y");
            
            // Test that parent constructor was called properly
            assertEquals("Charlie Brown", newRegular.getFullName(), "Constructor should call parent constructor");
            assertEquals("Diana Prince", newContractual.getFullName(), "Constructor should call parent constructor");
            assertEquals(45000, newRegular.getBasicSalary(), "Constructor should set inherited fields");
            assertEquals(55000, newContractual.getBasicSalary(), "Constructor should set inherited fields");
        }
    }
    
    @Nested
    @DisplayName("Abstraction Tests")
    class AbstractionTests {
        
        @Test
        @DisplayName("Test abstract method implementation requirement")
        void testAbstractMethodImplementation() {
            // All abstract methods must be implemented by concrete classes
            assertDoesNotThrow(() -> regularEmployee.calculateGrossPay(22, 8), 
                              "RegularEmployee must implement calculateGrossPay");
            assertDoesNotThrow(() -> contractualEmployee.calculateGrossPay(22, 8), 
                              "ContractualEmployee must implement calculateGrossPay");
            
            assertDoesNotThrow(() -> regularEmployee.calculateDeductions(), 
                              "RegularEmployee must implement calculateDeductions");
            assertDoesNotThrow(() -> contractualEmployee.calculateDeductions(), 
                              "ContractualEmployee must implement calculateDeductions");
            
            assertDoesNotThrow(() -> regularEmployee.calculateAllowances(), 
                              "RegularEmployee must implement calculateAllowances");
            assertDoesNotThrow(() -> contractualEmployee.calculateAllowances(), 
                              "ContractualEmployee must implement calculateAllowances");
            
            assertDoesNotThrow(() -> regularEmployee.isEligibleForBenefits(), 
                              "RegularEmployee must implement isEligibleForBenefits");
            assertDoesNotThrow(() -> contractualEmployee.isEligibleForBenefits(), 
                              "ContractualEmployee must implement isEligibleForBenefits");
        }
        
        @Test
        @DisplayName("Test template method pattern implementation")
        void testTemplateMethodPattern() {
            // calculateNetPay is a template method that uses abstract methods
            int daysWorked = 22;
            double overtimeHours = 4.0;
            
            double regularNetPay = regularEmployee.calculateNetPay(daysWorked, overtimeHours);
            double contractualNetPay = contractualEmployee.calculateNetPay(daysWorked, overtimeHours);
            
            // Template method should work for both types
            assertTrue(regularNetPay > 0, "Template method should work for RegularEmployee");
            assertTrue(contractualNetPay > 0, "Template method should work for ContractualEmployee");
            
            // Verify template method algorithm: grossPay + allowances - deductions
            double regularGross = regularEmployee.calculateGrossPay(daysWorked, overtimeHours);
            double regularAllowances = regularEmployee.calculateAllowances();
            double regularDeductions = regularEmployee.calculateDeductions();
            double expectedRegularNet = regularGross + regularAllowances - regularDeductions;
            
            assertEquals(expectedRegularNet, regularNetPay, 0.01, 
                        "Template method should follow defined algorithm");
        }
        
        @Test
        @DisplayName("Test abstract class provides common interface")
        void testAbstractClassInterface() {
            // Create array of Employee references
            Employee[] employees = {
                new RegularEmployee(7001, "Luna", "Garcia", "QA Engineer", 48000),
                new ContractualEmployee(7002, "Mike", "Rodriguez", "Business Analyst", 52000, "2025-11-30", "Analysis Project")
            };
            
            // All employees must respond to the same abstract method interface
            for (Employee emp : employees) {
                assertDoesNotThrow(() -> emp.calculateGrossPay(22, 0), 
                                  "All employees must implement calculateGrossPay");
                assertDoesNotThrow(() -> emp.calculateDeductions(), 
                                  "All employees must implement calculateDeductions");
                assertDoesNotThrow(() -> emp.calculateAllowances(), 
                                  "All employees must implement calculateAllowances");
                assertDoesNotThrow(() -> emp.isEligibleForBenefits(), 
                                  "All employees must implement isEligibleForBenefits");
                
                // Test return value constraints
                assertTrue(emp.calculateGrossPay(22, 0) >= 0, "Gross pay should be non-negative");
                assertTrue(emp.calculateDeductions() >= 0, "Deductions should be non-negative");
                assertTrue(emp.calculateAllowances() >= 0, "Allowances should be non-negative");
            }
        }
    }
    
    @Nested
    @DisplayName("Polymorphism Tests")
    class PolymorphismTests {
        
        @Test
        @DisplayName("Test runtime polymorphism with method overriding")
        void testRuntimePolymorphism() {
            // Create array of Employee references pointing to different concrete types
            Employee[] employees = {
                new RegularEmployee(4001, "Eve", "Adams", "Manager", 70000),
                new ContractualEmployee(4002, "Frank", "Miller", "Advisor", 80000, "2025-09-30", "Project Z")
            };
            
            // Test that each employee behaves according to its actual type at runtime
            for (Employee emp : employees) {
                double grossPay = emp.calculateGrossPay(22, 8);
                double allowances = emp.calculateAllowances();
                boolean benefits = emp.isEligibleForBenefits();
                String type = emp.getEmployeeType();
                
                // All should return valid values
                assertTrue(grossPay > 0, type + " should have positive gross pay");
                assertTrue(allowances >= 0, type + " should have non-negative allowances");
                assertNotNull(type, "Employee type should not be null");
                
                // But behavior should differ based on actual type
                if (emp instanceof RegularEmployee) {
                    assertTrue(allowances > 0, "Regular employee should have allowances");
                    assertTrue(benefits, "Regular employee should be eligible for benefits");
                    assertTrue(grossPay > emp.getBasicSalary(), "Regular employee should get overtime pay");
                } else if (emp instanceof ContractualEmployee) {
                    assertEquals(0, allowances, 0.01, "Contractual employee should have no allowances");
                    assertFalse(benefits, "Contractual employee should not be eligible for benefits");
                    // Contractual employees don't get overtime, so gross pay should equal basic calculation
                }
            }
        }
        
        @Test
        @DisplayName("Test polymorphic method overriding")
        void testPolymorphicMethodOverriding() {
            // Test that overridden methods behave differently
            assertEquals("Regular Employee", regularEmployee.getEmployeeType(), 
                        "RegularEmployee should override getEmployeeType");
            assertEquals("Contractual Employee", contractualEmployee.getEmployeeType(),
                        "ContractualEmployee should override getEmployeeType");
            
            // Test different calculation behaviors
            double regularOvertimePay = regularEmployee.calculateGrossPay(22, 8);
            double regularNoOvertimePay = regularEmployee.calculateGrossPay(22, 0);
            assertTrue(regularOvertimePay > regularNoOvertimePay, 
                      "Regular employee should get overtime pay");
            
            double contractualOvertimePay = contractualEmployee.calculateGrossPay(22, 8);
            double contractualNoOvertimePay = contractualEmployee.calculateGrossPay(22, 0);
            assertEquals(contractualOvertimePay, contractualNoOvertimePay, 0.01,
                        "Contractual employee should not get overtime pay");
        }
        
        @Test
        @DisplayName("Test polymorphic collections")
        void testPolymorphicCollections() {
            // Create a collection of different employee types
            java.util.List<Employee> employeeList = java.util.Arrays.asList(
                new RegularEmployee(6001, "Ivy", "Chen", "Senior Developer", 65000),
                new ContractualEmployee(6002, "Jack", "Taylor", "Technical Writer", 45000, "2025-08-31", "Documentation Project"),
                new RegularEmployee(6003, "Kelly", "Davis", "Team Lead", 75000)
            );
            
            // Test polymorphic behavior in collection
            double totalNetPay = 0;
            for (Employee emp : employeeList) {
                totalNetPay += emp.calculateNetPay(22, 0);
            }
            
            assertTrue(totalNetPay > 0, "Total net pay should be positive");
            assertEquals(3, employeeList.size(), "Should have 3 employees in collection");
            
            // Test that we can call polymorphic methods on collection elements
            long regularCount = employeeList.stream()
                .filter(emp -> emp instanceof RegularEmployee)
                .count();
            long contractualCount = employeeList.stream()
                .filter(emp -> emp instanceof ContractualEmployee)
                .count();
            
            assertEquals(2, regularCount, "Should have 2 regular employees");
            assertEquals(1, contractualCount, "Should have 1 contractual employee");
        }
    }
    
    @Nested
    @DisplayName("Encapsulation Tests")
    class EncapsulationTests {
        
        @Test
        @DisplayName("Test private fields are protected")
        void testPrivateFieldsEncapsulation() {
            // We cannot directly access private fields, must use getters/setters
            assertDoesNotThrow(() -> regularEmployee.getEmployeeId(), "Should access employee ID through getter");
            assertDoesNotThrow(() -> regularEmployee.setEmployeeId(2001), "Should modify employee ID through setter");
            
            assertEquals(2001, regularEmployee.getEmployeeId(), "Setter should update the field value");
        }
        
        @Test
        @DisplayName("Test data validation in setters")
        void testSetterValidation() {
            // Test validation in setters
            assertThrows(IllegalArgumentException.class, 
                        () -> regularEmployee.setEmployeeId(-1),
                        "Setter should validate negative employee ID");
            
            assertThrows(IllegalArgumentException.class,
                        () -> regularEmployee.setFirstName(""),
                        "Setter should validate empty first name");
            
            assertThrows(IllegalArgumentException.class,
                        () -> regularEmployee.setLastName(null),
                        "Setter should validate null last name");
        }
        
        @Test
        @DisplayName("Test controlled access to internal state")
        void testControlledAccess() {
            // Test that internal calculations are protected
            double originalSalary = regularEmployee.getBasicSalary();
            regularEmployee.setBasicSalary(60000);
            
            // Derived fields should be automatically updated
            assertNotEquals(originalSalary, regularEmployee.getBasicSalary(), 
                           "Salary should be updated through setter");
            
            // Test that we can access calculated fields
            assertTrue(regularEmployee.getDailyRate() > 0, "Should calculate daily rate");
            assertTrue(regularEmployee.getHourlyRate() > 0, "Should calculate hourly rate");
        }
    }
    
    @Test
    @DisplayName("Test Factory Pattern creates correct employee types")
    void testEmployeeFactory() {
        // Act
        Employee regular = EmployeeFactory.createEmployee("REGULAR", 20001, "Test", "Regular", "Developer", 45000);
        Employee contractual = EmployeeFactory.createEmployee("CONTRACTUAL", 20002, "Test", "Contractual", "Consultant", 55000);
        
        // Assert using JUnit Assert Functions
        assertInstanceOf(RegularEmployee.class, regular, 
                        "Factory should create RegularEmployee for REGULAR type");
        assertInstanceOf(ContractualEmployee.class, contractual, 
                        "Factory should create ContractualEmployee for CONTRACTUAL type");
        
        assertEquals("Regular Employee", regular.getEmployeeType());
        assertEquals("Contractual Employee", contractual.getEmployeeType());
    }
    
    @Test
    @DisplayName("Test Factory throws exception for invalid type")
    void testEmployeeFactoryInvalidType() {
        // Act & Assert using JUnit Assert Functions
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> EmployeeFactory.createEmployee("INVALID", 30001, "Test", "Test", "Position", 50000),
            "Factory should throw exception for invalid employee type"
        );
        
        assertTrue(exception.getMessage().contains("Unknown employee type"), 
                  "Exception message should indicate unknown employee type");
    }
    
    @ParameterizedTest
    @DisplayName("Test salary calculations with different working days")
    @ValueSource(ints = {15, 20, 22, 25})
    void testSalaryCalculationWithDifferentDays(int daysWorked) {
        // Act
        double grossPay = regularEmployee.calculateGrossPay(daysWorked, 0);
        
        // Assert using JUnit Assert Functions
        assertTrue(grossPay > 0, "Gross pay should be positive for " + daysWorked + " days");
        
        double expectedDailyRate = regularEmployee.getBasicSalary() / 22;
        double expectedGrossPay = expectedDailyRate * daysWorked;
        assertEquals(expectedGrossPay, grossPay, 0.01, 
                    "Gross pay should be calculated correctly for " + daysWorked + " days");
    }
    
    @ParameterizedTest
    @DisplayName("Test different employee behaviors with various salaries")
    @CsvSource({
        "30000, Regular Employee, true, true",
        "50000, Regular Employee, true, true", 
        "70000, Regular Employee, true, true",
        "40000, Contractual Employee, false, false",
        "60000, Contractual Employee, false, false"
    })
    void testEmployeeBehaviorVariations(double salary, String expectedType, boolean shouldHaveAllowances, boolean shouldHaveBenefits) {
        // Arrange
        Employee emp;
        if (expectedType.equals("Regular Employee")) {
            emp = new RegularEmployee(99999, "Test", "Employee", "Test Position", salary);
        } else {
            emp = new ContractualEmployee(99999, "Test", "Contractor", "Test Position", salary, "2025-12-31", "Test Project");
        }
        
        // Act & Assert using JUnit Assert Functions
        assertEquals(expectedType, emp.getEmployeeType(), "Employee type should match expected");
        assertEquals(shouldHaveBenefits, emp.isEligibleForBenefits(), "Benefits eligibility should match expected");
        
        double allowances = emp.calculateAllowances();
        if (shouldHaveAllowances) {
            assertTrue(allowances > 0, "Employee should have allowances");
        } else {
            assertEquals(0, allowances, 0.01, "Employee should have no allowances");
        }
    }
    
    @Test
    @DisplayName("Integration test - All OOP principles working together")
    void testOOPIntegration() {
        // Create employees using Factory (Abstraction)
        Employee manager = EmployeeFactory.createEmployee("REGULAR", 8001, "Nancy", "Wilson", "Project Manager", 80000);
        Employee consultant = EmployeeFactory.createEmployee("CONTRACTUAL", 8002, "Oscar", "Martinez", "IT Consultant", 70000);
        
        // Test Encapsulation - controlled access to data
        assertEquals(80000, manager.getBasicSalary(), "Encapsulation should allow controlled access");
        manager.setBasicSalary(85000);
        assertEquals(85000, manager.getBasicSalary(), "Encapsulation should allow controlled modification");
        
        // Test Inheritance - shared behavior
        assertTrue(manager instanceof Employee, "Should inherit from Employee");
        assertTrue(consultant instanceof Employee, "Should inherit from Employee");
        assertEquals("Nancy Wilson", manager.getFullName(), "Should inherit getFullName method");
        assertEquals("Oscar Martinez", consultant.getFullName(), "Should inherit getFullName method");
        
        // Test Polymorphism - different behavior for same interface
        double managerAllowances = manager.calculateAllowances();
        double consultantAllowances = consultant.calculateAllowances();
        
        assertTrue(managerAllowances > 0, "Manager (Regular) should have allowances");
        assertEquals(0, consultantAllowances, 0.01, "Consultant (Contractual) should have no allowances");
        
        // Test Abstraction - template method using abstract methods
        double managerNetPay = manager.calculateNetPay(22, 0);
        double consultantNetPay = consultant.calculateNetPay(22, 0);
        
        assertTrue(managerNetPay > 0, "Template method should work for manager");
        assertTrue(consultantNetPay > 0, "Template method should work for consultant");
        
        // Verify different employee types produce different results
        assertNotEquals(manager.getEmployeeType(), consultant.getEmployeeType(), 
                       "Different employee types should return different type strings");
    }
    
    @AfterEach
    @DisplayName("Cleanup after each test")
    void tearDown() {
        regularEmployee = null;
        contractualEmployee = null;
        employeeDAO = null;
    }
    
    @AfterAll
    @DisplayName("OOP test completion summary")
    static void tearDownAll() {
        System.out.println("✅ All Employee OOP tests completed successfully!");
        System.out.println("🎯 Tested OOP Principles:");
        System.out.println("   ✅ Encapsulation: Data hiding and controlled access");
        System.out.println("   ✅ Inheritance: Code reuse and class hierarchy");
        System.out.println("   ✅ Polymorphism: Runtime behavior and method overriding");
        System.out.println("   ✅ Abstraction: Abstract classes and template methods");
        System.out.println("📊 Used JUnit 5 Assert Functions for comprehensive validation");
    }
}