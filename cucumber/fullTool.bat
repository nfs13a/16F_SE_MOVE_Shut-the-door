javac -cp "jars/*;." implementation/TestSCMX.java implementation/StudentCourseManagerX.java implementation/ScriptRunner.java
@ECHO OFF
powershell.exe -Command "& 'cucumber.ps1'"
java -cp "jars/*;." implementation/TestSCMX
powershell.exe -Command "& 'remove.ps1'"