# Planejador BDD para Problemas de Planejamento com Busca Heurística e Exaustiva

Este projeto implementa um planejador simbólico para problemas clássicos de planejamento em Inteligência Artificial, utilizando Diagramas de Decisão Binária (BDDs). Ele permite a execução de buscas **forward (exaustiva)** e **backward + forward (heurística)** sobre domínios modelados em arquivos `.txt`.

---

## 📁 Estrutura do Projeto

```
src/main/java/org/ufc/
└── planner/
├─── app/                    # Classe principal (Main)
├─── controller/             # Controladores da execução
├─── core/                   # Interface + implementações de busca
├─── domain/                 # Entidades do problema (ações, nós, enums)
├─── infrastructure/         # Leitura de arquivos e controle de tempo
├─── problems/               # Casos de teste (logistics, rovers, etc.)
└─── results/                # Resultados gerados (.txt)
```
---

## ⚙️ Requisitos

- Java 17+
- Maven 3+

---

## 📦 Dependências

O projeto utiliza:

```xml
<dependencies>
    <dependency>
        <groupId>net.sf.javabdd</groupId>
        <artifactId>javabdd</artifactId>
        <version>1.0b2</version>
    </dependency>
</dependencies>
```
---

## 🚀 Como executar

### 1. Clone o repositório
```bash
git clone https://github.com/HenrickyL/AI-Planner-BDD
cd AI-Planner-BDD
```

### 2. Compile o projeto e baixe as dependências
Certifique-se de ter o Java JDK 17+ e Maven 3+ instalados.
```bash
mvn clean compile
```
| Esse comando irá baixar automaticamente as dependências (como `javabdd`) e compilar o projeto.

### 3. Execute a aplicação
```bash
mvn exec:java -Dexec.mainClass="org.ufc.planner.app.Main"
```
---

## 🧠 Arquitetura e Conceitos

O sistema segue uma arquitetura modular inspirada em MVC, com separação entre:

- **Camada de Controle (`controller`)**: organiza os experimentos, seleciona os problemas e aplica os métodos de busca.
- **Camada de Infraestrutura (`infrastructure`)**: inclui `ModelReader` (interpretação dos arquivos de entrada) e `TimeManager` (controle de tempo).
- **Camada de Busca (`core`)**: define a interface `SearchAlgorithm` e as implementações `SearchOldMethod` e `SearchNewMethod`.

### 🔍 Planejamento com BDDs

Os Diagramas de Decisão Binária (BDDs) são estruturas compactas que representam conjuntos de estados. Esse projeto usa BDDs para representar estados iniciais, metas, ações e restrições.

- **Busca Forward (Exaustiva)**: explora estados a partir do estado inicial até atingir a meta.
- **Busca Backward (Heurística)**: regressa a partir da meta, propagando heurísticas com base nos estados atingidos, e depois aplica busca forward guiada por essa heurística (similar ao A\*).

---

## 🧪 Exemplos de problemas

Os arquivos `.txt` contendo os domínios (rovers, logistics, block-world) devem estar em:

```
src/main/java/org/ufc/planner/problems/
```

E seguir o formato usado pelo `ModelReader`.

---

## 📝 Licença

Projeto acadêmico para fins de estudo.

---


