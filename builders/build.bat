CALL java --version
CALL mvn --version
CALL mvn javadoc:javadoc
CALL mvn clean install package

echo ""
echo "Job's done look if BUILD SUCCES..."

exit 0
