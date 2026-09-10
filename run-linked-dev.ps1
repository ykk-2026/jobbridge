$ErrorActionPreference = 'Stop'

$projectRoot = $PSScriptRoot
$frontendRoot = if ($env:YKK_FRONTEND_DIR) {
    $env:YKK_FRONTEND_DIR
} else {
    'C:\Users\data8320-13\Desktop\YKK\YKK-Frontend'
}

$nodeCandidates = @(
    "$env:APPDATA\JetBrains\IntelliJIdea2025.3\node\versions\24.18.1\node.exe",
    "$env:LOCALAPPDATA\JetBrains\acp-agents\.runtimes\node\24.13.0\bin\node.exe"
)
$nodeExe = $nodeCandidates | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1

if (-not $nodeExe) {
    throw 'Node.js was not found. Install Node.js or configure it in IntelliJ.'
}
if (-not (Test-Path -LiteralPath (Join-Path $frontendRoot 'src\main.tsx'))) {
    throw "YKK-Frontend was not found at $frontendRoot"
}

$env:YKK_FRONTEND_DIR = $frontendRoot
$watchOutput = Join-Path $projectRoot 'frontend-linked-watch.log'
$watchError = Join-Path $projectRoot 'frontend-linked-watch-error.log'
$viteCli = Join-Path $projectRoot 'frontend\node_modules\vite\bin\vite.js'
$viteConfig = Join-Path $projectRoot 'frontend\vite.linked.config.ts'

$watchProcess = Start-Process -FilePath $nodeExe `
    -ArgumentList @($viteCli, 'build', '--watch', '--config', $viteConfig) `
    -WorkingDirectory $projectRoot `
    -RedirectStandardOutput $watchOutput `
    -RedirectStandardError $watchError `
    -WindowStyle Hidden `
    -PassThru

Write-Host "YKK-Frontend watcher started (PID $($watchProcess.Id))."
Write-Host 'Open http://localhost:8080 after Spring Boot starts.'

try {
    & (Join-Path $projectRoot 'mvnw.cmd') spring-boot:run
} finally {
    if (-not $watchProcess.HasExited) {
        Stop-Process -Id $watchProcess.Id -Force
    }
}
