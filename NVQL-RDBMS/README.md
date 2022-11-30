# NVQL

NVQL is a Domain Specific Language (DSL) to facilitate modeling of input data necessary for generation of attack graphs. It also has query constructs to help in attack graph based network security analysis. NVQL provides a higher level abstraction, whereby an user need not be concerned about underlying data management system, be it relational, graph data, etc. 

NVQL parser is generated using JavaCC (https://javacc.github.io/javacc/), an  open source parser and lexical analyzer generator.

# NVQL-RDBMS 

This version of NVQL uses PostgreSQL as the backend data store. 

## Install and Configure PostgreSQL

Detail of PostgreSQL installation is given here https://wiki.postgresql.org/wiki/Apt

Import the repository key from https://www.postgresql.org/media/keys/ACCC4CF8.asc

```
sudo apt-get install curl ca-certificates gnupg
curl https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
```

Create /etc/apt/sources.list.d/pgdg.list file. The distributions are called codename-pgdg. In the example, replace cosmic with the actual distribution you are using:

```
deb http://apt.postgresql.org/pub/repos/apt cosmic-pgdg main
```

(You may determine the codename of your distribution by running lsb_release -c). For a shorthand version of the above, presuming you are using a supported release:

```
sudo sh -c 'echo "deb [arch=amd64] http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
```

Finally, update the package lists, and start installing packages:

```
sudo apt-get update
sudo apt-get install postgresql-11 pgadmin4
sudo apt install postgresql-client-common
```
Generate password for postgres database user

```
sudo -u postgres psql
```

Inside psql shell give database user a password

```
ALTER USER postgres PASSWORD 'yourPassword';
```

Create two databases

```
CREATE DATABASE "NVQL";
CREATE DATABASE "TVA";
```
## Install Graphviz

NVQL uses open source graph visualization software Graphviz (https://www.graphviz.org) for generating attack graph images. 

```
sudo add-apt-repository universe
sudo apt update

sudo apt install graphviz
```

## Install Ruby

TVA attack graph generation scripts are written in Ruby. Install Ruby and the libraries rubygems, activerecords, pg, and ruby_graphviz. 

```
sudo apt-get install ruby-full
```

Install necessary gems

```
sudo gem install pg # this may require installation of libpg-dev library
sudo apt-get install libpg-dev
sudo gem install activerecord
sudo gem install ruby-graphviz
```

# Howto Use

## Step 1: Set environment

In environment.sh script (located in root folder) set the JAVACC_HOME and PGSQL_JDBC_JAR environment variable.

Example:

```
JAVACC_HOME=/home/xyz/javacc-6.0
PGSQL_JDBC_JAR=/usr/share/java/postgresql-9.4.1212.jar
```

   

## Step 2: Buid NVQL

Run buildNVQL.sh script. It generates the NVQL parser source files, compiles them  and places the class files in NVQL-Class-Files folder. 

## Step 3: Run NVQL interpreter

Invoke NVQL.sh with name of the agql script as parameter. It populates the PgSQL database with appropriate tables and rows.

## Step4: Generate Attack Graph 

Invoke TVAGenerateAG script

## Step5: Clean everything

To clean everything run clean.sh script

# References



# Contributors

1. Mridul Snakar Barik - mridulsankar@gmail.com
2. Dipayan Ghosh - d6ghosh@gmail.com


# Maintained By

1. Mridul Snakar Barik - mridulsankar@gmail.com
2. Dipayan Ghosh - d6ghosh@gmail.com


