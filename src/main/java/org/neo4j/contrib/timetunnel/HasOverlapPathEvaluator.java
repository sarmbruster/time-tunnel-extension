package org.neo4j.contrib.timetunnel;

import org.joda.time.ReadableInterval;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.PathEvaluator;

/**
 * terminates a branch if the branch state does not have an overlap, aka branch state is null
 * to be used together with
 *
 * @author Stefan Armbruster
 */
public class HasOverlapPathEvaluator extends PathEvaluator.Adapter<ReadableInterval> {

    @Override
    public Evaluation evaluate(org.neo4j.graphdb.Path path, BranchState<ReadableInterval> state) {

        ReadableInterval interval = state.getState();
        if (interval != null) {
            return Evaluation.INCLUDE_AND_CONTINUE;
        } else {
            return Evaluation.INCLUDE_AND_PRUNE;
        }
    }
}
