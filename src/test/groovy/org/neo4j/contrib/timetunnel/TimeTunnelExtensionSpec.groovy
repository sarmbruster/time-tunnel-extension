package org.neo4j.contrib.timetunnel

import org.junit.Rule
import org.neo4j.extension.spock.Neo4jResource
import org.neo4j.extension.spock.Neo4jUtils
import spock.lang.Specification
import spock.lang.Unroll

class TimeTunnelExtensionSpec extends Specification {

    @Rule
    @Delegate
    Neo4jResource neo4j = new Neo4jResource()

    static SIMPLE_GRAPH = """create (a:Person {name:'John'})-[:WORKED_WITH {from:'2010-01-01', to:'2014-12-31'}]->(:Person {name:'Jim'})"""
    static SIMPLE_GRAPH_RESULT = [[name:'Jim', from: "2010-01-01", to: "2014-12-31"]]

    static OVERLAPPING_SIMPLE = """create
    (a:Person {name:'John'})-[:WORKED_WITH {from:'2010-01-01', to:'2014-12-31'}]->(:Person {name:'Jim'})-[:WORKED_WITH {from:'2011-01-01', to:'2015-12-31'}]->(:Person {name:'Simon'})
    """
    static OVERLAPPING_SIMPLE_RESULT = [[name:'Jim', from: "2010-01-01", to: "2014-12-31"], [name:'Simon', from: '2011-01-01', to:'2014-12-31']]

    static NON_OVERLAPPING_SIMPLE = """create
    (a:Person {name:'John'})-[:WORKED_WITH {from:'2010-01-01', to:'2014-12-31'}]->(:Person {name:'Jim'})-[:WORKED_WITH {from:'2015-01-01', to:'2015-12-31'}]->(:Person {name:'Simon'})
    """
    static NON_OVERLAPPING_SIMPLE_RESULT = [[name:'Jim', from: "2010-01-01", to: "2014-12-31"]]

    static NON_MATCHING_RELTYPE = """create (a:Person {name:'John'})-[:UNKNOWN_RELTYPE {from:'2010-01-01', to:'2014-12-31'}]->(:Person {name:'Jim'})"""
    static NON_MATCHING_RELTYPE_RESULT = []

    @Unroll
    def "should find time tunnel"() {
        setup:

        cypher.cypher()
        def cut = new TimeTunnelExtension(graphDatabaseService: graphDatabaseService)

        when:
        def result = cut.findPathsWithTimeTunnel("John", ["WORKED_WITH"], ["name"])

        then:
        result == expected

        where:
        cypher                 | expected
        SIMPLE_GRAPH           | SIMPLE_GRAPH_RESULT
        OVERLAPPING_SIMPLE     | OVERLAPPING_SIMPLE_RESULT
        NON_OVERLAPPING_SIMPLE | NON_OVERLAPPING_SIMPLE_RESULT
        NON_MATCHING_RELTYPE   | NON_MATCHING_RELTYPE_RESULT

    }



}
