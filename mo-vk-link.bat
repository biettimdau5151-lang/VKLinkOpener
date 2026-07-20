@echo off
title MO VK LINK - vietnam version
color 0A
cls
echo.
echo  ╔══════════════════════════════════════╗
echo  ║     MO VIDEO VK TREN DIEN THOAI     ║
echo  ╚══════════════════════════════════════╝
echo.
echo  Cach dung: 
echo  - Copy link VK
echo  - Paste vao day
echo  - Nhan Enter
echo.
echo ───────────────────────────────────────
echo.
set /p url="  DAN LINK VK: "
echo.
if "%url%"=="" (
    echo  [LOI] Khong co link!
    goto :end
)
echo  [OK] Dang mo tren dien thoai...
echo.
"D:\DATA_OLD\ADB_AppControl\adb\adb.exe" shell am start -a android.intent.action.VIEW -d "%url%"
echo.
echo  [XONG] Kiem tra dien thoai nhe!
echo.
:end
echo  Nhan phim bat ky de thoat...
pause >nul
