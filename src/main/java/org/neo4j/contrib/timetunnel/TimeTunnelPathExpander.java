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

    public Iterable<Relationship> expand(org.neo4j.graphdb.Path path, BranchState<ReadableInterval> state) {
        return Iterables.filter(new IntervalPredicate(state), inner.expand(path, state));
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

