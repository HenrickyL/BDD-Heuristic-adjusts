package org.ufc.planner.auxiliar.helper;

import com.github.javabdd.BDD;
import com.github.javabdd.BDDVarSet;

public class LogicHelper {
    private LogicHelper(){}
    // pode ter erro conceitual
    public static BDD existentialQuantification(BDD formula, BDD var) {
        // ∃x.φ = φ[x=0] ∨ φ[x=1]
        BDD positive = formula.restrict(var);      // φ[x=1]
        BDD negative = formula.restrict(var.not());// φ[x=0]
        BDD result = positive.or(negative);
        positive.free();
        negative.free();
        return result;
    }
    //Recomendado
    public static BDD existentialQuantification(BDD formula, BDDVarSet vars) {
        return formula.exist(vars);
    }
}
