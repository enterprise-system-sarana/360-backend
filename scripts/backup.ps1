# ==============================================================================
# 360 Phone Shop Management System - Automated Backup Script (Windows PowerShell)
# ==============================================================================
# Usage:
#   .\backup.ps1
# ==============================================================================

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectDir = Split-Path -Parent $ScriptDir

# Load .env variables if present
$EnvFile = Join-Path $ProjectDir ".env"
if (Test-Path $EnvFile) {
    Get-Content $EnvFile | Where-Object { $_ -match '^[A-Za-z_][A-Za-z0-9_]*=' } | ForEach-Object {
        $key, $value = $_.Split('=', 2)
        [System.Environment]::SetEnvironmentVariable($key.Trim(), $value.Trim())
    }
}

$PostgresContainer = if ($env:POSTGRES_CONTAINER) { $env:POSTGRES_CONTAINER } else { "360-postgres" }
$PostgresUser      = if ($env:POSTGRES_USER) { $env:POSTGRES_USER } else { "postgres" }
$PostgresDb        = if ($env:POSTGRES_DB) { $env:POSTGRES_DB } else { "db_360ff" }
$RustfsVolume      = if ($env:RUSTFS_VOLUME) { $env:RUSTFS_VOLUME } else { "360_phone_shop_rustfs_data" }
$BackupRoot        = Join-Path $ProjectDir "backups"
$RetentionDays     = 7

$Timestamp = (Get-Date).ToString("yyyyMMdd_HHmmss")
$BackupPath = Join-Path $BackupRoot $Timestamp

New-Item -ItemType Directory -Path $BackupPath -Force | Out-Null

Write-Host "======================================================" -ForegroundColor Cyan
Write-Host "Starting 360 System Backup: $Timestamp" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

# 1. PostgreSQL Database Dump
Write-Host "Backing up PostgreSQL database '$PostgresDb'..." -ForegroundColor Yellow
$DbBackupFile = Join-Path $BackupPath "postgres_${PostgresDb}_${Timestamp}.sql"

docker exec $PostgresContainer pg_dump -U $PostgresUser -d $PostgresDb --clean --if-exists --no-owner --no-privileges | Out-File -FilePath $DbBackupFile -Encoding utf8

if ((Get-Item $DbBackupFile).Length -gt 0) {
    Write-Host "✓ PostgreSQL backup successful: $DbBackupFile" -ForegroundColor Green
} else {
    Write-Error "PostgreSQL backup failed or file is empty!"
}

# 2. RustFS S3 Volume
Write-Host "Backing up RustFS storage volume '$RustfsVolume'..." -ForegroundColor Yellow
$StorageArchive = Join-Path $BackupPath "rustfs_data_${Timestamp}.tar.gz"

docker run --rm `
    -v "${RustfsVolume}:/source:ro" `
    -v "${BackupPath}:/backup" `
    alpine sh -c "tar -czf /backup/rustfs_data_${Timestamp}.tar.gz -C /source ."

if (Test-Path $StorageArchive) {
    Write-Host "✓ RustFS volume backup successful: $StorageArchive" -ForegroundColor Green
}

# 3. Clean up older backups
Write-Host "Cleaning up backups older than $RetentionDays days..." -ForegroundColor Yellow
Get-ChildItem -Path $BackupRoot -Directory | Where-Object {
    $_.CreationTime -lt (Get-Date).AddDays(-$RetentionDays)
} | Remove-Item -Recurse -Force

Write-Host "======================================================" -ForegroundColor Cyan
Write-Host "Backup completed! Location: $BackupPath" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

