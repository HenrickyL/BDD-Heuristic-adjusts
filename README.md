# Planejador BDD para Problemas de Planejamento com Busca Heurística e Exaustiva

Este projeto implementa um planejador simbólico para problemas clássicos de planejamento em Inteligência Artificial, utilizando Diagramas de Decisão Binária (BDDs). Ele permite a execução de buscas **forward (exaustiva)** e **backward + forward (heurística)** sobre domínios modelados em arquivos `.txt`.

---

## 📁 Estrutura do Projeto

```
src/main/
└── java/org/ufc/planner/
├───── app/                    # Classe principal (Main)
├───── controller/             # Controladores da execução
├───── core/                   # Interface + implementações de busca
├───── domain/                 # Entidades do problema (ações, nós, enums)
├───── infrastructure/         # Leitura de arquivos e controle de tempo
└── resources/
└───── problems/               # Casos de teste (logistics, rovers, etc.)
```
---

## ⚙️ Requisitos

- Java 23+
- Maven 3+
- **Recomendado:** IntelliJ IDEA para rodar facilmente com configurações personalizadas de memória.

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

## 🚀 Como executar (`MVN`)

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
mvn exec:java
```
|💡 Dica: Recomendamos usar o IntelliJ IDEA com uma configuração de execução personalizada, incluindo parâmetros de memória via VM options (ex:-Xmx6g).

| Para mais detalhes sobre esse processo e como testar os limites de memória do projeto, consulte o arquivo [java-memory-consideration.md](doc/java-memory-consideration.md).

## Gerar `.jar` pra executar via terminal

1. ` mvn clean package`
2. gera o arquivo `target/ai-planner-1.0-SNAPSHOT-jar-with-dependencies.jar`
3. Rode usando: `java -Xmx12g -Xms2g -jar target/ai-planner-1.0-SNAPSHOT-jar-with-dependencies.jar <type> <search> <test>`

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


