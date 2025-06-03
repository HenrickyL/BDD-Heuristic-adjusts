package org.ufc.planner.auxiliar.LearningBDD.domain;

import com.github.javabdd.BDD;
import com.github.javabdd.BDDFactory;

import java.io.PrintStream;
import java.util.Set;

public class BDDPlanner implements IPlanner{
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

    private BDD encodePrecondition(BDD[] props){
        BDD actionPreconditions = factory.one();
        for (BDD precond : props) {
            actionPreconditions.andWith(precond.id());
        }
        return actionPreconditions;
    }


    // Progression: ξ(progr(X, a)) = ∃ modified(a) (ξ(X) ∧ ξ(precond(a))) ∧ ξ(effects(a))
    public BDD progressState(BDD currentState, Action action, boolean isRelaxed) {
        // 1. Verificar pré-condições
        BDD actionPreconditions = encodePrecondition(action.getPreconditions());
        BDD applicable = currentState.and(actionPreconditions);
        if (applicable.isZero()) {
            applicable.free();
            actionPreconditions.free();
            return null; // Ação não aplicável
        }

        // 2. Existencial do modifica
        //- Modifica
        BDD filter = applicable;
        for (BDD addEffect : action.getAddictionEffects()) {
            filter = existentialQuantification(filter, addEffect);
        }
        if (!isRelaxed) {
            for (BDD delEffect : action.getDelectionEffects()) {
                filter = existentialQuantification(filter, delEffect);
            }
        }
        //- constroi efeitos
        BDD effects = factory.one();
        // Sempre aplica efeitos positivos
        for (BDD addEffect : action.getAddictionEffects()) {
            effects.andWith(addEffect.id());
        }
        if (!isRelaxed) {
            for (BDD delEffect : action.getDelectionEffects()) {
                effects.andWith(delEffect.not().id());
            }
        }

        // 3. Aplica os efeitos da acao
        BDD newState = filter.and(effects);

        // 4. Liberar recursos
        actionPreconditions.free();
        applicable.free();
        effects.free();
        filter.free();
//        newValues.free();

        return newState;
    }

//    // Regression: ξ(regr(X, a)) =  ξ(precond(a)) ∧ ∃ modified(a).( ξ(effects(a)) ∧ ξ(X) )
//    public BDD regressState(BDD targetState, Action action, boolean isRelaxed) {
//        // 1. Construir os efeitos da acao
//        BDD actionEffects = factory.one();
//        // Efeitos positivos
//        for (BDD addEffect : action.getEffectAddiction()) {
//            actionEffects.andWith(addEffect.id());
//        }
//        // Efeitos negativos (apenas se não for relaxado)
//        if (!isRelaxed) {
//            for (BDD delEffect : action.getEffectDelection()) {
//                actionEffects.andWith(delEffect.not().id());
//            }
//        }
//        // 2. AND com o estado alvo
//        BDD temp = targetState.and(actionEffects);
//        // 3. Quantificação existencial nas variáveis modificadas
//        BDD regressed = temp.id();
//        for (BDD addEffect : action.getEffectAddiction()) {
//            regressed = existentialQuantification(regressed, addEffect);
//        }
//        if (!isRelaxed) {
//            for (BDD delEffect : action.getEffectDelection()) {
//                regressed = existentialQuantification(regressed, delEffect);
//            }
//        }
//        // 4. Construir pré-condições da ação
//        BDD preconditions = factory.one();
//        for (BDD precond : action.getPrecondition()) {
//            preconditions.andWith(precond.id());
//        }
//        // 5. AND com as pré-condições
//        BDD result = regressed.and(preconditions);
//        // Liberar recursos
//        actionEffects.free();
//        temp.free();
//        regressed.free();
//        preconditions.free();
//
//        return result;
//    }


    public BDD getOne(){
        return factory.one();
    }

    public static BDD existentialQuantification(BDD formula, BDD var) {
        // ∃x.φ = φ[x=0] ∨ φ[x=1]
        BDD positive = formula.restrict(var);      // φ[x=1]
        BDD negative = formula.restrict(var.not());// φ[x=0]
        BDD result = positive.or(negative);
        positive.free();
        negative.free();
        return result;
    }

}
