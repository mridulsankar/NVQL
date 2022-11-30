import java.sql.*;

public class NVQLEntityType {
    String entityTypeName;
    String attrDefList;
    static Boolean DEBUG = true;

    NVQLEntityType(String entityTypeName, String attrDefList, Connection pgsqlCon) {
        this.entityTypeName = entityTypeName;
        this.attrDefList = attrDefList.replace(":", "`:\"").replace(", ", "\", `");

        try {
            if (!existsEntityType(entityTypeName, pgsqlCon))
                this.createEntityType(pgsqlCon);
            else {
                System.out.println("ERROR.NVQLEntityType: Entity <" + entityTypeName + "> already exists");
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("NVQLEntityType: Neo4j Error");
            System.out.println(e);
            System.exit(0);
        }

    }

    static Boolean existsEntityType(String entityTypeName, Connection pgsqlCon) throws Exception {
        String query;
        Boolean exists = false;

        try {
            query = "MATCH (n: `ENTITY_TYPE_DEF` : `" + entityTypeName
                    + "`) RETURN CASE WHEN (COUNT(n) = 0) THEN \"f\" ELSE \"t\" END as n";
            PreparedStatement statement = pgsqlCon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();
            if(DEBUG)
                System.out.println("@" + query);

            if (result.getString(1).equals("t"))
                exists = true;
        } catch (Exception e) {
            System.out.println("NVQLEntityTypeDef: Neo4j Query Failed");
            System.out.println(e);
            System.exit(0);
        }
        return (exists);
    }

    void createEntityType(Connection pgsqlCon) throws Exception {
        // System.out.println("##" + this.attrDefList);

        String query = "CREATE ( n: `ENTITY_TYPE_DEF`: `" + this.entityTypeName + "` {`" + this.entityTypeName
                + "_id`:\"integer\", `Entity_Type_Unique_Attr_List`:\"\"";
        if (!this.attrDefList.isEmpty())
            query = query + ",`" + this.attrDefList + "\"";

        query = query + "})";
        PreparedStatement posted = pgsqlCon.prepareStatement(query);
        posted.executeUpdate();
        if(DEBUG)
            System.out.println("@" + query);
        System.out.println("1 node added Values: [(`" + this.entityTypeName + ") , (" + this.attrDefList
                + "\") , ()] inserted with label 'ENTITY_TYPE_DEF'");

        // String adl = this.attrDefList;
        // String quelist = "";

        // NVQLAttrDefList adlObject = new NVQLAttrDefList(adl);

        // quelist = adlObject.convertADL4SQL();
        // tempary disabled for neo4j compatible
        /*
         * if (quelist != "") query = "CREATE TABLE \"" + this.entityTypeName + "\" (\""
         * + this.entityTypeName + "_Id\" SERIAL," + quelist + ")"; else query =
         * "CREATE TABLE \"" + this.entityTypeName + "\" (\"" + this.entityTypeName +
         * "_Id\" SERIAL)";
         * 
         * posted = pgsqlCon.prepareStatement(query); posted.executeUpdate();
         * System.out.println("@" + query);
         */
        System.out.println("Entity Type '" + this.entityTypeName + "' definition created successfully");

    }

    public static String getAttrDefList(String entityTypeName, Connection pgsqlCon) throws Exception {

        if (!existsEntityType(entityTypeName, pgsqlCon)) {
            System.out.println("ERROR.NVQLEntityType: Entity type <" + entityTypeName + "> is not defined");
            System.exit(0);
            return null;
        }

        String query = "MATCH (n: `ENTITY_TYPE_DEF`: `" + entityTypeName
                + "`) RETURN apoc.map.removeKey(n,'Entity_Type_Unique_Attr_List') As props";

        PreparedStatement statement1 = pgsqlCon.prepareStatement(query);
        ResultSet result1 = statement1.executeQuery();

        result1.next();
        if(DEBUG)
            System.out.println("@" + query);

        return (result1.getObject(1).toString());
    }

    public static String getModAttrDefList(String AttrDefList, Connection pgsqlCon) throws Exception {

        String new_AttrDefList = AttrDefList.substring(1, AttrDefList.length() - 1).replace("=", ":");
        return new_AttrDefList;
    }

}
