@echo off
setlocal

set "NODE_EXE=%APPDATA%\JetBrains\IntelliJIdea2025.3\node\versions\24.18.1\node.exe"

if not exist "%NODE_EXE%" (
    set "NODE_EXE=%LOCALAPPDATA%\JetBrains\acp-agents\.runtimes\node\24.13.0\bin\node.exe"
)

if not exist "%NODE_EXE%" (
    echo Node.js was not found. Install Node.js or configure it in IntelliJ.
    exit /b 1
)

cd /d "%~dp0"
"%NODE_EXE%" "%~dp0node_modules\vite\bin\vite.js"
