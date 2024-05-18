#!/bin/bash

# Listas de testes rápidos e lentos para rovers e logística
roverFastTests=(1 2 3 4)
roverSlowTests=(5 7 6 8)
logisticFastTests=(4 6)
logisticSlowTests=(8 10 12 14)

# Executa os testes rápidos para os problemas rovers
for i in "${roverFastTests[@]}"; do
    echo "Executando teste exaustivo rápido para o problema rover $i..."
    time java -Xmx11g -Xms11g -jar GUI.jar exaustive rovers $i > exaustive_rover_$i.txt 2>&1
    echo "Limpando a memória..."
    jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."

    echo "Executando teste heurístico rápido para o problema rover $i..."
    time java -Xmx11g -Xms11g -jar GUI.jar heuristic rovers $i > heuristic_rover_$i.txt 2>&1
    echo "Limpando a memória..."
    jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."
done


# Executa os testes rápidos para os problemas logísticos
for i in "${logisticFastTests[@]}"; do
    echo "Executando teste exaustivo rápido para o problema logístico $i..."
    time java -Xmx11g -Xms11g -jar GUI.jar exaustive logistics $i > exaustive_logistic_$i.txt 2>&1
    echo "Limpando a memória..."
    jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."

    echo "Executando teste heurístico rápido para o problema logístico $i..."
    time java -Xmx11g -Xms11g -jar GUI.jar heuristic logistics $i > heuristic_logistic_$i.txt 2>&1
    echo "Limpando a memória..."
    jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."
done

# Executa os testes lentos para os problemas logísticos
for i in "${logisticSlowTests[@]}"; do
    echo "Executando teste heurístico lento para o problema logístico $i..."
    time java -Xmx11g -Xms11g -jar GUI.jar heuristic logistics $i > heuristic_logistic_$i.txt 2>&1
    echo "Limpando a memória..."
    jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."

    echo "Executando teste exaustivo lento para o problema logístico $i..."
    time java -Xmx11g -Xms11g -jar GUI.jar exaustive logistics $i > exaustive_logistic_$i.txt 2>&1
    echo "Limpando a memória..."
    jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."

   
done

# Executa os testes lentos para os problemas rovers
for i in "${roverSlowTests[@]}"; do
    echo "Executando teste heurístico lento para o problema rover $i..."
    time java -Xmx11g -Xms11g -jar GUI.jar heuristic rovers $i > heuristic_rover_$i.txt 2>&1
    echo "Limpando a memória..."
    jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."

    echo "Executando teste exaustivo lento para o problema rover $i..."
    time java -Xmx11g -Xms11g -jar GUI.jar exaustive rovers $i > exaustive_rover_$i.txt 2>&1
    echo "Limpando a memória..."
    jcmd $(pgrep java) GC.run || echo "Não foi possível limpar a memória."
done




