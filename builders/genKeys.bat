set PROJECT=C:\projects\Whitelist-DMC-Node
set SERVER=%PROJECT%\server_test

call keytool -genkeypair -keystore selfsigned.jks -keyalg RSA -alias selfsigned -dname "CN=rvdprojects.com L=Qc S=Qc C=CA" -storepass examplestorepass
@REM call keytool -exportcert -keystore selfsigned.jks -alias selfsigned -storepass examplestorepass -file test1.cer
@REM call keytool -importcert -keystore truststore -alias selfsigned -storepass examplestorepass -file test1.cer -noprompt

move selfsigned.jks %PROJECT%\src\main\resources\