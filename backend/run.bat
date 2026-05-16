@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
set "MAVEN_HOME=C:\liangtao\ruanjian\apache-maven-3.9.15"
set "Path=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%Path%"
echo Starting Spring Boot on port 10004...
cd /d "%~dp0"
call mvn spring-boot:run
pause
