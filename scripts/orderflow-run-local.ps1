param(
    [string]$PostgresUrl = $env:POSTGRES_URL,
    [string]$PostgresUsername = $env:POSTGRES_USERNAME,
    [string]$PostgresPassword = $env:POSTGRES_PASSWORD,
    [string]$Port = "8080",
    [switch]$PersistOpenAiKey
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($PostgresUrl)) {
    $PostgresUrl = "jdbc:postgresql://localhost:5432/hackathon"
}
if ([string]::IsNullOrWhiteSpace($PostgresUsername)) {
    throw "POSTGRES_USERNAME is required"
}
if ([string]::IsNullOrWhiteSpace($PostgresPassword)) {
    throw "POSTGRES_PASSWORD is required"
}
if ($PersistOpenAiKey -and [string]::IsNullOrWhiteSpace($env:OPENAI_API_KEY)) {
    throw "OPENAI_API_KEY must be set in the current shell when -PersistOpenAiKey is used"
}

$env:SPRING_PROFILES_ACTIVE = "local"
$env:POSTGRES_URL = $PostgresUrl
$env:POSTGRES_USERNAME = $PostgresUsername
$env:POSTGRES_PASSWORD = $PostgresPassword
$env:SERVER_PORT = $Port
$env:ORDERFLOW_OPENAI_PERSIST_API_KEY = if ($PersistOpenAiKey) { "true" } else { "false" }

Write-Host "Starting OrderFlow backend on http://localhost:$Port"
Write-Host "PostgreSQL URL: $PostgresUrl"
Write-Host "OpenAI key persistence: $env:ORDERFLOW_OPENAI_PERSIST_API_KEY"

.\mvnw.cmd spring-boot:run
