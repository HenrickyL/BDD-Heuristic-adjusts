### Domain do Mundo dos Blocos


### **Predicados**
- **on_x_y**: O bloco `x` está sobre o bloco ou mesa `y`.
- **ontable_x**: O bloco `x` está diretamente sobre a mesa.
- **clear_x**: O topo do bloco ou mesa `x` está livre.
- **handempty**: O robô não está segurando nenhum bloco.
- **holding_x**: O robô está segurando o bloco `x`.

---

### **Ações**

1. **pickup_x**  
   - **Pré-condições**: `ontable_x`, `clear_x`, `handempty`  
   - **Efeitos Positivos**: `holding_x`, `~ontable_x`, `~clear_x`, `~handempty`  
   - **Efeitos Negativos**: `ontable_x`, `clear_x`, `handempty`

2. **putdown_x**  
   - **Pré-condições**: `holding_x`  
   - **Efeitos Positivos**: `ontable_x`, `clear_x`, `handempty`, `~holding_x`  
   - **Efeitos Negativos**: `holding_x`

3. **stack_x_y**  
   - **Pré-condições**: `holding_x`, `clear_y`  
   - **Efeitos Positivos**: `on_x_y`, `clear_x`, `handempty`, `~holding_x`, `~clear_y`  
   - **Efeitos Negativos**: `holding_x`, `clear_y`

4. **unstack_x_y**  
   - **Pré-condições**: `on_x_y`, `clear_x`, `handempty`  
   - **Efeitos Positivos**: `holding_x`, `clear_y`, `~on_x_y`, `~handempty`  
   - **Efeitos Negativos**: `on_x_y`, `handempty`



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
on_a_b, ontable_a, ontable_b, clear_a, clear_b, handempty, holding_a
<\predicates>

<constraints>
ontable_a, ontable_b, clear_a, clear_b, handempty
<\constraints>

<actionsSet>
<action>
<name>pickup_a<\name>
<pre>ontable_a, clear_a, handempty<\pre>
<pos>holding_a, ~ontable_a, ~clear_a, ~handempty<\pos>
<\action>

<action>
<name>stack_a_b<\name>
<pre>holding_a, clear_b<\pre>
<pos>on_a_b, clear_a, handempty, ~holding_a, ~clear_b<\pos>
<\action>

<action>
<name>putdown_a<\name>
<pre>holding_a<\pre>
<pos>ontable_a, clear_a, handempty, ~holding_a<\pos>
<\action>

<\actionsSet>
```

#### Problema 1: Empilhar Dois Blocos
```plaintext
<predicates>
on_a_b, ontable_b, ontable_a, clear_a, clear_b, handempty, holding_a
<\predicates>

<constraints>
ontable_a, ontable_b, clear_a, clear_b, handempty
<\constraints>

<initial>
ontable_a, ontable_b, clear_a, clear_b, handempty, ~on_a_b, ~holding_a
<\initial>

<goal>
on_a_b, ontable_b, clear_a
<\goal>

```

#### Problema 2: Trocar a Ordem de Dois Blocos
```plaintext
<predicates>
on_a_b, on_b_a, ontable_a, ontable_b, clear_a, clear_b, handempty, holding_a
<\predicates>

<constraints>
on_a_b, ontable_b, clear_a, handempty
<\constraints>

<initial>
on_a_b, ontable_b, clear_a, handempty, ~holding_a, ~on_b_a
<\initial>

<goal>
on_b_a, ontable_a, clear_b
<\goal>

```

#### Problema 3: Empilhar Três Blocos
```plaintext
<predicates>
on_a_b, on_b_c, ontable_a, ontable_b, ontable_c, clear_a, clear_b, clear_c, handempty, holding_a
<\predicates>

<constraints>
ontable_a, ontable_b, ontable_c, clear_a, clear_b, clear_c, handempty
<\constraints>

<initial>
ontable_a, ontable_b, ontable_c, clear_a, clear_b, clear_c, handempty, ~on_a_b, ~on_b_c, ~holding_a
<\initial>

<goal>
on_a_b, on_b_c, ontable_c, clear_a
<\goal>

```