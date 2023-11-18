cls
@echo off
set JAVA_FOLDER=D:\Program Files\Java\jdk-18.0.2
set DEBUG_ARGS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005
set MC_SERVER_FILE=paper.jar
set BOOT_MAX_RAM=8G
set EXEC_MAX_RAM=8G

:begin
set xms = -Xms%BOOT_MAX_RAM%
set xms = -Xms%EXCEC_MAX_RAM%
"%JAVA_FOLDER%\bin\java.exe" -version
"%JAVA_FOLDER%\bin\java.exe" %DEBUG_ARGS% %xms% %xmx% -jar "%MC_SERVER_FILE%" -nogui
echo closing script...
timeout 10
