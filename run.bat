@echo off
REM Build and run Mini Twitter on Windows.
setlocal

echo Compiling...
if not exist out mkdir out
javac -d out src\minitwitter\Driver.java src\minitwitter\model\*.java src\minitwitter\observer\*.java src\minitwitter\visitor\*.java src\minitwitter\ui\*.java
if errorlevel 1 (
    echo Compile failed.
    exit /b 1
)

echo Running...
java -cp out minitwitter.Driver

endlocal
