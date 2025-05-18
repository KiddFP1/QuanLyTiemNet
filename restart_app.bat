@echo off
echo === Dang khoi dong lai ung dung ===

:: Dung ung dung neu dang chay (cach nay chi mang tinh tham khao)
taskkill /f /im java.exe

:: Xoa database (tuong tu nhu recreate_database.sql)
sqlcmd -S localhost -U sa -P 1234 -Q "USE master; IF EXISTS (SELECT name FROM sys.databases WHERE name = 'tiemnet') BEGIN ALTER DATABASE tiemnet SET SINGLE_USER WITH ROLLBACK IMMEDIATE; DROP DATABASE tiemnet; END; CREATE DATABASE tiemnet;"

:: Cho 2 giay
timeout /t 2 > nul

:: Chay ung dung
echo === Dang chay ung dung ===
call mvnw spring-boot:run

pause 