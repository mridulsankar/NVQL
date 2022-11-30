source environment.sh

echo -n "Do you want to populate TVA database? (y/n): "
read response
if [ "$response" = "y" ] 
then
    SECONDS=0
    #java --module-path $PATH_TO_FX --add-modules=javafx.controls -cp "/usr/share/java/postgresql-9.4.1212.jar:./AGQL-Class-Files"  TVA_Approach
    java -cp "$PGSQL_JDBC_JAR:./NVQL-Class-Files"  TVA_Approach
    echo "Populated TVA Database..."
    duration=$SECONDS
    echo
    echo "NVQL to TVA Transfer Time: $(($duration / 60)) minutes and $(($duration % 60)) seconds"
    echo

fi



echo -n "Do you want to generate TVA Attack Graph? (y/n): "
read response
if [ "$response" = "y" ] 
then
    SECONDS=0
    echo "Generating TVA Attack Graph..."
    echo
    ruby ./TVA/Generate-AG-3.0.rb
    echo
    echo "TVA Attack Graph generation finished..."
    echo    

    duration=$SECONDS
    echo "TVA Attack Graph Generation Time: $(($duration / 60)) minutes and $(($duration % 60)) seconds"
fi

echo
echo -n "Do you want to generate Attack Graph image? (y/n): "

read response
if [ "$response" = "y" ] 
then 
    echo -n "Enter name of output file: "
    read fname
    ruby ./TVA/Draw-AG-3.0.rb $fname

    echo
    echo "TVA Attack Graph has been generated in $fname.jpg"
    
fi



