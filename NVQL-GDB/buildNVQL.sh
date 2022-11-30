source environment.sh
cd ./NVQL-Src
java -cp $JAVACC_HOME/bin/lib/javacc.jar:. javacc NVQLGrammar.jj

#commented this and added the below line java -cp $JAVACC_HOME/bin/lib/javacc.jar:. javacc NVQLGrammar.jj
#javacc NVQLGrammar.jj
cd ..
javac -d ./NVQL-Class-Files --module-path $PATH_TO_FX --add-modules=javafx.controls ./NVQL-Src/*.java
#javac -d ./NVQL-Class-Files --module-path $PATH_TO_FX --add-modules=javafx.controls ./NVQL-Src/*.java
#javac -d ./NVQL-Class-Files  ./NVQL-Src/*.java
