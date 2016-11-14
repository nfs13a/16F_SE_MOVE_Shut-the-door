javac -cp "jars/*;." implementation/TestSCM.java implementation/StudentCourseManager.java implementation/ScriptRunner.java
@ECHO OFF
powershell.exe -Command "& 'cucumber.ps1'"
java -cp "jars/*;." implementation/TestSCM
powershell.exe -Command "& 'remove.ps1'"