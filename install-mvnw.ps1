$version = "3.3.2"
$base_url = "https://raw.githubusercontent.com/takari/maven-wrapper/mvnw-$version"

Write-Host "Downloading Maven Wrapper v$version..."

# Create .mvn/wrapper directory
$wrapper_dir = ".mvn\wrapper"
if (!(Test-Path -Path $wrapper_dir)) {
    New-Item -ItemType Directory -Path $wrapper_dir | Out-Null
}

# Download mvnw.cmd
Invoke-WebRequest -Uri "$base_url/mvnw.cmd" -OutFile "mvnw.cmd"

# Download mvnw (for WSL/Bash if needed)
Invoke-WebRequest -Uri "$base_url/mvnw" -OutFile "mvnw"

# Download maven-wrapper.jar
Invoke-WebRequest -Uri "$base_url/maven-wrapper.jar" -OutFile "$wrapper_dir\maven-wrapper.jar"

# Download maven-wrapper.properties
Invoke-WebRequest -Uri "$base_url/maven-wrapper.properties" -OutFile "$wrapper_dir\maven-wrapper.properties"

Write-Host "Maven Wrapper installed successfully!"
