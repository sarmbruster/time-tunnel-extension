# Neo4j time tunnel extension #

## brief description ##

This project is an example for a Neo4j unmanaged extension using traversal API with branch state in its implementation.

The use case here is to only find paths with a time tunnel. Each releationship has a defined validity interval specified
a `dateFrom` and a `dateTo` property. Only if all relationships along the path have a common overlap a path is consider
 being valid and part of the result set.

It is assumed that the time properties are existing in a form of a string defaulting to the usual format `yyyy-MM-dd HH:mm:ss.SSSS`.
Using the wonderful [Joda time](http://www.joda.org/joda-time/) library, working with instants, intervals and overlap is
easy.

## usage ##

Clone this repository and run

    ./gradlew test fatJar

For Windoze it's

    gradlew.bat test fatJar

Copy the resulting jar `build/libs/time-tunnel-extension-all-0.1.jar` into your Neo4j's `plugins` folder. Amend
`conf/neo4j-server.properties` with the following line

    org.neo4j.server.thirdparty_jaxrs_classes=org.neo4j.contrib.timetunnel=/timetunnel

and restart your Neo4j instance.

To use the extension send an http GET to

    http://localhost:7474/timetunnel/<label>/<propertyName>/<propertyValue>?prop=<returnPropName>&reltype=<myRelationshipType>

The URL called has the following mandatory path parameters:

| path parameter  | description  |
|:-:|:-:|
| label  | the label to be used for the start node  |
| propertyName  | the property key to be used for the start node  |
| propertyValue  | the property value to be used for the start node  |

The following query parameters are possible

| parameter name | description | multiple times | mandatory | default value |
|----------------|-------------|----------------|-----------|---------------|
| prop | node properties to be returned | yes | yes | none |
| reltype | relationship type to follow | yes | no | all |
| fromProp | name of relationship property for start datetime | no | no | dateFrom |
| toProp | name of relationship property for end datetime | no | no | dateTo |
| datePattern | format string for datetime | no | no | yyyy-MM-dd HH:mm:ss.SSSS |

## example ##

Using [httpie](http://httpie.org) the following request starts a traversal at a node having a `Person` label with `name=John`,
 follows all `WORKED_WITH` and `IS_MANAGER_OF` relationships and returns the `name` properties of the nodes within the time tunnel.

    $ http -v "localhost:7474/timetunnel/Person/name/John?prop=name&reltype=WORKED_WITH&reltype=IS_MANAGER_OF"

The result looks like:

    GET /timetunnel/Person/name/John?prop=name&reltype=WORKED_WITH&reltype=IS_MANAGER_OF HTTP/1.1
    Host: localhost:7474
    Accept-Encoding: gzip, deflate, compress
    Accept: */*
    User-Agent: HTTPie/0.7.2

    HTTP/1.1 200 OK
    Date: Sat, 20 Dec 2014 09:57:20 GMT
    Content-Type: application/json
    Access-Control-Allow-Origin: *
    Transfer-Encoding: chunked
    Server: Jetty(9.2.1.v20140609)

    [
        {
            "dateFrom": "2009-03-05 00:00:00.0000",
            "dateTo": "2013-12-31 00:00:00.0000",
            "labels": [
                "WORKER"
            ],
            "name": "Joe"
        },
        {
            "dateFrom": "2009-03-05 00:00:00.0000",
            "dateTo": "2011-01-01 00:00:00.0000",
            "labels": [
                "WORKER"
            ],
            "simon": "Simon"
        }
    ]

