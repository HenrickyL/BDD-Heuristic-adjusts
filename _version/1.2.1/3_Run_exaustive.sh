# Configurações
PATH="drive/MyDrive/TCC"
JAR="$1"
BKW_TIME=${2:-300000}
FRW_TIME=${3:-300000}

MEM_MAX="11g"
MEM_MIN="11g"
RESULT_BASE="$PATH/logs"


# Listas de testes
roverFastTests=(1 2 3 4)
roverSlowTests=(5 7 6 8)
logisticFastTests=(4 6)
logisticSlowTests=(8 10 12 14)

# Métodos heurísticos
searchMethods=("OLD_TIME" "NEW" "OLD")


# Função para rodar heurísticos
run_heuristics() {
    local domain=$1
    local testId=$2

    for method in "${searchMethods[@]}"; do
        out="$RESULT_BASE/heuristic_${domain}_${testId}_${method}.txt"
        
        echo "🔍 $stage: Heurístico $method → $domain $testId"
        time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar "$PATH/$JAR" "$domain" heuristic "$testId" "$BKW_TIME" "$FRW_TIME" "$method" > "$out" 2>&1

    done
}

# Função para rodar exaustivo
run_exhaustive() {
    local domain=$1
    local testId=$2
    
    out="$RESULT_BASE/exhaustive/exhaustive_${domain}_${testId}.txt"
    
    echo "📊 Exaustivo → $domain $testId"
    time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar "$PATH/$JAR" "$domain" exhaustive "$testId" "$BKW_TIME" "$FRW_TIME"  > "$out" 2>&1
}

# # ------------------------- ETAPA 1: Testes Rápidos -------------------------
# echo "🚀 Iniciando Etapa 1: Testes Rápidos (Heurísticos)"
# for i in "${roverFastTests[@]}"; do
#     run_heuristics rovers "$i" fast
# done

# for i in "${logisticFastTests[@]}"; do
#     run_heuristics logistics "$i" fast
# done


# # ------------------------- ETAPA 2: Testes Lentos -------------------------
# echo "🐢 Iniciando Etapa 2: Testes Lentos (Heurísticos)"
# for i in "${roverSlowTests[@]}"; do
#     run_heuristics rovers "$i" slow
# done

# for i in "${logisticSlowTests[@]}"; do
#     run_heuristics logistics "$i" slow
# done


# ------------------------- ETAPA 3: Testes Exaustivos -------------------------
echo "🧪 Iniciando Etapa 3: Testes Exaustivos"

# Fast tests
for i in "${roverFastTests[@]}"; do
    run_exhaustive rovers "$i"
done

for i in "${logisticFastTests[@]}"; do
    run_exhaustive logistics "$i"
done


# Slow tests
for i in "${roverSlowTests[@]}"; do
    run_exhaustive rovers "$i"
done

for i in "${logisticSlowTests[@]}"; do
    run_exhaustive logistics "$i"
done


echo "🎉 Todas as etapas concluídas com sucesso!"