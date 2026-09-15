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

$viteOutput = Join-Path $projectRoot 'ykk-vite-dev.log'
$viteError = Join-Path $projectRoot 'ykk-vite-dev-error.log'
$viteCli = Join-Path $frontendRoot 'node_modules\vite\bin\vite.js'

if (-not (Test-Path -LiteralPath $viteCli)) {
    throw "YKK-Frontend dependencies were not found. Run npm install in $frontendRoot"
}

$viteProcess = Start-Process -FilePath $nodeExe `
    -ArgumentList @($viteCli) `
    -WorkingDirectory $frontendRoot `
    -RedirectStandardOutput $viteOutput `
    -RedirectStandardError $viteError `
    -WindowStyle Hidden `
    -PassThru

Write-Host "YKK-Frontend dev server started (PID $($viteProcess.Id))."
Write-Host 'Open http://localhost:5173 after Spring Boot starts.'

try {
    & (Join-Path $projectRoot 'mvnw.cmd') spring-boot:run
} finally {
    if (-not $viteProcess.HasExited) {
        Stop-Process -Id $viteProcess.Id -Force
    }
}
