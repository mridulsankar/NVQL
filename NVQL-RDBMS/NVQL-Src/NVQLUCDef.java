import java.sql.*;

public class NVQLUCDef {

    String entityTypeName;
    String attrList;

    NVQLUCDef(String entityTypeName, String attrList, Connection pgsqlCon) {
        this.entityTypeName = entityTypeName;
        this.attrList = attrList;

        try {
            if (NVQLEntityType.existsEntityType(entityTypeName, pgsqlCon)) {
                String query = "SELECT \"Entity_Type_Unique_Attr_List\" FROM \"ENTITY_TYPE_DEF\" WHERE \"Entity_Type_Name\" ='"
                        + this.entityTypeName + "'";
                PreparedStatement statementchk = pgsqlCon.prepareStatement(query);
                ResultSet resultchk = statementchk.executeQuery();
                resultchk.next();

                if (resultchk.getString(1).isEmpty()) {
                    query = "UPDATE \"ENTITY_TYPE_DEF\" SET \"Entity_Type_Unique_Attr_List\" = '" + this.attrList
                            + "' WHERE \"Entity_Type_Name\" = '" + this.entityTypeName + "'";
                    // System.out.println(query);

                    PreparedStatement statement = pgsqlCon.prepareStatement(query);
                    int rowAffected = statement.executeUpdate();

                    if (rowAffected > 0)
                        System.out.println("Table 'ENTITY_TYPE_DEF' updated successfully.");

                    query = "ALTER TABLE \"" + this.entityTypeName + "\" ADD CONSTRAINT \"" + this.entityTypeName
                            + "1\" UNIQUE (\"" + this.attrList.replace(",", "\",\"") + "\")";
                    // System.out.println(query);
                    statement = pgsqlCon.prepareStatement(query);
                    statement.executeUpdate();
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
            System.out.println("NVQLUCDef: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }
    }
}
