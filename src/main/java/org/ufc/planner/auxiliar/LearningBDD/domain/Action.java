package org.ufc.planner.auxiliar.LearningBDD.domain;

import com.github.javabdd.BDD;

public class Action {
    private final String name;
    private final BDDPlanner planner;
    private final BDD[] precondition;
    private final BDD[] effectAddiction;
    private final BDD[] effectDelection;

    public Action(String name, BDDPlanner planner, String[] precondition, String[] effectAddiction, String[] effectDelection){
        this.name = name;
        this.planner = planner;
        this.precondition = createBDD(precondition);
        this.effectAddiction = createBDD(effectAddiction);
        this.effectDelection = createBDD(effectDelection);
    }

    private BDD[] createBDD(String[] props){
        BDD[] response = new BDD[props.length];
        for (int i=0; i< props.length; i++){
            response[i] = planner.getVar(props[i]);
        }
        return response;
    }

    public BDD[] getPrecondition() {
        return precondition;
    }

    public BDD[] getEffectAddiction() {
        return effectAddiction;
    }

    public BDD[] getEffectDelection() {
        return effectDelection;
    }

    public String getName() {
        return name;
    }
}
