(mkdir build\froid\app) 2> $null
$jars = Get-ChildItem -File -Attributes !ReadOnly -path ".\lib" | % { $_.FullName }
$cpJars = [string]::Join(";", $jars)
javac -cp """$cpJars""" -bootclasspath "C:\Program Files\Java\jre1.8.0_111\lib\rt.jar" -source 1.7 -target 1.7 -d .\build .\src\java\*
java -Xmx6072m -Xss10M -XX:MaxJavaStackTraceDepth=-1 -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Xverify:none -cp """$cpJars""" frege.compiler.Main -target 1.7 -d .\build .\src\frege\froid\text\Editable.fr
