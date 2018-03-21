(mkdir build\froid\app) 2>&1> $null

$jars = Get-ChildItem -File -Attributes !ReadOnly -path ".\lib" | % { $_.FullName }
$cpJars = [string]::Join(";", $jars)

($runtime = java -verbose) 2>&1> $null

$s = ($runtime -split '\n')[0]
$rtjar = $s.Substring(8, $s.length - 9)

# compile frege
$fregec = "java -Xmx6072m -Xss10M -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Xverify:none -cp `"$cpJars`" frege.compiler.Main -target 1.7 -d .\build"

# compile java files
javac -cp """$cpJars""" -bootclasspath """$rtjar""" -source 1.7 -target 1.7 -d .\build .\src\java\*

# compile modules with no dependencies
# Invoke-Expression "$fregec .\src\frege\froid\view\View.fr"
# Invoke-Expression "$fregec .\src\frege\froid\view\ViewGroup.fr"
# Invoke-Expression "$fregec .\src\frege\froid\widget\CompoundButton.fr"
# Invoke-Expression "$fregec .\src\frege\froid\view\Menu.fr"
# Invoke-Expression "$fregec .\src\frege\froid\Types.fr"
# Invoke-Expression "$fregec .\src\frege\froid\content\res"
# Invoke-Expression "$fregec .\src\frege\froid\java\nio\IntBuffer.fr"
# Invoke-Expression "$fregec .\src\frege\froid\animation"
# Invoke-Expression "$fregec .\src\frege\froid\text\style"
# Invoke-Expression "$fregec .\src\frege\froid\util"

# compile modules with already compiled dependencies
Invoke-Expression "$fregec .\src\frege\froid\media"
# Invoke-Expression "$fregec .\src\frege\froid\text\Editable.fr"
# Invoke-Expression "$fregec .\src\frege\froid\text"
# Invoke-Expression "$fregec .\src\frege\froid\content\Context.fr"
# Invoke-Expression "$fregec .\src\frege\froid\content\Intent.fr"
# Invoke-Expression "$fregec .\src\frege\froid\graphics"
# Invoke-Expression "$fregec .\src\frege\froid\os"
# Invoke-Expression "$fregec .\src\frege\froid\widget"
# Invoke-Expression "$fregec .\src\frege\froid\view"
# Invoke-Expression "$fregec .\src\frege\froid\content\res"
# Invoke-Expression "$fregec .\src\frege\froid\app\java"
# Invoke-Expression "$fregec .\src\frege\froid\app"
# Invoke-Expression "$fregec .\src\frege\froid\app\Activity.fr"
# Invoke-Expression "$fregec .\src\frege\froid\java"
# Invoke-Expression "$fregec .\src\frege\froid\javax"
# Invoke-Expression "$fregec .\src\frege\froid\opengl\glSurfaceView\java"
# Invoke-Expression "$fregec .\src\frege\froid\opengl\glSurfaceView"
# Invoke-Expression "$fregec .\src\frege\froid\opengl\GLSurfaceView.fr"
