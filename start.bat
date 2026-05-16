@echo off
chcp 65001 >nul
title Omni-Ecom OS 启动器

echo ============================================
echo   Omni-Ecom OS 一键启动
echo ============================================
echo.
echo [1/2] 启动后端服务 (Spring Boot :10004)...
start "Omni-Ecom Backend" cmd /c "cd /d C:\liangtao\opencode\omni-ecom-os-v1.0\backend && mvn spring-boot:run"

echo [2/2] 等待后端启动并启动前端 (Vite :10003)...
timeout /t 15 /nobreak >nul
start "Omni-Ecom Frontend" cmd /c "cd /d C:\liangtao\opencode\omni-ecom-os-v1.0 && npm run dev"

echo.
echo ============================================
echo   前端: http://localhost:10003
echo   后端: http://localhost:10004/api
echo ============================================
echo.
echo 请勿关闭此窗口，关闭后端/前端窗口即可停止服务。
pause
