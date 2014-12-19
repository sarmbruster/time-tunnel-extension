package org.neo4j.contrib.timetunnel;

import org.joda.time.ReadableInterval;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.helpers.collection.Iterables;
import org.neo4j.kernel.StandardExpander;

/**
 * @author Stefan Armbruster
 */
public class TimeTunnelPathExpander implements PathExpander<ReadableInterval> {

    StandardExpander inner = StandardExpander.DEFAULT;

    private final String fromPropertyName;
    private final String toPropertyName;

    public TimeTunnelPathExpander(String fromPropertyName, String toPropertyName) {
        this.fromPropertyName = fromPropertyName;
        this.toPropertyName = toPropertyName;
    }

    public Iterable<Relationship> expand(org.neo4j.graphdb.Path path, BranchState<ReadableInterval> state) {
        return Iterables.filter(new IntervalPredicate(state, fromPropertyName, toPropertyName), inner.expand(path, state));
    }

    @Override
    public PathExpander<ReadableInterval> reverse() {
        throw new UnsupportedOperationException();
    }

    public void add(RelationshipType relationshipType, Direction direction) {
        inner = inner.add(relationshipType, direction);
    }

    public void add(RelationshipType relationshipType) {
        inner = inner.add(relationshipType);
    }
}

