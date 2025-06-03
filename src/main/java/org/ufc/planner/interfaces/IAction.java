package org.ufc.planner.interfaces;

import com.github.javabdd.BDD;
import com.github.javabdd.BDDVarSet;

public interface IAction {
    public BDDVarSet getChange();
    public BDDVarSet getRelaxChange();
    public BDD getPrecondition();
    public BDD getEffect();
    public BDD getRelaxEffect();
}
