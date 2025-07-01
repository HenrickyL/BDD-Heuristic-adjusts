#!/bin/bash

# Configurações do caminho
PATH="drive/MyDrive/"
JAR="GUI_v1_1.jar" #"ai-planner-1.0-SNAPSHOT-jar-with-dependencies.jar"

# Parâmetros de execução
maxTime=${1:-1200000}
MEM_MAX="11g"
MEM_MIN="11g"

echo "Run with time: $maxTime"

# Garante diretório para os resultados
mkdir -p res

# Métodos heurísticos na ordem desejada
searchMethods=("OLD_TIME" "NEW" "OLD")

# Listas de testes
roverFastTests=(1 2 3 4)
roverSlowTests=(5 7 6 8)
logisticFastTests=(4 6)
logisticSlowTests=(8 10 12 14)

# -------------------------------------------------------------------------
# Função para rodar heurísticos
run_heuristics() {
    local domain=$1
    local testId=$2

    for method in "${searchMethods[@]}"; do
        echo "🔍 Heurístico $method → $domain $testId"
        out="$PATH/res/heuristic_${domain}_${testId}_${method}.txt"
        time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar "$PATH/$JAR" "$domain" heuristic "$testId" "$maxTime" "$method" > "$out" 2>&1
    done
}

# Função para rodar exaustivo
run_exaustive() {
    local domain=$1
    local testId=$2

    echo "📊 Exaustivo → $domain $testId"
    out="$PATH/res/exaustive_${domain}_${testId}_t-.txt"
    time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar "$PATH/$JAR" "$domain" exaustive "$testId" "$maxTime" > "$out" 2>&1
}
# -------------------------------------------------------------------------

# Etapa 1: Rodar todos os heurísticos --- --- --- FAST ---
for i in "${roverFastTests[@]}"; do
    run_heuristics rovers "$i"
done

for i in "${logisticFastTests[@]}"; do
    run_heuristics logistics "$i"
done

for i in "${roverSlowTests[@]}"; do
    run_heuristics rovers "$i"
done

for i in "${logisticSlowTests[@]}"; do
    run_heuristics logistics "$i"
done
# -----  ----- ----- ----- ----- -----


# Etapa 2: Rodar todos os exaustivos  --- --- --- SLOW ---
for i in "${roverFastTests[@]}"; do
    run_exaustive rovers "$i"
done

for i in "${logisticFastTests[@]}"; do
    run_exaustive logistics "$i"
done

for i in "${roverSlowTests[@]}"; do
    run_exaustive rovers "$i"
done

for i in "${logisticSlowTests[@]}"; do
    run_exaustive logistics "$i"
done


# # Rovers - Testes Rápidos
# for i in "${roverFastTests[@]}"; do
#     echo "Executando teste exaustivo rápido para o problema rover $i T: $maxTime..."
#     time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar $PATH/$JAR rovers exaustive $i $maxTime > $PATH/res/exaustive_rover_$i.txt 2>&1
#     # jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."

#     echo "Executando teste heurístico rápido para o problema rover $i T: $maxTime..."
#     time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar $PATH/$JAR rovers heuristic $i $maxTime > $PATH/res/heuristic_rover_$i.txt 2>&1
#     # jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."
# done

# # Logística - Testes Rápidos
# for i in "${logisticFastTests[@]}"; do
#     echo "Executando teste exaustivo rápido para o problema logístico $i T: $maxTime..."
#     time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar $PATH/$JAR logistics exaustive $i $maxTime > $PATH/res/exaustive_logistic_$i.txt 2>&1
#     # jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."

#     echo "Executando teste heurístico rápido para o problema logístico $i T: $maxTime..."
#     time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar $PATH/$JAR logistics heuristic $i $maxTime > $PATH/res/heuristic_logistic_$i.txt 2>&1
#     # jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."
# done

# # Logística - Testes Lentos
# for i in "${logisticSlowTests[@]}"; do
#     echo "Executando teste heurístico lento para o problema logístico $i T: $maxTime..."
#     time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar $PATH/$JAR logistics heuristic $i $maxTime > $PATH/res/heuristic_logistic_$i.txt 2>&1
#     # jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."

#     echo "Executando teste exaustivo lento para o problema logístico $i T: $maxTime..."
#     time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar $PATH/$JAR logistics exaustive $i $maxTime > $PATH/res/exaustive_logistic_$i.txt 2>&1
#     # jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."
# done

# # Rovers - Testes Lentos
# for i in "${roverSlowTests[@]}"; do
#     echo "Executando teste heurístico lento para o problema rover $i T: $maxTime..."
#     time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar $PATH/$JAR rovers heuristic $i $maxTime > $PATH/res/heuristic_rover_$i.txt 2>&1
#     # jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."

#     echo "Executando teste exaustivo lento para o problema rover $i T: $maxTime..."
#     time /usr/bin/java -Xmx$MEM_MAX -Xms$MEM_MIN -jar $PATH/$JAR rovers exaustive $i $maxTime > $PATH/res/exaustive_rover_$i.txt 2>&1
#     # jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."
# done
