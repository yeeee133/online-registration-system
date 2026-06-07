#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$BASE_DIR"

JMETER_BIN="${JMETER_BIN:-/opt/homebrew/bin/jmeter}"
TEST_PLAN="${TEST_PLAN:-online_registration_performance_test_v2_demo.jmx}"
RUNS_DIR="${RUNS_DIR:-runs}"
TS="$(date +%Y%m%d_%H%M%S)"
RUN_DIR="$RUNS_DIR/$TS"

# Common runtime parameters (can be overridden by env)
BASE_URL="${BASE_URL:-localhost}"
PORT="${PORT:-8080}"
PROTOCOL="${PROTOCOL:-http}"
ACTIVITY_ID="${ACTIVITY_ID:-1}"
LOGIN_USERNAME="${LOGIN_USERNAME:-admin}"
LOGIN_PASSWORD="${LOGIN_PASSWORD:-123456}"
STUDENTS_FILE="${STUDENTS_FILE:-students_demo.csv}"
SCENE1_THREADS="${SCENE1_THREADS:-30}"
SCENE1_RAMP="${SCENE1_RAMP:-15}"
SCENE1_LOOPS="${SCENE1_LOOPS:-15}"
SCENE2_THREADS="${SCENE2_THREADS:-150}"
SCENE2_RAMP="${SCENE2_RAMP:-30}"
SCENE2_LOOPS="${SCENE2_LOOPS:-2}"

mkdir -p "$RUN_DIR"

if [[ ! -f "$TEST_PLAN" ]]; then
  echo "ERROR: test plan not found: $TEST_PLAN" >&2
  exit 1
fi
if [[ ! -f "$STUDENTS_FILE" ]]; then
  echo "ERROR: students file not found: $STUDENTS_FILE" >&2
  exit 1
fi
if [[ ! -x "$JMETER_BIN" ]]; then
  echo "ERROR: jmeter binary not executable: $JMETER_BIN" >&2
  exit 1
fi

echo "[INFO] Running JMeter test plan: $TEST_PLAN"
echo "[INFO] Output directory: $RUN_DIR"
echo "[INFO] Runtime args: baseUrl=$BASE_URL port=$PORT protocol=$PROTOCOL activityId=$ACTIVITY_ID studentsFile=$STUDENTS_FILE"
echo "[INFO] Scene1 args: threads=$SCENE1_THREADS ramp=$SCENE1_RAMP loops=$SCENE1_LOOPS"
echo "[INFO] Scene2 args: threads=$SCENE2_THREADS ramp=$SCENE2_RAMP loops=$SCENE2_LOOPS"

"$JMETER_BIN" -n \
  -t "$TEST_PLAN" \
  -l "$RUN_DIR/result.jtl" \
  -j "$RUN_DIR/jmeter.log" \
  -e -o "$RUN_DIR/report" \
  -JbaseUrl="$BASE_URL" \
  -Jport="$PORT" \
  -Jprotocol="$PROTOCOL" \
  -JactivityId="$ACTIVITY_ID" \
  -JloginUsername="$LOGIN_USERNAME" \
  -JloginPassword="$LOGIN_PASSWORD" \
  -JstudentsFile="$STUDENTS_FILE" \
  -Jscene1Threads="$SCENE1_THREADS" \
  -Jscene1Ramp="$SCENE1_RAMP" \
  -Jscene1Loops="$SCENE1_LOOPS" \
  -Jscene2Threads="$SCENE2_THREADS" \
  -Jscene2Ramp="$SCENE2_RAMP" \
  -Jscene2Loops="$SCENE2_LOOPS" \
  | tee "$RUN_DIR/run.log"

cp -f "$TEST_PLAN" "$RUN_DIR/"
cp -f "$STUDENTS_FILE" "$RUN_DIR/"

STATS_FILE="$RUN_DIR/report/statistics.json"
if [[ ! -f "$STATS_FILE" ]]; then
  echo "ERROR: report statistics missing: $STATS_FILE" >&2
  exit 1
fi

extract_metric() {
  local file="$1"
  local metric="$2"
  awk -v m="$metric" '
    /"Total"[[:space:]]*:[[:space:]]*\{/ {in_total=1; next}
    in_total && /\}/ {in_total=0}
    in_total && $0 ~ "\""m"\"" {
      gsub(/,/, "", $2)
      gsub(/^[[:space:]]+|[[:space:]]+$/, "", $2)
      print $2
      exit
    }
  ' "$file"
}

TOTAL_SAMPLES="$(extract_metric "$STATS_FILE" sampleCount)"
TOTAL_ERRORS="$(extract_metric "$STATS_FILE" errorCount)"
ERROR_PCT="$(extract_metric "$STATS_FILE" errorPct)"
MEAN_RT="$(extract_metric "$STATS_FILE" meanResTime)"
P95_RT="$(extract_metric "$STATS_FILE" pct2ResTime)"
THROUGHPUT="$(extract_metric "$STATS_FILE" throughput)"

SUMMARY_FILE="$RUN_DIR/summary.txt"
{
  echo "run_dir=$RUN_DIR"
  echo "test_plan=$TEST_PLAN"
  echo "sample_count=$TOTAL_SAMPLES"
  echo "error_count=$TOTAL_ERRORS"
  echo "error_pct=$ERROR_PCT"
  echo "mean_rt_ms=$MEAN_RT"
  echo "p95_rt_ms=$P95_RT"
  echo "throughput_rps=$THROUGHPUT"
} > "$SUMMARY_FILE"

PREV_RUN="$(ls -1dt "$RUNS_DIR"/* 2>/dev/null | sed -n '2p' || true)"
COMPARE_FILE="$RUN_DIR/compare_with_previous.txt"

if [[ -n "$PREV_RUN" && -f "$PREV_RUN/report/statistics.json" ]]; then
  PREV_STATS="$PREV_RUN/report/statistics.json"
  PREV_ERROR_PCT="$(extract_metric "$PREV_STATS" errorPct)"
  PREV_MEAN_RT="$(extract_metric "$PREV_STATS" meanResTime)"
  PREV_P95_RT="$(extract_metric "$PREV_STATS" pct2ResTime)"
  PREV_THROUGHPUT="$(extract_metric "$PREV_STATS" throughput)"

  {
    echo "current_run=$RUN_DIR"
    echo "previous_run=$PREV_RUN"
    echo "error_pct: current=$ERROR_PCT previous=$PREV_ERROR_PCT"
    echo "mean_rt_ms: current=$MEAN_RT previous=$PREV_MEAN_RT"
    echo "p95_rt_ms: current=$P95_RT previous=$PREV_P95_RT"
    echo "throughput_rps: current=$THROUGHPUT previous=$PREV_THROUGHPUT"
  } > "$COMPARE_FILE"
else
  echo "No previous run found for comparison." > "$COMPARE_FILE"
fi

echo "[INFO] Done"
echo "[INFO] JTL: $RUN_DIR/result.jtl"
echo "[INFO] JMeter log: $RUN_DIR/jmeter.log"
echo "[INFO] Console log: $RUN_DIR/run.log"
echo "[INFO] HTML report: $RUN_DIR/report/index.html"
echo "[INFO] Summary: $SUMMARY_FILE"
echo "[INFO] Compare: $COMPARE_FILE"
