package org.ufc.planner.auxiliar.LearningBDD.domain;

import com.github.javabdd.BDD;
import com.github.javabdd.BDDFactory;
import com.github.javabdd.BDDVarSet;

import java.io.PrintStream;
import java.io.FileOutputStream;
import java.util.Set;

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

    public BDD encodeState(Set<String> trueProps) {
        BDD state = factory.one(); // BDD verdadeiro

        // AND das proposições verdadeiras
        for (String prop : trueProps) {
            state.andWith(getVar(prop).id());
        }



        // AND das negações das outras
        for (String prop : propNames) {
            if (!trueProps.contains(prop)) {
                state.andWith(getVar(prop).not().id());
            }
        }

        return state;
    }

    public BDD getVar(String key) {
        for (int i = 0; i < propNames.length; i++) {
            if (propNames[i].equals(key)) {
                return vars[i].id();
            }
        }
        throw new IllegalArgumentException("Proposição não encontrada: " + key);
    }

    public boolean isGoalState(BDD state, BDD goalCondition) {
        BDD temp = state.and(goalCondition);
        boolean result = !temp.isZero();
        temp.free();
        return result;
    }

    public void visualizeBDD(BDD bdd, String filename) {
        try (PrintStream out = new PrintStream(filename + ".dot")) {
            // Redireciona a saída padrão para o arquivo temporariamente
            PrintStream originalOut = System.out;
            System.setOut(out);

            bdd.printDot();  // Já imprime um grafo .dot completo com digraph e chaves

            System.setOut(originalOut);  // Restaura a saída original
            System.out.println("BDD exportado para: " + filename + ".dot");
        } catch (Exception e) {
            System.err.println("Erro ao gerar visualização: " + e.getMessage());
        }
    }

    public BDD applyAction(BDD state, BDD preconditions, BDD effects) {
        // Verifica se as pré-condições são satisfeitas
        BDD temp = state.and(preconditions);
        try {
            if (temp.isZero()) {
                return null; // Ação não aplicável
            }

            // Aplica os efeitos - NÃO use andWith() pois consumiria state
            BDD newState = state.and(effects);
            return newState;
        } finally {
            temp.free(); // Garante que temp será liberado mesmo se ocorrer exceção
        }
    }

    private BDD[] extractBDDFromEffects(Set<String> addEffects, Set<String> delEffects){
        int size = addEffects.size() + delEffects.size();
        BDD[] bddsEffects = new BDD[size];

        int i=0;
        for(String prop: addEffects){
            bddsEffects[i] = this.getVar(prop);
            i++;
        }
        for(String prop : delEffects){
            bddsEffects[i] = this.getVar(prop);
            i++;
        }
        return bddsEffects;
    }

    // Progression: ξ(progr(X, a)) = ∃ modified(a) (ξ(X) ∧ ξ(precond(a))) ∧ ξ(effects(a))
    public BDD progressState(BDD currentState, BDD actionPreconditions,
                             Set<String> addEffects, Set<String> delEffects) {
        // 1. Verificar pré-condições
        BDD applicable = currentState.and(actionPreconditions);
        if (applicable.isZero()) {
            applicable.free();
            return null; // Ação não aplicável
        }

        // 2. Existencial do modifica
        BDD[] bddsEffects = extractBDDFromEffects(addEffects, delEffects);
        BDD filter = applicable;
        for(BDD var: bddsEffects){
            filter = existentialQuantification(filter, var);
        }

        BDD effects = factory.one();
        for(String key: addEffects) {
            effects = effects.and(getVar(key));
        }
        for(String key: delEffects) {
            effects = effects.and(getVar(key).not());
        }
        // 3. Add Effects
        BDD newState = filter.and(effects);

        // 4. Liberar recursos
        applicable.free();
        effects.free();
        filter.free();
//        newValues.free();

        return newState;
    }

    private BDD existentialQuantification(BDD formula, BDD var) {
        // ∃x.φ = φ[x=0] ∨ φ[x=1]
        BDD positive = formula.restrict(var);      // φ[x=1]
        BDD negative = formula.restrict(var.not());// φ[x=0]
        BDD result = positive.or(negative);
        positive.free();
        negative.free();
        return result;
    }
}
