@echo off
REM build.bat - CREATE THIS FILE in your project root directory

echo ========================================
echo  MotorPH Payroll System - Build Script
echo ========================================
echo.

REM Set paths
set SRC_DIR=src
set BUILD_DIR=build
set CLASSES_DIR=%BUILD_DIR%\classes
set LIB_DIR=lib

REM Create build directory
echo Creating build directories...
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"
if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"
if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

REM Check for basic JAR file (MySQL at minimum)
if not exist "%LIB_DIR%\mysql-connector-java-8.0.33.jar" (
    echo.
    echo WARNING: MySQL Connector JAR not found in lib folder
    echo Please download: mysql-connector-java-8.0.33.jar
    echo URL: https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar
    echo.
    echo Continuing compilation without MySQL connector...
    echo.
)

REM Set classpath (handle missing lib directory gracefully)
if exist "%LIB_DIR%\*.jar" (
    set CLASSPATH=%LIB_DIR%\*;%SRC_DIR%
    echo Using classpath with libraries: %CLASSPATH%
) else (
    set CLASSPATH=%SRC_DIR%
    echo Using basic classpath: %CLASSPATH%
    echo Note: Some features may not work without required JAR files
)

echo.
echo Compiling Java files...
echo.

REM Compile Java files
javac -cp "%CLASSPATH%" -d "%CLASSES_DIR%" -Xlint:unchecked ^
    %SRC_DIR%\model\*.java ^
    %SRC_DIR%\service\*.java ^
    %SRC_DIR%\dao\*.java ^
    %SRC_DIR%\ui\*.java ^
    %SRC_DIR%\util\*.java

REM Check compilation result
if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ COMPILATION FAILED!
    echo.
    echo Common fixes:
    echo 1. Make sure you created RegularEmployee.java in src\model\
    echo 2. Make sure you updated PayslipData.java 
    echo 3. Make sure you created SimplePayslipService.java in src\service\
    echo 4. Download required JAR files to lib\ folder
    echo.
    pause
    exit /b 1
)

echo.
echo ✅ COMPILATION SUCCESSFUL!
echo.

REM Try to compile test files if they exist
if exist "%SRC_DIR%\test" (
    echo Compiling test files...
    javac -cp "%CLASSES_DIR%;%CLASSPATH%" -d "%CLASSES_DIR%" %SRC_DIR%\test\*.java
    
    if %ERRORLEVEL% equ 0 (
        echo ✅ Test compilation successful!
    ) else (
        echo ⚠️  Test compilation failed (this is OK for now)
    )
    echo.
)

echo Build completed successfully!
echo.
echo To run the application:
echo java -cp "%CLASSES_DIR%;%CLASSPATH%" ui.MainApplication
echo.

REM Ask if user wants to run the application
set /p choice="Do you want to run the application now? (y/n): "
if /i "%choice%"=="y" (
    echo.
    echo Starting MotorPH Payroll System...
    echo.
    java -cp "%CLASSES_DIR%;%CLASSPATH%" ui.MainApplication
)

echo.
echo Build script completed.
pause