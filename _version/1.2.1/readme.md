
## AI Planner com BDDs – Versão 1.2.1

### Visão Geral

A versão 1.2.1 Ajhusta os explorados para use de apenas `HashSet<Node>` dos hash dos nós. Adicão de controle de memória e print do sumários.

### Principais Alterações

* **Ajuste no `ExploredVector`: Remoção do vetor e uso de  `HashSet<Node>`**

* **Controle de memória e adição dos summaries:**:
```
SUMMARY:
-⏱️ [TIME] Elapsed: 1063 ms
-💾 [MEMORY] Elapsed: 0.205 MB
```

---

### Estruturas Reestruturadas

#### `ExploredVector`

| Item               | Descrição                                          |
| ------------------ | -------------------------------------------------- |
| Tipo               | `HashSet<Node>`                                     |
| Estimativa memória | Proximo do que Frontier tem|

---

### Resumo de Tamanho e Uso (com `getSizeSummary()`)

Estes métodos mostram um panorama leve do uso de memória estimado:

```java
System.out.println(frontier.getSizeSummary());
System.out.println(explored.getSizeSummary());
```

#### Exemplo de saída:

```
[Frontier] Nodes: 16, Est.Memory: 13674,69 KB | nodeIndex: 16, Est.Memory: 0,06 KB
[Explored] Nodes: 14, Est. Memory: 14735,00 KB
```
