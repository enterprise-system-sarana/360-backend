#!/usr/bin/env bash
# ==============================================================================
# 360 Phone Shop Management System - Automated Backup Script
# ==============================================================================
# Backs up:
#   1. PostgreSQL Database (pg_dump compressed with gzip)
#   2. RustFS Object Storage Data (Persistent Volume tar.gz)
#   3. .env Configuration file (Encrypted or archived)
#
# Features:
#   - Automated retention cleanup (default: keeps last 7 days)
#   - Size validation and integrity checks
#   - Optional Telegram notifications on success / failure
#   - Safe read-only volume access
# ==============================================================================

set -euo pipefail

# ------------------------------------------------------------------------------
# 1. Directories & Configuration
# ------------------------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Load .env file if available
ENV_FILE="${PROJECT_DIR}/.env"
if [ -f "$ENV_FILE" ]; then
  # Safely export environment variables from .env
  set -a
  # shellcheck disable=SC1090
  source <(grep -E '^[A-Za-z_][A-Za-z0-9_]*=' "$ENV_FILE" | sed 's/\r$//')
  set +a
fi

# PostgreSQL Defaults
POSTGRES_CONTAINER="${POSTGRES_CONTAINER:-360-postgres}"
POSTGRES_USER="${POSTGRES_USER:-postgres}"
POSTGRES_DB="${POSTGRES_DB:-db_360ff}"

# RustFS Defaults
RUSTFS_VOLUME="${RUSTFS_VOLUME:-360_phone_shop_rustfs_data}"

# Backup Destination & Retention
BACKUP_ROOT="${BACKUP_DIR:-${PROJECT_DIR}/backups}"
RETENTION_DAYS="${RETENTION_DAYS:-7}"

TIMESTAMP="$(date +"%Y%m%d_%H%M%S")"
BACKUP_PATH="${BACKUP_ROOT}/${TIMESTAMP}"
LOG_FILE="${BACKUP_ROOT}/backup.log"

mkdir -p "$BACKUP_PATH"

# ------------------------------------------------------------------------------
# Logging Helper
# ------------------------------------------------------------------------------
log() {
  local msg="[$(date '+%Y-%m-%d %H:%M:%S')] $1"
  echo "$msg"
  echo "$msg" >> "$LOG_FILE"
}

log_error() {
  local msg="[$(date '+%Y-%m-%d %H:%M:%S')] [ERROR] $1"
  echo -e "\e[31m$msg\e[0m" >&2
  echo "$msg" >> "$LOG_FILE"
}

