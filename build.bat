@echo off
echo.
echo ----------------------------------------------------------------------------------------------------------------
echo ----------------------------------------------------------------------------------------------------------------
echo WWWWWWWW                           WWWWWWWW                  JJJJJJJJJJJ        EEEEEEEEEEEEEEEEEEEEEE
echo W::::::W                           W::::::W                  J:::::::::J        E::::::::::::::::::::E
echo W::::::W                           W::::::W                  J:::::::::J        E::::::::::::::::::::E
echo W::::::W                           W::::::W                  JJ:::::::JJ        EE::::::EEEEEEEEE::::E
echo  W:::::W           WWWWW           W:::::W                     J:::::J            E:::::E       EEEEEE
echo   W:::::W         W:::::W         W:::::W                      J:::::J            E:::::E             
echo    W:::::W       W:::::::W       W:::::W                       J:::::J            E::::::EEEEEEEEEE   
echo     W:::::W     W:::::::::W     W:::::W                        J:::::j            E:::::::::::::::E   
echo      W:::::W   W:::::W:::::W   W:::::W                         J:::::J            E:::::::::::::::E   
echo       W:::::W W:::::W W:::::W W:::::W              JJJJJJJ     J:::::J            E::::::EEEEEEEEEE   
echo        W:::::W:::::W   W:::::W:::::W               J:::::J     J:::::J            E:::::E             
echo         W:::::::::W     W:::::::::W                J::::::J   J::::::J            E:::::E       EEEEEE
echo          W:::::::W       W:::::::W                 J:::::::JJJ:::::::J          EE::::::EEEEEEEE:::::E
echo           W:::::W         W:::::W           ......  JJ:::::::::::::JJ    ...... E::::::::::::::::::::E
echo            W:::W           W:::W            .::::.    JJ:::::::::JJ      .::::. E::::::::::::::::::::E
echo             WWW             WWW             ......      JJJJJJJJJ        ...... EEEEEEEEEEEEEEEEEEEEEE
echo ----------------------------------------------------------------------------------------------------------------
echo ----------------------------------------------------------------------------------------------------------------
echo.

set BUILDER=C:\projects\Whitelist-DMC
set CONFFILE=pom.xml
cd %BUILDER%

@echo off
copy /y %BUILDER%\src\main\java\configs\ConfigManager.java %BUILDER%\backups\ConfigManager.bck
copy /y %BUILDER%\build-WDMC.bat %BUILDER%\backups\build-WDMC.bck
copy /y %BUILDER%\launch.bat %BUILDER%\backups\launch.bck
copy /y %BUILDER%\debug_launch.bat %BUILDER%\backups\debug_launch.bck
copy /y %BUILDER%\src\main\resources\plugin.yml %BUILDER%\backups\plugin.bck
copy /y %BUILDER%\src\main\resources\config.yml %BUILDER%\backups\config.bck
copy /y %BUILDER%\pom.xml %BUILDER%\backups\pom.bck

echo.

call java --version
call mvn --version
call mvn javadoc:javadoc
call mvn clean install package

echo Job's done look if BUILD SUCCES...
