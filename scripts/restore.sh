#!/usr/bin/env bash
# ==============================================================================
# 360 Phone Shop Management System - Restore Script
# ==============================================================================
# Usage:
#   ./restore.sh list                         -> Lists available backup points
#   ./restore.sh db <path_to_sql.gz>          -> Restores PostgreSQL database
#   ./restore.sh storage <path_to_tar.gz>     -> Restores RustFS S3 data
#   ./restore.sh all <backup_folder_path>     -> Restores both DB and S3 storage
# ==============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Load .env file if available
ENV_FILE="${PROJECT_DIR}/.env"
if [ -f "$ENV_FILE" ]; then
  set -a
  # shellcheck disable=SC1090
  source <(grep -E '^[A-Za-z_][A-Za-z0-9_]*=' "$ENV_FILE" | sed 's/\r$//')
  set +a
fi

POSTGRES_CONTAINER="${POSTGRES_CONTAINER:-360-postgres}"
POSTGRES_USER="${POSTGRES_USER:-postgres}"
POSTGRES_DB="${POSTGRES_DB:-db_360ff}"
RUSTFS_VOLUME="${RUSTFS_VOLUME:-360_phone_shop_rustfs_data}"
BACKUP_ROOT="${BACKUP_DIR:-${PROJECT_DIR}/backups}"

usage() {
  echo "Usage:"
  echo "  $0 list                                  # List all available backup points"
  echo "  $0 db <path_to_postgres_backup.sql.gz>   # Restore database only"
  echo "  $0 storage <path_to_rustfs_data.tar.gz>  # Restore RustFS files only"
  echo "  $0 all <backup_directory_path>           # Restore entire backup point"
  exit 1
}

# ------------------------------------------------------------------------------
# Action: List Backups
# ------------------------------------------------------------------------------
if [ "${1:-}" = "list" ] || [ -z "${1:-}" ]; then
  echo "======================================================"
  echo "Available Backup Points in: $BACKUP_ROOT"
  echo "======================================================"
  if [ ! -d "$BACKUP_ROOT" ] || [ -z "$(ls -A "$BACKUP_ROOT" 2>/dev/null)" ]; then
    echo "No backups found."
    exit 0
  fi
  
  find "$BACKUP_ROOT" -mindepth 1 -maxdepth 1 -type d -name "20*" | sort -r | while read -r dir; do
    name=$(basename "$dir")
    size=$(du -sh "$dir" | cut -f1)
    echo "• $name (Total size: $size)"
    ls -lh "$dir" | awk 'NR>1 {print "   - " $9 " (" $5 ")"}'
  done
  exit 0
fi

# ------------------------------------------------------------------------------
# Helper: Confirm Action
# ------------------------------------------------------------------------------
confirm() {
  read -r -p "⚠️ WARNING: This will overwrite current data. Are you sure? [y/N] " response
  case "$response" in
    [yY][eE][sS]|[yY]) ;;
    *) echo "Operation cancelled."; exit 0 ;;
  esac
}

# ------------------------------------------------------------------------------
# Action: Restore Database
# ------------------------------------------------------------------------------
restore_db() {
  local file="$1"
  if [ ! -f "$file" ]; then
    echo "Error: Database backup file '$file' not found!" >&2
    exit 1
  fi

  confirm
  echo "Restoring PostgreSQL database '$POSTGRES_DB' from '$file'..."
  
  gunzip -c "$file" | docker exec -i "$POSTGRES_CONTAINER" psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"
  echo "✓ Database restored successfully!"
}

# ------------------------------------------------------------------------------
# Action: Restore RustFS Storage
# ------------------------------------------------------------------------------
restore_storage() {
  local file="$1"
  if [ ! -f "$file" ]; then
    echo "Error: Storage backup file '$file' not found!" >&2
    exit 1
  fi

  confirm
  echo "Restoring RustFS storage volume '$RUSTFS_VOLUME' from '$file'..."

  # Stop rustfs temporarily to avoid file locks
  docker compose -f "${PROJECT_DIR}/docker-compose.yml" stop rustfs 2>/dev/null || true

  docker run --rm \
    -v "${RUSTFS_VOLUME}:/target" \
    -v "$(dirname "$file"):/source:ro" \
    alpine sh -c "rm -rf /target/* && tar -xzf /source/$(basename "$file") -C /target"

  docker compose -f "${PROJECT_DIR}/docker-compose.yml" start rustfs 2>/dev/null || true
  echo "✓ Storage restored and RustFS restarted!"
}

# ------------------------------------------------------------------------------
# Main Dispatcher
# ------------------------------------------------------------------------------
COMMAND="$1"

case "$COMMAND" in
  db)
    [ -z "${2:-}" ] && usage
    restore_db "$2"
    ;;
  storage)
    [ -z "${2:-}" ] && usage
    restore_storage "$2"
    ;;
  all)
    [ -z "${2:-}" ] && usage
    DIR="$2"
    if [ ! -d "$DIR" ]; then
      echo "Error: Directory '$DIR' not found!" >&2
      exit 1
    fi
    DB_FILE=$(find "$DIR" -type f -name "postgres_*.sql.gz" | head -n 1)
    STORAGE_FILE=$(find "$DIR" -type f -name "rustfs_data_*.tar.gz" | head -n 1)

    if [ -n "$DB_FILE" ]; then
      restore_db "$DB_FILE"
    else
      echo "No database backup found in $DIR"
    fi

    if [ -n "$STORAGE_FILE" ]; then
      restore_storage "$STORAGE_FILE"
    else
      echo "No storage backup found in $DIR"
    fi
    ;;
  *)
    usage
    ;;
esac
