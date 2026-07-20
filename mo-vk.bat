@echo off
title Mo VK Link
echo.
echo ========================================
echo        MO VIDEO VK TREN DIEN THOAI
echo ========================================
echo.
set /p url="DAN LINK VK VAO DAY: "
if "%url%"=="" (
    echo KHONG CO LINK!
    pause
    exit /b
)
echo.
echo Dang mo link tren dien thoai...
"D:\DATA_OLD\ADB_AppControl\adb\adb.exe" shell am start -a android.intent.action.VIEW -d "%url%"
echo.
echo XONG! Kiem tra dien thoai nhe.
echo.
pause
