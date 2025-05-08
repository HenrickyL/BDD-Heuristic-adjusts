package org.ufc.planner.core;

import org.ufc.planner.infrastructure.ModelReader;
import org.ufc.planner.infrastructure.TimeManager;

import java.io.IOException;

public interface ISearchAlgorithm {
    void SetModel(ModelReader model);
    void ExaustiveSearch(TimeManager verify);
    void HeuristicSearch(TimeManager verify) throws IOException;
}
