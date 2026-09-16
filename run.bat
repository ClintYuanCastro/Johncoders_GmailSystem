@echo off
cd /d "%~dp0"
where java >nul 2>nul
if %errorlevel% equ 0 (
    java -cp "target/classes;lib/*" prelim.group.login.Main
) else (
    "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\jbr\bin\java.exe" -cp "target/classes;lib/*" prelim.group.login.Main
)
if errorlevel 1 pause
