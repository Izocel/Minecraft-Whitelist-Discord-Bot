@echo off
cls
set BUILDER=C:\Users\Utilisateur\Desktop\Whitelist-je\13
set CONFFILE=pom.xml
cd %BUILDER%

@echo off
copy /y %BUILDER%\src\main\java\configs\ConfigManager.java %BUILDER%\backups\ConfigManager.bck
copy /y %BUILDER%\build-WJE.bat %BUILDER%\backups\build-WJE.bck
copy /y %BUILDER%\launch.bat %BUILDER%\backups\launch.bck
copy /y %BUILDER%\src\main\resources\plugin.yml %BUILDER%\backups\plugin.bck
copy /y %BUILDER%\pom.xml %BUILDER%\backups\pom.bck

call java --version
call mvn --version
call mvn -U clean install package -f %BUILDER%

echo Job's done look if BUILD SUCCES...
set /p "id=</>Press enter to continue:"