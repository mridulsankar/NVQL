source environment.sh

SECONDS=0
# do some work


cd ./NVQL-Src
java -cp $JAVACC_HOME/bin/lib/javacc.jar:. javacc NVQLGrammar.jj
cd ..
#javac -d ./NVQL-Class-Files --module-path $PATH_TO_FX --add-modules=javafx.controls ./NVQL-Src/*.java
#javac -d ./NVQL-Class-Files --module-path $PATH_TO_FX --add-modules=javafx.controls ./TVA/*.java
if [ ! -d "NVQL-Class-Files" ]
then
    echo "Craeting folder NVQL-Class-Files..."
    mkdir NVQL-Class-Files
fi
javac -d ./NVQL-Class-Files --module-path $PATH_TO_FX --add-modules=javafx.controls ./NVQL-Src/*.java
javac -d ./NVQL-Class-Files --module-path $PATH_TO_FX --add-modules=javafx.controls ./TVA/*.java


#duration=$SECONDS
#echo "$(($duration / 60)) minutes and $(($duration % 60)) seconds elapsed."

#javac -d ./NVQL-Class-Files  ./NVQL-Src/*.java
#javac -d ./NVQL-Class-Files  ./TVA/*.java

#javac --module-path $PATH_TO_FX --add-modules=javafx.controls *.java
#java --module-path $PATH_TO_FX --add-modules=javafx.controls -cp "/usr/share/java/postgresql-9.4.1212.jar:." NVQLParseTest "MSB-Demo1-test.NVQL"

#javac TVA_Approach.java
#java --module-path $PATH_TO_FX --add-modules=javafx.controls -cp "/usr/share/java/postgresql-9.4.1212.jar:."  TVA_Approach
