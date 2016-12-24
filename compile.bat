@ECHO OFF
(mkdir build\froid\app) 2> $null
javac -cp ".\lib\frege-3.24-7.100.jar;.\lib\android.jar;.\build" -bootclasspath "C:\Program Files\Java\jre1.8.0_111\lib\rt.jar" -source 1.7 -target 1.7 -d .\build .\src\java\FregeActivity.java
java -Xmx6072m -Xss10M -XX:MaxJavaStackTraceDepth=-1 -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Xverify:none -cp ".\lib\frege-3.24-7.100.jar;.\lib\android.jar;.\build" frege.compiler.Main -target 1.7 -d .\build .\src\frege