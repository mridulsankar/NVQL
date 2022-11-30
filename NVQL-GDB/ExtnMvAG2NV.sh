for f in *.agql; do 
    mv -- "$f" "${f%.agql}.nvql"
done

#rename 'y/AG/NV' *