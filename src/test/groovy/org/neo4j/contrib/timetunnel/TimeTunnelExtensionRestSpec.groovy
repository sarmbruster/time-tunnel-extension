package org.neo4j.contrib.timetunnel

import org.junit.ClassRule
import org.neo4j.extension.spock.Neo4jServerResource
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class TimeTunnelExtensionRestSpec extends Specification {

    @ClassRule
    @Shared
    Neo4jServerResource neo4j = new Neo4jServerResource(
            thirdPartyJaxRsPackages: [ "org.neo4j.contrib.timetunnel": "/timetunnel"]
    )

    def "run via server"() {
        setup:
        TimeTunnelExtensionSpec.OVERLAPPING_SIMPLE.cypher()

        when:
        def response = neo4j.http.GET("timetunnel/Person/name/John?prop=name&reltype=WORKED_WITH&fromProp=from&toProp=to&datePattern=yyyy-MM-dd")

        then:
        response.status() == 200
        response.content() == TimeTunnelExtensionSpec.OVERLAPPING_SIMPLE_RESULT
    }

}
