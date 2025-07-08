#!/bin/bash
# RUN ===================
# # Rodar só a partição logistics 8
#!bash drive/MyDrive/TCC/Run.sh GUI_v1_2_1.jar 900000 1800000 logi2

# Rodar todos
#!bash drive/MyDrive/TCC/Run.sh GUI_v1_2_1.jar 900000 1800000 all

# =================== CONFIG =====================
PATH="drive/MyDrive/TCC"
JAR="$1"
BKW_TIME=${2:-300000}
FRW_TIME=${3:-300000}
PARTITION=${4:-all}
MEM_MAX=${5:-11g}
MEM_MIN=${6:-11g}
RESULT_BASE="$PATH/logs"
# VERSION="v1_2_1"

# Métodos heurísticos
searchMethods=("OLD_TIME" "NEW" "OLD")

# ============== TESTES POR PARTIÇÃO ==============
rover1=(1 2 3 4)
rover2=(5 6)
rover3=(7 8)

logi1=(4 6)
logi2=(8 10)
logi3=(12 14)

# ============== FUNÇÕES ==============

run_heuristics() {
    local domain=$1
    local testId=$2
    for method in "${searchMethods[@]}"; do
        out="$RESULT_BASE/heuristic_${domain}_${testId}_${method}.txt"
        echo "🔍 Heurístico $method → $domain $testId"
        time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar "$PATH/$JAR" "$domain" heuristic "$testId" "$BKW_TIME" "$FRW_TIME" "$method" > "$out" 2>&1
    done
}

run_exhaustive() {
    local domain=$1
    local testId=$2
    out="$RESULT_BASE/exhaustive/exhaustive_${domain}_${testId}.txt"
    echo "📊 Exaustivo → $domain $testId"
    time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar "$PATH/$JAR" "$domain" exhaustive "$testId" "$BKW_TIME" "$FRW_TIME" > "$out" 2>&1
}

# backup_results() {
#     echo "💾 Salvando backup..."
#     cp -r /content/results "/content/drive/MyDrive/TCC/$VERSION/${PARTITION}/"
#     cp -r "$RESULT_BASE" "/content/drive/MyDrive/TCC/$VERSION/${PARTITION}/"
# }

run_partition() {
    local domain=$1
    shift
    local tests=("$@")
    for t in "${tests[@]}"; do
        run_heuristics "$domain" "$t"
    done
    # backup_results
}

# ============== EXECUÇÃO ==============

echo "🚀 Rodando partição: $PARTITION"

case "$PARTITION" in
    rover1) run_partition rovers "${rover1[@]}" ;;
    rover2) run_partition rovers "${rover2[@]}" ;;
    rover3) run_partition rovers "${rover3[@]}" ;;
    logi1)  run_partition logistics "${logi1[@]}" ;;
    logi2)  run_partition logistics "${logi2[@]}" ;;
    logi3)  run_partition logistics "${logi3[@]}" ;;
    all)
        for part in rover1 rover2 rover3 rover4 logi1 logi2 logi3 logi4 logi5; do
            bash "$0" "$JAR" "$BKW_TIME" "$FRW_TIME" "$part"
        done
        ;;
    *)
        echo "❌ Partição inválida: $PARTITION"
        echo "Use: rover1, rover2, ..., logi1, logi2, ..., all"
        exit 1
        ;;
esac
