import java.sql.*;

public class NVQLCCDef {

    String relationTypeName;
    String attrList;

    static void CardinalityConstraintDef(String relationTypeName, int numOfsrcEntity, int numOftgtEntity,
            Connection pgsqlCon) {

        try {
            if (NVQLRelationType.existsRelationType(relationTypeName, pgsqlCon)) {
                String query = "SELECT \"Relation_Type_Cardinality_Constraint\" FROM \"RELATION_TYPE_DEF\" WHERE \"Relation_Type_Name\" ='"
                        + relationTypeName + "'";
                PreparedStatement statementchk = pgsqlCon.prepareStatement(query);
                ResultSet resultchk = statementchk.executeQuery();
                resultchk.next();

                if (resultchk.getString(1).isEmpty()) {
                    query = "UPDATE \"RELATION_TYPE_DEF\" SET \"Relation_Type_Cardinality_Constraint\" = '"
                            + numOfsrcEntity + ":" + numOftgtEntity + "' WHERE \"Relation_Type_Name\" = '"
                            + relationTypeName + "'";
                    // System.out.println(query);

                    PreparedStatement statement = pgsqlCon.prepareStatement(query);
                    int rowAffected = statement.executeUpdate();

                    if (rowAffected > 0)
                        System.out.println("Table 'RELATION_TYPE_DEF' updated successfully.");

                    System.out.println("Cardinality Constraint is assigned to Relation Type :: " + relationTypeName);

                }

                else {
                    System.out.println("ERROR.NVQLCCDef: Cardinality Constraint already assigned for Relation Type "
                            + relationTypeName + " as attributes [" + resultchk.getString(1) + "]");
                    System.exit(0);
                }
            } else {
                System.out.println("ERROR.NVQLCCDef: Relation Type <" + relationTypeName + "> does not exist");
                System.exit(0);

            }
        } catch (Exception e) {
            System.out.println("NVQLCCDef: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }
    }
}
