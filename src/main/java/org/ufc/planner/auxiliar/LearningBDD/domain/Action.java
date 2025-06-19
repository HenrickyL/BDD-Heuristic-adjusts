package org.ufc.planner.auxiliar.LearningBDD.domain;

import com.github.javabdd.BDD;
import com.github.javabdd.BDDVarSet;
import org.ufc.planner.interfaces.IAction;

public class Action /*implements IAction*/ {
    private final String name;
    private final BDDPlanner planner;

    private final BDD precondition;
    private final BDD effect;
    private final BDD relaxEffect;
    private final BDD change;
//    private final BDD relaxChange;


    private final String[] preconditions;
    private final String[] addictionEffects;
    private final String[] delectionEffects;


    public Action(String name, BDDPlanner planner, String[] precondition, String[] addictionEffects, String[] delectionEffects){
        this.name = name;
        this.planner = planner;
        this.preconditions = precondition;
        this.addictionEffects = addictionEffects;
        this.delectionEffects = delectionEffects;

        BDD addiction = createBDD(addictionEffects);
        BDD delection = createNotBDD(delectionEffects);

        this.precondition = createBDD(precondition);
        this.effect = addiction.and(delection);
        this.relaxEffect = addiction;
        this.change = addiction.and(createBDD(delectionEffects));
    }

    private BDD createBDD(String[] props){
        BDD response = planner.getVar(props[0]);
        for (int i=1; i< props.length; i++){
            response = response.and(planner.getVar(props[i]));
        }
        return response;
    }
    private BDD createNotBDD(String[] props){
        BDD response = planner.getVar(props[0]).not();
        for (int i=1; i< props.length; i++){
            response = response.and(planner.getVar(props[i]).not());
        }
        return response;
    }

    /* ----- GETTER & SETTER ----- */
    public String[] getPreconditions() {
        return preconditions;
    }

    public String[] getAddictionEffects() {
        return addictionEffects;
    }

    public String[] getDelectionEffects() {
        return delectionEffects;
    }

    public String getName() {
        return name;
    }

    public BDD getPrecondition() {
        return precondition;
    }



    public BDD getEffect() {
        return effect;
    }

    public BDD getRelaxEffect() {
        return relaxEffect;
    }

    public BDD getChange() {
        return change;
    }

    public BDD getRelaxChange() {
        return relaxEffect;
    }

    public BDDVarSet getChangeVars() {
        return change.support();
    }

    public BDDVarSet getEffectVars() {
        return effect.support();
    }

    public BDDVarSet getRelaxEffectVars() {
        return relaxEffect.support();
    }

    /**
     * Representa uma ação no contexto de planejamento simbólico com BDDs.
     *
     * Cada ação possui:
     *
     * - {@code change}:
     *     Representa as proposições que são modificadas pela ação,
     *     tanto efeitos positivos (adições) quanto negativos (deleções),
     *     considerando os efeitos diretos e explícitos da ação.
     *     É usado para identificar quais variáveis precisam ser
     *     removidas do estado atual durante a progressão (através
     *     da quantificação existencial).
     *
     * - {@code effect}:
     *     Representa o efeito completo da ação.
     *     Esse efeito é a combinação das proposições que devem ser adicionadas
     *     (efeitos positivos) e das que devem ser removidas (efeitos negativos,
     *     representadas como negações no BDD).
     *     Aplicar {@code effect} sobre um estado significa obter o estado
     *     resultante após a execução da ação.
     *
     * - {@code relaxEffect}:
     *     É uma versão simplificada do {@code effect}, onde considera-se
     *     apenas os efeitos aditivos (positivos) da ação, ignorando
     *     os efeitos de deleção (negativos).
     *     Esse efeito relaxado é geralmente utilizado em heurísticas
     *     ou em algoritmos de planejamento que ignoram deleções para
     *     simplificar o espaço de busca, como no cálculo de heurísticas
     *     baseadas em problemas relaxados (ex.: FF ou H<sub>max</sub>).
     *
     * 🔸 Resumo das diferenças:
     * - {@code change} → Quais proposições são alteradas (usado para
     *                     filtrar do estado atual antes de aplicar {@code effect}).
     * - {@code effect} → Efeito completo da ação (adições e deleções).
     * - {@code relaxEffect} → Apenas os efeitos positivos (adições).
     */
}
