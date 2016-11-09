javac -cp "jars/*;." implementation/TestSCM.java
@ECHO OFF
powershell.exe -Command "& 'cucumber.ps1'"
java -cp "jars/*;." implementation/TestSCM