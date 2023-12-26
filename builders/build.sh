SERVER=./server_test
TARGET=./target
SITE_TRGT=$TARGET/site
SITE_DIST=$SERVER/plugins/WhitelistDmc/site

rm -rf $TARGET

java --version;
mvn --version;
mvn clean install package site;

rm -rf $SITE_DIST
cp -r $SITE_TRGT $SITE_DIST

echo "---------------------------------------"
echo "Job's done look if BUILD SUCCES...";
echo "---------------------------------------"

exit 0;
