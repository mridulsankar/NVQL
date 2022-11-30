SECONDS=0

echo -n "Do you want to clean DB? (y/n): "
read response
if [ "$response" = "y" ] 
then
    #java -cp /usr/share/java/postgresql-9.4.1212.jar:./NVQL-Class-Files NVQLDBClean
    java -cp /usr/share/java/postgresql-42.3.0.jar:./NVQL-Class-Files NVQLDBClean
fi

echo -n "Do you want to clean source? (y/n): "
read response
if [ "$response" = "y" ] 
then
    echo "Cleaning class files..."
    rm -rf ./NVQL-Class-Files/*.class
    echo "Cleaning parser..."
    rm -rf ./NVQL-Src/NVQLParser.java
    rm -rf ./NVQL-Src/NVQLParserConstants.java
    rm -rf ./NVQL-Src/NVQLParserTokenManager.java
    rm -rf ./NVQL-Src/Token.java
    rm -rf ./NVQL-Src/TokenMgrError.java
    rm -rf ./NVQL-Src/ParseException.java
    rm -rf ./NVQL-Src/SimpleCharStream.java
fi

duration=$SECONDS
echo "$(($duration / 60)) minutes and $(($duration % 60)) seconds elapsed."