@echo off
cls
set CURRENT=%cd%
set BUILDER=C:\TestServerMinecraft
set CONFFILE=pom.xml
cd %BUILDER%

@echo off
copy /y %BUILDER%\src\main\java\configs\ConfigManager.java %BUILDER%\backups\ConfigManager.bck
copy /y %BUILDER%\build-WJE.bat %BUILDER%\backups\build-WJE.bck
copy /y %BUILDER%\pom.xml %BUILDER%\backups\pom.bck

call java --version
call mvn --version
call mvn -U clean install package -f %BUILDER%

echo Job's done look if BUILD SUCCES...
set /p "id=</>Press enter to continue:"