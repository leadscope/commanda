Commanda - Command-line Lambdas For Java
========================================

Create one-line command-line operations with Java 8's new lambdas. Setup chains of
transformations for your own Java types, all statically typed and compiled.


Installation
------------
To create the executable jar:

  mvn install

The jar file will be at: target/commanda-<version>-jar-with-dependencies.jar

Optionally create an alias:

  alias cmda='java -jar e:/juno/commanda/target/commanda-<version>-jar-with-dependencies.jar'


Simple Examples
---------------
Extract the fourth column from a CSV file:

  cmda -csv my-data.csv -ne 'r -> r.get(3)'

Extract the ids from ToxML records:

  cmda -tox rtecs-50.xml -ne 'cr -> cr.getIds().stream().map(id -> id.toString()).collect(joining(", "))'


Extend Commanda
---------------
Custom sources, maps, and sinks can be created and plugged into the command-line interface.