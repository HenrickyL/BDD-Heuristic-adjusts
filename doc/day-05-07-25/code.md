## 1. Fundamentos de BDDs em Java

### 1.1 Configuração Inicial
```java
import com.github.javabdd.BDD;
import com.github.javabdd.BDDFactory;

public class BDDPlanner {
    private BDDFactory factory;
    private BDD[] vars;
    private String[] propNames;
    
    public BDDPlanner() {
        // Inicializa com capacidade para 1.000.000 de nós
        factory = BDDFactory.init(1000000, 1000000);
    }
    
    public void initializePropositions(String[] propositions) {
        this.propNames = propositions;
        factory.setVarNum(propositions.length);
        vars = new BDD[propositions.length];
        
        for (int i = 0; i < propositions.length; i++) {
            vars[i] = factory.ithVar(i);
        }
    }
}
```

### 1.2 Exemplo Básico de Operações
```java
public class BasicBDDExample {
    public static void main(String[] args) {
        BDDPlanner planner = new BDDPlanner();
        String[] props = {"on_a_b", "on_b_a", "ontable_a", "clear_a", "handempty"};
        planner.initializePropositions(props);
        
        // Criando expressões simples
        BDD expr1 = planner.getVar("on_a_b").and(planner.getVar("on_b_a").not());
        BDD expr2 = planner.getVar("handempty").or(planner.getVar("clear_a"));
        
        System.out.println("Expr1: " + expr1);
        System.out.println("Expr2: " + expr2);
        
        // Liberando memória
        expr1.free();
        expr2.free();
    }
}
```

## 2. Codificação de Estados (Como na Sua Imagem)

### 2.1 Implementando a Codificação de Estados
```java
public BDD encodeState(Set<String> trueProps) {
    BDD state = factory.one(); // BDD verdadeiro
    
    // AND das proposições verdadeiras
    for (String prop : trueProps) {
        state.andWith(getVar(prop).id();
    }
    
    // AND das negações das outras
    for (String prop : propNames) {
        if (!trueProps.contains(prop)) {
            state.andWith(getVar(prop).not().id();
        }
    }
    
    return state;
}

// Helper para pegar variável pelo nome
public BDD getVar(String name) {
    for (int i = 0; i < propNames.length; i++) {
        if (propNames[i].equals(name)) {
            return vars[i].id();
        }
    }
    throw new IllegalArgumentException("Proposição não encontrada: " + name);
}
```

### 2.2 Exemplo Prático
```java
public class StateEncodingExample {
    public static void main(String[] args) {
        String[] allProps = {
            "handempty", "holding_a", "holding_b", 
            "ontable_a", "ontable_b", "on_a_b", 
            "on_b_a", "clear_a", "clear_b"
        };
        
        BDDPlanner planner = new BDDPlanner();
        planner.initializePropositions(allProps);
        
        Set<String> L = new HashSet<>(Arrays.asList(
            "handempty", "ontable_a", "on_b_a", "clear_b"
        ));
        
        BDD initialState = planner.encodeState(L);
        System.out.println("Estado inicial codificado:");
        printState(initialState, planner);
        
        initialState.free();
    }
    
    public static void printState(BDD state, BDDPlanner planner) {
        for (int i = 0; i < planner.propNames.length; i++) {
            BDD var = planner.vars[i];
            if (state.and(var).isZero()) {
                System.out.println(planner.propNames[i] + ": false");
            } else {
                System.out.println(planner.propNames[i] + ": true");
            }
        }
    }
}
```

## 3. Operações Avançadas para Planejamento

### 3.1 Verificação de Estado Meta
```java
public boolean isGoalState(BDD state, BDD goalCondition) {
    BDD temp = state.and(goalCondition);
    boolean result = !temp.isZero();
    temp.free();
    return result;
}
```

### 3.2 Aplicação de Ação (Transição de Estado)
```java
public BDD applyAction(BDD state, BDD preconditions, BDD effects) {
    // Verifica se as pré-condições são satisfeitas
    BDD temp = state.and(preconditions);
    if (temp.isZero()) {
        temp.free();
        return null; // Ação não aplicável
    }
    temp.free();
    
    // Aplica os efeitos
    BDD newState = state.id();
    newState.andWith(effects);
    return newState;
}
```

## 4. Exemplo Completo: Mundo dos Blocos

```java
public class BlocksWorldExample {
    public static void main(String[] args) {
        // Todas as proposições do domínio
        String[] props = {
            "handempty", "holding_a", "holding_b",
            "ontable_a", "ontable_b", "on_a_b", "on_b_a",
            "clear_a", "clear_b"
        };
        
        BDDPlanner planner = new BDDPlanner();
        planner.initializePropositions(props);
        
        // Estado inicial (como na sua imagem)
        Set<String> initialStateProps = new HashSet<>(Arrays.asList(
            "handempty", "ontable_a", "on_b_a", "clear_b"
        ));
        BDD initialState = planner.encodeState(initialStateProps);
        
        // Estado meta (A sobre B)
        Set<String> goalProps = new HashSet<>(Arrays.asList(
            "handempty", "ontable_b", "on_a_b", "clear_a"
        ));
        BDD goalState = planner.encodeState(goalProps);
        
        // Verificando se o estado inicial é meta
        System.out.println("É estado meta? " + 
            planner.isGoalState(initialState, goalState));
        
        // Definindo uma ação (pegar bloco A)
        BDD pickupPrecond = planner.getVar("ontable_a")
            .and(planner.getVar("clear_a"))
            .and(planner.getVar("handempty"));
            
        BDD pickupEffects = planner.getVar("holding_a")
            .and(planner.getVar("ontable_a").not())
            .and(planner.getVar("handempty").not());
        
        // Aplicando ação
        BDD newState = planner.applyAction(initialState, pickupPrecond, pickupEffects);
        if (newState != null) {
            System.out.println("Novo estado após pegar bloco A:");
            printState(newState, planner);
        }
        
        // Liberando memória
        initialState.free();
        goalState.free();
        pickupPrecond.free();
        pickupEffects.free();
        if (newState != null) newState.free();
    }
}
```

## 5. Exercícios para Praticar

1. **Codificação de Estados**:
    - Implemente uma função que gere todos os estados possíveis para 3 blocos
    - Verifique quantos estados são possíveis

2. **Validação de Ações**:
    - Crie as pré-condições e efeitos para a ação "stack(A,B)"
    - Teste aplicação em diferentes estados

3. **Busca em Espaço de Estados**:
    - Implemente uma busca em largura usando BDDs
    - Compare com abordagem tradicional

4. **Heurísticas**:
    - Crie uma função que estime distância até o estado meta

## 6. Dicas para seu TCC

1. **Otimizações**:
```java
// Use BDDVarSet para conjuntos de variáveis
BDDVarSet allVars = factory.makeVarSet();
for (BDD var : vars) {
    allVars.unionWith(var.support());
}

// Operações eficientes
BDD restricted = someBDD.exist(allVars);
```

2. **Visualização**:
```java
// Para debug (requer Graphviz)
public void visualizeBDD(BDD bdd, String filename) {
    try (PrintStream out = new PrintStream(filename + ".dot")) {
        out.println("digraph G {");
        bdd.printDot(out);
        out.println("}");
    }
}
```

3. **Gerenciamento de Memória Avançado**:
```java
// Padrão de uso seguro
try (BDDResource res1 = new BDDResource(expr1);
     BDDResource res2 = new BDDResource(expr2)) {
    // Operações com os BDDs
} // Liberação automática
```

Quer que eu desenvolva mais algum aspecto específico? Ou prefere focar em algum dos exercícios propostos?