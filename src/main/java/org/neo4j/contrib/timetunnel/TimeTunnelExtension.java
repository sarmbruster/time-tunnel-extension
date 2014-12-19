package org.neo4j.contrib.timetunnel;

import org.joda.time.Interval;
import org.joda.time.ReadableInterval;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.*;

import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/")
public class TimeTunnelExtension {

    @Context
    GraphDatabaseService graphDatabaseService;

    @GET
    @Path("{personName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Map<String, Object>> findPathsWithTimeTunnel(
            @PathParam("personName") String personName,
            @QueryParam("reltype") List<String> reltypes,
            @QueryParam("prop") List<String> returnProps)
    {
        try (Transaction tx = graphDatabaseService.beginTx()) {

            InitialBranchState.State<ReadableInterval> stateHolder = new InitialBranchState.State<ReadableInterval>(new Interval(0, Long.MAX_VALUE), null);

            TimeTunnelPathExpander timeTunnelPathExpander = new TimeTunnelPathExpander();
            for (String reltype : reltypes) {
                timeTunnelPathExpander.add(DynamicRelationshipType.withName(reltype));
            }

            TraversalDescription timeTunnelTraversal = graphDatabaseService.traversalDescription()
                    .expand(timeTunnelPathExpander, stateHolder)
                    .evaluator(new HasOverlapPathEvaluator())
                    .evaluator(Evaluators.excludeStartPosition());

            ResourceIterable<Node> startNodes = graphDatabaseService.findNodesByLabelAndProperty(DynamicLabel.label("Person"), "name", personName);

            // consume traversal and build return data structure
            Collection<Map<String,Object>> result = new ArrayList<>();
            for (org.neo4j.graphdb.Path p : timeTunnelTraversal.traverse(startNodes)) {

                TraversalBranch path = (TraversalBranch)p;

                Map<String,Object> map = new HashMap<>();
                result.add(map);

                for (String key : returnProps) {
                    map.put(key, path.endNode().getProperty(key, null));
                }
                ReadableInterval interval = (ReadableInterval) path.state();
                map.put("from", interval.getStart().toLocalDate().toString("yyyy-MM-dd"));
                map.put("to", interval.getEnd().toLocalDate().toString("yyyy-MM-dd"));
            }
            return result;
        }
    }


}