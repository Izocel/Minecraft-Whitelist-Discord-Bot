set PROJECT=C:\projects\Whitelist-DMC-Node
set SERVER=%PROJECT%\server_test
set PLUGIN_FILTER=Whitelist*shaded.jar


cd %BUILDER%
XCOPY target\%PLUGIN_FILTER% %SERVER%\plugins\ /S /Y

cd %SERVER%
START "Minecraft-Debug" launch_debug.bat

@echo .
@echo "Job's done SERVER is awaiting debug socket connection..."
exit 0