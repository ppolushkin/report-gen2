# Download jdk17
$zip = "$pwd\openjdk-17.0.2_windows-x64_bin.zip"
Invoke-WebRequest -Uri "https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_windows-x64_bin.zip" -OutFile $zip
Expand-Archive -Path $zip -DestinationPath "$pwd"
Remove-Item $zip

# Download application
$applicationProperties="$pwd\application.properties"

Invoke-WebRequest -Uri "https://github.com/ppolushkin/report-gen2/raw/master/prod/report-generator2-1.1.jar" -OutFile "$pwd\report-generator2-1.1.jar"
Invoke-WebRequest -Uri "https://github.com/ppolushkin/report-gen2/raw/master/prod/application.properties" -OutFile $applicationProperties
Invoke-WebRequest -Uri "https://github.com/ppolushkin/report-gen2/raw/master/prod/generate2.bat" -OutFile "$pwd\generate2.bat"
Invoke-WebRequest -Uri "https://github.com/ppolushkin/report-gen2/raw/master/prod/README.md" -OutFile "$pwd\README.md"


# Update application.properties
(Get-Content $applicationProperties).Replace("@PWD@", $pwd) | Set-Content $applicationProperties
(Get-Content $applicationProperties).Replace("\", "/") | Set-Content $applicationProperties