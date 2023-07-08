set SERVER=C:\projects\PaperMc
set BUILDER=C:\projects\Whitelist-Je
set PLUGIN_FILTER=Whitelist*shaded.jar


cd %BUILDER%
XCOPY target\%PLUGIN_FILTER% %SERVER%\plugins\ /S /Y

cd %SERVER%
START "Minecraft-Debug" launch_debug.bat

echo Job's done SERVER is awaiting debug socket connection...
