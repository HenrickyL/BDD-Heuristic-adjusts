### Domain do Mundo dos Blocos

**Predicados:**
- `ON(x, y)`: O bloco `x` está sobre o bloco ou mesa `y`.
- `CLEAR(x)`: O topo do bloco ou mesa `x` está livre.
- `HOLDING(x)`: O robô está segurando o bloco `x`.
- `ARMEMPTY`: O robô não está segurando nenhum bloco.

**Ações:**
1. **PICKUP(x)**  
   Pré-condições: `CLEAR(x)`, `ON(x, y)`, `ARMEMPTY`  
   Efeitos Positivos: `HOLDING(x)`, `CLEAR(y)`  
   Efeitos Negativos: `ON(x, y)`, `ARMEMPTY`

2. **PUTDOWN(x)**  
   Pré-condições: `HOLDING(x)`  
   Efeitos Positivos: `ON(x, mesa)`, `CLEAR(x)`, `ARMEMPTY`  
   Efeitos Negativos: `HOLDING(x)`

3. **STACK(x, y)**  
   Pré-condições: `HOLDING(x)`, `CLEAR(y)`  
   Efeitos Positivos: `ON(x, y)`, `CLEAR(x)`, `ARMEMPTY`  
   Efeitos Negativos: `HOLDING(x)`, `CLEAR(y)`

4. **UNSTACK(x, y)**  
   Pré-condições: `ON(x, y)`, `CLEAR(x)`, `ARMEMPTY`  
   Efeitos Positivos: `HOLDING(x)`, `CLEAR(y)`  
   Efeitos Negativos: `ON(x, y)`, `CLEAR(x)`

---

### Problemas

1. **Problema Simples: Empilhar Dois Blocos**  
   **Estado Inicial:**  
   - `ON(A, mesa)`, `ON(B, mesa)`, `CLEAR(A)`, `CLEAR(B)`, `ARMEMPTY`  
   **Objetivo:**  
   - `ON(A, B)`, `ON(B, mesa)`, `CLEAR(A)`

2. **Problema Médio: Trocar a Ordem de Dois Blocos**  
   **Estado Inicial:**  
   - `ON(A, B)`, `ON(B, mesa)`, `CLEAR(A)`, `ARMEMPTY`  
   **Objetivo:**  
   - `ON(B, A)`, `ON(A, mesa)`, `CLEAR(B)`

3. **Problema Difícil: Empilhar Três Blocos em Ordem**  
   **Estado Inicial:**  
   - `ON(A, mesa)`, `ON(B, mesa)`, `ON(C, mesa)`, `CLEAR(A)`, `CLEAR(B)`, `CLEAR(C)`, `ARMEMPTY`  
   **Objetivo:**  
   - `ON(A, B)`, `ON(B, C)`, `ON(C, mesa)`, `CLEAR(A)`

---

### Arquivos no Formato Solicitado

#### Domain
```plaintext
<predicates>
ON(x, y), CLEAR(x), HOLDING(x), ARMEMPTY
<\predicates>
<actionsSet>
<action>
<name>PICKUP(x)<\name>
<pre>CLEAR(x), ON(x, y), ARMEMPTY<\pre>
<pos>HOLDING(x), CLEAR(y)<\pos>
<neg>ON(x, y), ARMEMPTY<\neg>
<\action>
<action>
<name>PUTDOWN(x)<\name>
<pre>HOLDING(x)<\pre>
<pos>ON(x, mesa), CLEAR(x), ARMEMPTY<\pos>
<neg>HOLDING(x)<\neg>
<\action>
<action>
<name>STACK(x, y)<\name>
<pre>HOLDING(x), CLEAR(y)<\pre>
<pos>ON(x, y), CLEAR(x), ARMEMPTY<\pos>
<neg>HOLDING(x), CLEAR(y)<\neg>
<\action>
<action>
<name>UNSTACK(x, y)<\name>
<pre>ON(x, y), CLEAR(x), ARMEMPTY<\pre>
<pos>HOLDING(x), CLEAR(y)<\pos>
<neg>ON(x, y), CLEAR(x)<\neg>
<\action>
<\actionsSet>
```

#### Problema 1: Empilhar Dois Blocos
```plaintext
<predicates>
ON(A, mesa), ON(B, mesa), CLEAR(A), CLEAR(B), ARMEMPTY
<\predicates>
<constraints>
ON(A, mesa), ON(B, mesa), ARMEMPTY
<\constraints>
<initial>
ON(A, mesa), ON(B, mesa), CLEAR(A), CLEAR(B), ARMEMPTY
<\initial>
<goal>
ON(A, B), ON(B, mesa), CLEAR(A)
<\goal>
```

#### Problema 2: Trocar a Ordem de Dois Blocos
```plaintext
<predicates>
ON(A, B), ON(B, mesa), CLEAR(A), ARMEMPTY
<\predicates>
<constraints>
ON(A, B), ARMEMPTY
<\constraints>
<initial>
ON(A, B), ON(B, mesa), CLEAR(A), ARMEMPTY
<\initial>
<goal>
ON(B, A), ON(A, mesa), CLEAR(B)
<\goal>
```

#### Problema 3: Empilhar Três Blocos
```plaintext
<predicates>
ON(A, mesa), ON(B, mesa), ON(C, mesa), CLEAR(A), CLEAR(B), CLEAR(C), ARMEMPTY
<\predicates>
<constraints>
ON(A, mesa), ARMEMPTY
<\constraints>
<initial>
ON(A, mesa), ON(B, mesa), ON(C, mesa), CLEAR(A), CLEAR(B), CLEAR(C), ARMEMPTY
<\initial>
<goal>
ON(A, B), ON(B, C), ON(C, mesa), CLEAR(A)
<\goal>
```