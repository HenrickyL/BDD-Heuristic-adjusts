package org.ufc.planner.auxiliar.LearningBDD.domain;

import com.github.javabdd.BDD;
import org.ufc.planner.infrastructure.ModelReader;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BDDModelPlanner extends BDDPlanner {
    private final ModelReader modelReader;

    public BDDModelPlanner(ModelReader modelReader) {
        super(); // Chama o construtor padrão do BDDPlanner
        this.modelReader = modelReader;
        initializeFromModel();
    }

    private void initializeFromModel() {
        // 1. Inicializa as proposições
        Hashtable<String, Integer> varTable = modelReader.getVarTable();
        String[] propositions = varTable.keySet().toArray(new String[0]);
        super.initializePropositions(propositions);

        // 2. Configura estado inicial e goal (opcional, pode ser acessado diretamente do modelReader quando necessário)
        // Eles já estão disponíveis via modelReader.getInitialStateBDD() e getGoalSpec()
    }

    // Métodos de conveniência para acessar os elementos do modelo
    public BDD getModelInitialState() {
        return modelReader.getInitialStateBDD().id();
    }

    public BDD getModelGoal() {
        return modelReader.getGoalSpec().id();
    }

//    public List<Action> getModelActions() {
//        return new ArrayList<>(modelReader.getActionSet());
//    }

    public BDD getModelConstraints() {
        return modelReader.getConstraints().id();
    }

//    // Método para criar uma ação diretamente do modelo pelo nome
//    public Action getActionByName(String actionName) {
//        return modelReader.getActionSet().stream()
//                .filter(a -> a.getName().equals(actionName))
//                .findFirst()
//                .orElse(null);
//    }

//    // Versão modificada do progressState que trabalha com ações do modelo
//    public BDD modelProgressState(BDD currentState, String actionName, boolean isRelaxed) {
//        Action action = getActionByName(actionName);
//        if (action == null) {
//            throw new IllegalArgumentException("Ação não encontrada: " + actionName);
//        }
//        return super.progressState(currentState, action, isRelaxed);
//    }
}