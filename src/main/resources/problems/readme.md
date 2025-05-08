### **Formato de Definição de Problemas**

Este formato combina as definições de domínio e problema, descritas de maneira hierárquica. Ele é inspirado pelo PDDL (*Planning Domain Definition Language*), uma linguagem padrão para representação de problemas em planejamento automatizado.

---

#### **Estrutura do PDDL**

1. **Domínio (`Domain`)**  
   Define os componentes gerais aplicáveis a múltiplos problemas. Inclui:
   - **Predicados (`<predicates>`):** Lista de propriedades ou relações válidas no domínio.
   - **Conjunto de ações (`<actionsSet>`):** Descrição das ações disponíveis, incluindo pré-condições (`<pre>`), efeitos positivos (`<pos>`) e efeitos negativos (`<neg>` se aplicável).

2. **Problema (`Problem`)**  
   Descreve os detalhes específicos de uma instância no domínio. Contém:
   - **Restrições (`<constraints>`):** Condições que devem ser satisfeitas no estado inicial.  
   - **Estado inicial (`<initial>`):** Predicados verdadeiros e falsos no início do problema.  
   - **Objetivo (`<goal>`):** Estado final desejado para resolver o problema.  

---

### **Exemplo - Mundo dos Blocos**

Abaixo, segue a especificação de um domínio e três problemas com dificuldades variadas no mundo dos blocos.

#### **Definição Geral - Domínio e Problema**
```plaintext
<predicates>
ON(x, y), ONTABLE(x), CLEAR(x), HANDEMPTY, HOLDING(x)
<\predicates>
<constraints>
HANDEMPTY, CLEAR(A), ON(A, B), ONTABLE(B)
<\constraints>
<initial>
HANDEMPTY, CLEAR(A), ON(A, B), ONTABLE(B), CLEAR(C), ONTABLE(C)
<\initial>
<goal>
ON(C, A), ON(A, B), ONTABLE(B)
<\goal>
<actionsSet>
<action>
<name>PICKUP(x)<\name>
<pre>CLEAR(x), ONTABLE(x), HANDEMPTY<\pre>
<pos>HOLDING(x), ~ONTABLE(x), ~CLEAR(x), ~HANDEMPTY<\pos>
<\action>
<action>
<name>PUTDOWN(x)<\name>
<pre>HOLDING(x)<\pre>
<pos>ONTABLE(x), CLEAR(x), HANDEMPTY, ~HOLDING(x)<\pos>
<\action>
<action>
<name>STACK(x, y)<\name>
<pre>HOLDING(x), CLEAR(y)<\pre>
<pos>ON(x, y), CLEAR(x), HANDEMPTY, ~HOLDING(x), ~CLEAR(y)<\pos>
<\action>
<action>
<name>UNSTACK(x, y)<\name>
<pre>ON(x, y), CLEAR(x), HANDEMPTY<\pre>
<pos>HOLDING(x), CLEAR(y), ~ON(x, y), ~HANDEMPTY<\pos>
<\action>
<\actionsSet>
```

---

#### **Problemas Variados**

##### Problema 1: Simples (Mover um bloco da mesa para outro bloco)
```plaintext
<initial>
HANDEMPTY, CLEAR(A), ONTABLE(A), CLEAR(B), ONTABLE(B)
<\initial>
<goal>
ON(A, B)
<\goal>
```

##### Problema 2: Médio (Construir uma torre de dois blocos)
```plaintext
<initial>
HANDEMPTY, CLEAR(A), ONTABLE(A), CLEAR(B), ONTABLE(B)
<\initial>
<goal>
ON(A, B), ONTABLE(B)
<\goal>
```

##### Problema 3: Complexo (Construir uma torre de três blocos)
```plaintext
<initial>
HANDEMPTY, CLEAR(A), ON(A, B), ONTABLE(B), CLEAR(C), ONTABLE(C)
<\initial>
<goal>
ON(C, A), ON(A, B), ONTABLE(B)
<\goal>
``` 

Este formato pode ser usado para resolver problemas utilizando planejadores automatizados, como sistemas baseados em busca no espaço de estados.