set PROJECT=C:\projects\Whitelist-DMC
set SERVER=%PROJECT%\server_test
set PLUGIN_FILTER=Whitelist*shaded.jar


cd %BUILDER%
XCOPY target\%PLUGIN_FILTER% %SERVER%\plugins\ /S /Y

cd %SERVER%
START "Minecraft" launch.bat

@echo .
@echo Job's done SERVER is starting...
