Commanda - Command-line Lambdas For Java
========================================

Create command-line operations with Java 8's lambdas. Similar to processing
line records with awk, perl, or ruby - but instead, setup chains of
transformations for your own Java types, all statically typed and compiled.


Installation
------------
To create the executable jar:

    mvn install

Optionally create an alias:

    alias cmda='java -jar <project-path>/target/commanda-<version>-jar-with-dependencies.jar'


Simple Examples
---------------
Extract the fourth column from a CSV file:

    cmda -csv my-data.csv -ne 'r -> r.get(3)'

Extract the ids from ToxML records:

    cmda -tox rtecs-50.xml -ne 'cr -> cr.getIds().map(id -> id.toString()).collect(joining(", "))'


Extend Commanda
---------------
Custom sources, maps, and sinks can be created and plugged into the command-line interface.