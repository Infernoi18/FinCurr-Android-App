@echo off
cd /d "c:\Users\kumar.ayush\StudioProjects\Fincurr"
call .\gradlew.bat testDebugUnitTest
if errorlevel 1 (
    echo.
    echo testDebugUnitTest failed, trying assembleDebug instead...
    call .\gradlew.bat assembleDebug
)
