#!/usr/bin/env bash
set -euo pipefail

require_cmd() {
  local cmd="$1"
  if ! command -v "${cmd}" >/dev/null 2>&1; then
    echo "[error] command not found: ${cmd}"
    exit 1
  fi
}

require_java17() {
  local version_line
  local version_raw
  local major

  version_line="$(java -version 2>&1 | head -n 1)"
  version_raw="$(echo "${version_line}" | sed -n 's/.*"\([^"]*\)".*/\1/p')"

  if [[ "${version_raw}" == 1.* ]]; then
    major="$(echo "${version_raw}" | cut -d. -f2)"
  else
    major="$(echo "${version_raw}" | cut -d. -f1)"
  fi

  if [[ -z "${major}" ]] || [[ "${major}" -lt 17 ]]; then
    echo "[error] Java 17+ is required, current version line: ${version_line}"
    exit 1
  fi
}

is_running_pid() {
  local pid="$1"
  [[ -n "${pid}" ]] && kill -0 "${pid}" >/dev/null 2>&1
}

health_up() {
  local port="$1"
  local url="http://127.0.0.1:${port}/actuator/health"

  if command -v curl >/dev/null 2>&1; then
    curl -fsS "${url}" 2>/dev/null | grep -q '"status":"UP"'
    return $?
  fi

  if command -v wget >/dev/null 2>&1; then
    wget -qO- "${url}" 2>/dev/null | grep -q '"status":"UP"'
    return $?
  fi

  return 1
}

start_service() {
  local module="$1"
  local port="$2"
  local pid_file="${RUN_DIR}/${module}.pid"
  local log_file="${LOG_DIR}/${module}.log"
  local jar_file
  local timeout="${START_TIMEOUT_SECONDS:-90}"

  mkdir -p "${RUN_DIR}" "${LOG_DIR}"

  if [[ -f "${pid_file}" ]]; then
    local old_pid
    old_pid="$(cat "${pid_file}")"
    if is_running_pid "${old_pid}"; then
      echo "[skip] ${module} is already running (pid=${old_pid})"
      return 0
    fi
    rm -f "${pid_file}"
  fi

  jar_file="$(find "${APP_ROOT}/${module}/target" -maxdepth 1 -type f -name "${module}-*.jar" ! -name "original-*.jar" | head -n 1 || true)"
  if [[ -z "${jar_file}" ]]; then
    echo "[fail] runnable jar not found for ${module}"
    echo "[hint] run prepare script first to build jars"
    echo "[hint] expected path pattern: ${APP_ROOT}/${module}/target/${module}-*.jar"
    return 1
  fi

  echo "[start] ${module} (port=${port}, jar=${jar_file})"
  nohup env JAVA_OPTS="${JAVA_OPTS:-}" bash -lc "cd \"${APP_ROOT}\" && java \$JAVA_OPTS -jar \"${jar_file}\"" >>"${log_file}" 2>&1 &
  local pid=$!
  echo "${pid}" >"${pid_file}"

  for ((i = 1; i <= timeout; i++)); do
    if ! is_running_pid "${pid}"; then
      echo "[fail] ${module} exited unexpectedly, see: ${log_file}"
      tail -n 40 "${log_file}" || true
      return 1
    fi
    if health_up "${port}"; then
      echo "[ok] ${module} is UP"
      return 0
    fi
    sleep 1
  done

  echo "[warn] ${module} started but health check timed out (${timeout}s), see: ${log_file}"
  return 1
}

stop_service() {
  local module="$1"
  local pid_file="${RUN_DIR}/${module}.pid"

  if [[ ! -f "${pid_file}" ]]; then
    echo "[skip] ${module} pid file not found"
    return 0
  fi

  local pid
  pid="$(cat "${pid_file}")"
  if ! is_running_pid "${pid}"; then
    echo "[skip] ${module} not running, cleanup stale pid"
    rm -f "${pid_file}"
    return 0
  fi

  echo "[stop] ${module} (pid=${pid})"
  kill "${pid}" >/dev/null 2>&1 || true
  for ((i = 1; i <= 20; i++)); do
    if ! is_running_pid "${pid}"; then
      rm -f "${pid_file}"
      echo "[ok] ${module} stopped"
      return 0
    fi
    sleep 1
  done

  echo "[warn] ${module} did not stop in time, force killing"
  kill -9 "${pid}" >/dev/null 2>&1 || true
  rm -f "${pid_file}"
}

status_service() {
  local module="$1"
  local port="$2"
  local pid_file="${RUN_DIR}/${module}.pid"

  local proc_state="DOWN"
  local health_state="DOWN"

  if [[ -f "${pid_file}" ]]; then
    local pid
    pid="$(cat "${pid_file}")"
    if is_running_pid "${pid}"; then
      proc_state="UP(pid=${pid})"
    fi
  fi

  if health_up "${port}"; then
    health_state="UP"
  fi

  echo "${module} | process=${proc_state} | health=${health_state} | port=${port}"
}
