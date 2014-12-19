package org.neo4j.contrib.timetunnel;

import org.joda.time.ReadableInterval;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.helpers.collection.Iterables;

/**
 * @author Stefan Armbruster
 */
public class TimeTunnelPathExpander implements PathExpander<ReadableInterval> {

    @Override
    public Iterable<Relationship> expand(org.neo4j.graphdb.Path path, BranchState<ReadableInterval> state) {
        return Iterables.filter(new IntervalPredicate(state), path.endNode().getRelationships());
    }

    @Override
    public PathExpander<ReadableInterval> reverse() {
        throw new UnsupportedOperationException();
    }
}

