package org.neo4j.contrib.timetunnel;

import org.joda.time.Interval;
import org.joda.time.ReadableInterval;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.helpers.Predicate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * a filter predicate for a Iterable of Relationship
 * checks if {@link org.joda.time.ReadableInterval} of branchstate has an overlap with to/from properties of current relationship
 *
 * @author Stefan Armbruster
 */
public class IntervalPredicate implements Predicate<Relationship> {

    static public final String DATE_FORMAT = "yyyy-MM-dd";

    private final BranchState<ReadableInterval> state;
    private String fromPropertyName;
    private String toPropertyName;

    public IntervalPredicate(BranchState<ReadableInterval> state, String fromPropertyName, String toPropertyName) {
        this.state = state;
        this.fromPropertyName = fromPropertyName;
        this.toPropertyName = toPropertyName;
    }

    @Override
    public boolean accept(Relationship relationship) {
        ReadableInterval overlap = getIntervalFromRelationship(relationship).overlap(state.getState());
        state.setState(overlap);
        return overlap != null;
    }

    private Interval getIntervalFromRelationship(Relationship relationship) {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            String from = (String) relationship.getProperty(fromPropertyName);
            String to = (String) relationship.getProperty(toPropertyName);
            long fromLong = df.parse(from).getTime();
            long toLong = df.parse(to).getTime();
            return new Interval(fromLong, toLong);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
