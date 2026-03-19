@REM Maven Wrapper for Windows
@SET MAVEN_VERSION=3.9.6
@SET MAVEN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip
@SET MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%

@IF NOT EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
  echo Downloading Apache Maven %MAVEN_VERSION%...
  mkdir "%MAVEN_HOME%"
  powershell -Command "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%TEMP%\maven.zip'; Expand-Archive '%TEMP%\maven.zip' -DestinationPath '%TEMP%\maven-extract'; Move-Item '%TEMP%\maven-extract\apache-maven-%MAVEN_VERSION%\*' '%MAVEN_HOME%'"
)

@"%MAVEN_HOME%\bin\mvn.cmd" %*
