set SERVER=.\server_test
set TARGET=.\target
set SITE_TRGT=%TARGET%\site
set SITE_DIST=%SERVER%\plugins\WhitelistDmc\site

RMDIR %TARGET% /S /Q

CALL java --version
CALL mvn --version
CALL mvn clean install package site

RMDIR %SITE_DIST% /S /Q
XCOPY %SITE_TRGT% %SITE_DIST% /S /Y /I

@echo "---------------------------------------"
@echo "Job's done look if BUILD SUCCES..."
@echo "---------------------------------------"

exit 0
