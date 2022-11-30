import java.sql.*;

public class NVQLUCDef {

    String entityTypeName;
    String attrList;

    NVQLUCDef(String entityTypeName, String attrList, Connection pgsqlCon) {
        this.entityTypeName = entityTypeName;
        this.attrList = attrList;

        try {
            if (NVQLEntityType.existsEntityType(entityTypeName, pgsqlCon)) {

                // String query = "SELECT \"Entity_Type_Unique_Attr_List\" FROM
                // \"ENTITY_TYPE_DEF\" WHERE \"Entity_Type_Name\" ='"
                // + this.entityTypeName + "'";
                String query = "MATCH (n:`ENTITY_TYPE_DEF`:`" + this.entityTypeName
                        + "`) RETURN (n.`Entity_Type_Unique_Attr_List`)";
                PreparedStatement statementchk = pgsqlCon.prepareStatement(query);
                ResultSet resultchk = statementchk.executeQuery();
                resultchk.next();
                // System.out.println("@$" + query);
                // System.out.println("@%" + resultchk.getString(1));

                if (resultchk.getString(1).isEmpty()) {

                    // query = "UPDATE \"ENTITY_TYPE_DEF\" SET \"Entity_Type_Unique_Attr_List\" = '"
                    // + this.attrList
                    // + "' WHERE \"Entity_Type_Name\" = '" + this.entityTypeName + "'";
                    query = "MATCH (n:`ENTITY_TYPE_DEF`:`" + this.entityTypeName
                            + "`) SET n.`Entity_Type_Unique_Attr_List` = \"" + this.attrList + "\"";
                    // System.out.println(query);

                    PreparedStatement statement = pgsqlCon.prepareStatement(query);
                    int rowAffected = statement.executeUpdate();
                    // System.out.println("@" + query);

                    if (rowAffected > 0)
                        System.out.println("NODE with LABEL 'ENTITY_TYPE_DEF' updated successfully.");

                    // query = "ALTER TABLE \"" + this.entityTypeName + "\" ADD CONSTRAINT \"" +
                    // this.entityTypeName
                    // + "1\" UNIQUE (\"" + this.attrList.replace(",", "\",\"") + "\")";
                    query = "CREATE CONSTRAINT ON (n:`" + this.entityTypeName + "`) ASSERT (n.`"
                            + this.attrList.replace(",", "`,n.`") + "`) IS NODE KEY";
                     System.out.println(query);
                    statement = pgsqlCon.prepareStatement(query);
                    statement.executeUpdate();
                    // System.out.println("@" + query);
                    System.out.println("Uniqueness Constraint is assigned to Entity Type :: " + this.entityTypeName);

                } else {
                    System.out.println("ERROR.NVQLUCDef: Uniqueness Constraint already assigned for Entity Type "
                            + this.entityTypeName + " as attributes (" + resultchk.getString(1) + ")");
                    System.exit(0);
                }
            } else {
                System.out.println("ERROR.NVQLUCDef: Entity Type <" + entityTypeName + "> does not exist");
                System.exit(0);

            }
        } catch (Exception e) {
            System.out.println("NVQLUCDef: Neo4j Error");
            System.out.println(e);
            System.exit(0);
        }
    }
}
