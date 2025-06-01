package org.ufc.planner.auxiliar.LearningBDD.domain;

import com.github.javabdd.BDD;

import java.util.Set;

public interface IPlanner {
    public void initializePropositions(String[] propositions);
    public BDD encodeState(Set<String> trueProps);
    public BDD progressState(BDD currentState, Action action, boolean isRelaxed);
}
