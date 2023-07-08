set SERVER=C:\projects\Whitelist-DMC\server_test
set BUILDER=C:\projects\Whitelist-DMC
set PLUGIN_FILTER=Whitelist*shaded.jar


cd %BUILDER%
XCOPY target\%PLUGIN_FILTER% %SERVER%\plugins\ /S /Y

cd %SERVER%
START "Minecraft" launch.bat

echo Job's done SERVER is starting...