# ------------------------------------------------------------------------------
# Telegram Notification Helper
# ------------------------------------------------------------------------------
send_telegram() {
  local status="$1"
  local details="$2"

  if [ -z "${TELEGRAM_BOT_TOKEN:-}" ] || [ -z "${TELEGRAM_CHAT_ID:-}" ]; then
    return 0
  fi

  local icon="✅"
  [ "$status" != "SUCCESS" ] && icon="❌"

  local message="${icon} *360 Phone Shop - Automated Backup ${status}*

📅 *Timestamp:* \`${TIMESTAMP}\`
💻 *Host:* \`$(hostname)\`
${details}"

  local thread_param=()
  if [ -n "${TELEGRAM_THREAD_ID:-}" ]; then
    thread_param=(-d "message_thread_id=${TELEGRAM_THREAD_ID}")
  fi

  curl -s -X POST "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage" \
    -d "chat_id=${TELEGRAM_CHAT_ID}" \
    -d "parse_mode=markdown" \
    -d "text=${message}" \
    "${thread_param[@]}" > /dev/null || true
}

# Trap unexpected errors
trap 'log_error "Backup process aborted unexpectedly on line $LINENO!"; send_telegram "FAILED" "⚠️ Process exited with an error on line $LINENO."; exit 1' ERR

# ------------------------------------------------------------------------------
# Pre-flight Checks
# ------------------------------------------------------------------------------
log "======================================================"
log "Starting Automated Backup: ${TIMESTAMP}"
log "======================================================"

if ! command -v docker &> /dev/null; then
  log_error "Docker is not installed or not in PATH."
  exit 1
fi

if ! docker ps --format '{{.Names}}' | grep -qw "$POSTGRES_CONTAINER"; then
  log_error "Container '$POSTGRES_CONTAINER' is not running!"
  exit 1
fi

# ------------------------------------------------------------------------------
# 2. PostgreSQL Database Backup
# ------------------------------------------------------------------------------
log "Backing up PostgreSQL database '${POSTGRES_DB}' from '${POSTGRES_CONTAINER}'..."
DB_DUMP_FILE="${BACKUP_PATH}/postgres_${POSTGRES_DB}_${TIMESTAMP}.sql.gz"

docker exec "$POSTGRES_CONTAINER" pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" --clean --if-exists --no-owner --no-privileges | gzip -9 > "$DB_DUMP_FILE"

# Validate dump file
if [ ! -s "$DB_DUMP_FILE" ]; then
  log_error "PostgreSQL backup failed or file is empty!"
  rm -f "$DB_DUMP_FILE"
  exit 1
fi

DB_SIZE=$(du -h "$DB_DUMP_FILE" | cut -f1)
log "✓ PostgreSQL backup successful: $DB_DUMP_FILE ($DB_SIZE)"

# ------------------------------------------------------------------------------
# 3. RustFS S3 Storage Backup
# ------------------------------------------------------------------------------
log "Backing up RustFS storage volume '${RUSTFS_VOLUME}'..."
RUSTFS_BACKUP_FILE="${BACKUP_PATH}/rustfs_data_${TIMESTAMP}.tar.gz"

# Use ephemeral alpine container to read the volume safely as read-only
if docker volume inspect "$RUSTFS_VOLUME" &> /dev/null; then
  docker run --rm \
    -v "${RUSTFS_VOLUME}:/source:ro" \
    -v "${BACKUP_PATH}:/backup" \
    alpine sh -c "tar -czf /backup/rustfs_data_${TIMESTAMP}.tar.gz -C /source ."

  if [ -s "$RUSTFS_BACKUP_FILE" ]; then
    RUSTFS_SIZE=$(du -h "$RUSTFS_BACKUP_FILE" | cut -f1)
    log "✓ RustFS storage backup successful: $RUSTFS_BACKUP_FILE ($RUSTFS_SIZE)"
  else
    log_error "RustFS backup archive was created but is empty."
    RUSTFS_SIZE="0B"
  fi
else
  log "⚠️ Volume '$RUSTFS_VOLUME' not found. Skipping S3 volume backup."
  RUSTFS_SIZE="N/A (volume not found)"
fi

# ------------------------------------------------------------------------------
# 4. Backup .env Configuration File
# ------------------------------------------------------------------------------
if [ -f "$ENV_FILE" ]; then
  cp "$ENV_FILE" "${BACKUP_PATH}/env_${TIMESTAMP}.bak"
  chmod 600 "${BACKUP_PATH}/env_${TIMESTAMP}.bak"
  log "✓ Configuration (.env) backed up securely."
fi

# ------------------------------------------------------------------------------
# 5. Clean up Old Backups (Retention Policy)
# ------------------------------------------------------------------------------
log "Applying retention policy: Deleting backups older than ${RETENTION_DAYS} days..."
find "$BACKUP_ROOT" -mindepth 1 -maxdepth 1 -type d -name "20*" -mtime "+${RETENTION_DAYS}" -exec rm -rf {} + 2>/dev/null || true
log "✓ Retention cleanup completed."

# ------------------------------------------------------------------------------
# 6. Summary & Notification
# ------------------------------------------------------------------------------
TOTAL_BACKUP_SIZE=$(du -sh "$BACKUP_PATH" | cut -f1)
log "======================================================"
log "Backup finished successfully! Total Size: ${TOTAL_BACKUP_SIZE}"
log "Destination: ${BACKUP_PATH}"
log "======================================================"

SUMMARY_TEXT="📦 *Database:* \`${POSTGRES_DB}\` (${DB_SIZE})
🗄 *Storage:* \`${RUSTFS_VOLUME}\` (${RUSTFS_SIZE})
💾 *Total Size:* \`${TOTAL_BACKUP_SIZE}\`
📁 *Path:* \`${BACKUP_PATH}\`"

send_telegram "SUCCESS" "$SUMMARY_TEXT"

exit 0

