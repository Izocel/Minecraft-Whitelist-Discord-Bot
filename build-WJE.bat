@echo off
cls
set CURRENT=%cd%
set BUILDER=C:/TestServerMinecraft/
set CONFFILE=pom.xml
cd %BUILDER%
ls
call java --version
call mvn --version
call mvn -U clean install package -f %BUILDER%