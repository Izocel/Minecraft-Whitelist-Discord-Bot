cls
@echo off
set JAVA18_FOLDER=D:\Program Files\Java\jdk-18.0.2
set MC_SERVER_FILE=paper-1.19.2-141.jar
set BOOT_MAX_RAM=8G
set EXEC_MAX_RAM=8G

:begin
set xms = -Xms%BOOT_MAX_RAM%
set xms = -Xms%EXCEC_MAX_RAM%
"%JAVA18_FOLDER%\bin\java.exe" -version
"%JAVA18_FOLDER%\bin\java.exe" %xms% %xmx% -jar "%MC_SERVER_FILE%"
echo closing script...
timeout 10

