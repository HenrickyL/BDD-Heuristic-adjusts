package org.ufc.planner.interfaces;

import org.ufc.planner.infrastructure.ModelReader;
import org.ufc.planner.infrastructure.MetricManager;

import java.io.IOException;

public interface ISearchAlgorithm {
    void SetModel(ModelReader model);
    void ExhaustiveSearch(MetricManager metric);
    void HeuristicSearch(MetricManager metric, long backwardTime, long forwardTime) throws IOException;
}
