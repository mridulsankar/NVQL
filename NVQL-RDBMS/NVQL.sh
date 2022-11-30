source environment.sh
if [ ! -f ./NVQL-Class-Files/NVQLParseTest.class ]
then
    echo "NVQL Parser does not exist, build it first!"
else
    if [ $# -ne 1 ]
    then 
        echo "Invalid number of arguments"
        echo "Usage -- NVQL.sh <input file>"        
    else
        echo "NVQL: Interpreting input file $1"
        sleep 1
        SECONDS=0
        #java --module-path $PATH_TO_FX --add-modules=javafx.controls -cp "/usr/share/java/postgresql-9.4.1212.jar:./NVQL-Class-Files" NVQLParseTest $1
        java -cp "$PGSQL_JDBC_JAR:./NVQL-Class-Files" NVQLParseTest $1

        duration=$SECONDS
        echo "NVQL query (all) processing time: $(($duration / 60)) minutes and $(($duration % 60)) seconds"
    fi
fi