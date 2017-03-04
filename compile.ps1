(mkdir build\froid\app) 2>&1> $null
$jars = Get-ChildItem -File -Attributes !ReadOnly -path ".\lib" | % { $_.FullName }
$cpJars = [string]::Join(";", $jars)

($runtime = java -verbose) 2>&1> $null

$s = ($runtime -split '\n')[0]
$rtjar = $s.Substring(8, $s.length - 9)

javac -cp """$cpJars""" -bootclasspath """$rtjar""" -source 1.7 -target 1.7 -d .\build .\src\java\*
java -Xmx6072m -Xss10M -XX:MaxJavaStackTraceDepth=-1 -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Xverify:none -cp """$cpJars""" frege.compiler.Main -target 1.7 -d .\build .\src\frege\froid\
