# AGQL

AGQL is a Domain Specific Language (DSL) to facilitate modeling of input data necessary for generation of attack graphs. It also has query constructs to help in attack graph based network security analysis. AGQL provides a higher level abstraction where, the user need not bother about underlying data management system, be it relational, graph data, etc.  

AGQL parser is generated using JavaCC (https://javacc.github.io/javacc/), an  open source parser and lexical analyzer generator.

# AGQL-GDB 

This version of AGQL uses Neo4J graph database as the backend data store. 

# Howto Use

## Step 1: Set environment

In environment.sh script (located in root folder) set the JAVACC_HOME and NEO4J_JDBC_JAR environment variable.

Example:

JAVACC_HOME=/home/xyz/javacc-6.0
NEO4j_JDBC_JAR=/home/xyz/Software/neo4j-community-3.4.4/lib/neo4j-jdbc-driver-3.3.1.jar

## Step 2: Buid AGQL

Run buildAGQL.sh script. It generates the AGQL parser source files, compiles them  and places the class files in AGQL-Class-Files folder. 

## Step 3: Run AGQL interpreter

Invoke AGQL.sh with name of the agql script as parameter. It populates the Neo4J database with graph data.

## Step4: Generate Attack Graph 

YTD

## Step 5: 

To clean everything run clean.sh script
 