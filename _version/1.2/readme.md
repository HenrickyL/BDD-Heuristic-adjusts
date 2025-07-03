
## AI Planner com BDDs – Versão 1.2

### Visão Geral

A versão 1.2 introduz uma refatoração significativa na estrutura de controle de estados para alinhamento com a teoria clássica de busca (A\*), conforme descrita no livro *Artificial Intelligence: A Modern Approach (AIMA)*.

### Principais Alterações

* **Refatoração completa para uso de `Node` no lugar de `BDD` cru** em estruturas como `FrontierQueue` e `ExploredVector`.

  * `Node` encapsula o estado (BDD), o custo total `f(n)`, e o pai, permitindo rastreamento de caminho.
* **Reescrita dos métodos `equals()` e `hashCode()`** na classe `Node`:

  * Considera `BDD` + `f(n)` na comparação.
  * Garante consistência para uso seguro em `HashSet`/`HashMap`.
* **Melhoria na função `splitByHeuristic(...)`**:

  * Quebra um `BDD` em múltiplas entradas no vetor heurístico.
  * Usa `rest = rest ∧ ¬matched` para evitar repetição.
* **Uso de média móvel para estimar o tamanho dos BDDs**:

  * Estimativa leve e adaptativa de uso de memória.
  * Exibição com `getSizeSummary()` em `FrontierQueue` e `ExploredVector`.

---

### Estruturas Reestruturadas

#### `FrontierQueue`

| Item               | Descrição                                                         |
| ------------------ | ----------------------------------------------------------------- |
| Tipo               | `PriorityQueue<Node>`                                             |
| Verificação rápida | `HashSet<Node>` com `equals()` e `hashCode()`                     |
| Substituição       | `replace(Node)` compara e substitui se `f(n)` menor               |
| Estimativa memória | Baseada no número médio de nós BDD (`nodeCount * 20B + overhead`) |

#### `ExploredVector`

| Item               | Descrição                                          |
| ------------------ | -------------------------------------------------- |
| Tipo               | `Vector<Node>`                                     |
| Verificação rápida | `HashSet<Node>`                                    |
| Estimativa memória | Similar ao `FrontierQueue`, com média móvel de BDD |

---

### Resumo de Tamanho e Uso (com `getSizeSummary()`)

Estes métodos mostram um panorama leve do uso de memória estimado:

```java
System.out.println(frontier.getSizeSummary());
System.out.println(explored.getSizeSummary());
```

#### Exemplo de saída:

```
[Frontier] Nodes: 120, Hashes: 120, Est. Memory: 2400.00 KB
[Explored] BDDs: 500, Hashes: 500, Est. Memory: 9600.00 KB
```

---

### 💡 Observações

* A substituição de `Integer hashCode` por `Node` em `HashSet` **aumenta o uso de memória**, mas garante semântica e segurança.
* Essa versão **ainda não** utiliza controle refinado de reutilização de estados (como uma memória de transições), mas se prepara para isso.
* O controle de tempo permanece via `TimeManager`, e a liberação de memória (`.free()`) foi aplicada em todos os lugares possíveis.

---

### 📌 Próximos Passos (v1.3)

* Introdução do `SearchMemory` para análise precisa de consumo.
* Otimizações em tempo real de poda de estados duplicados.
* Comparações empíricas entre versão 1.1 (hashes simples) e 1.2 (nodes) em tempo e espaço.
