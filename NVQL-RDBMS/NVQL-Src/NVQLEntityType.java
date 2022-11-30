import java.sql.*;

public class NVQLEntityType {
    String entityTypeName;
    String attrDefList;

    NVQLEntityType(String entityTypeName, String attrDefList, Connection pgsqlCon) {
        this.entityTypeName = entityTypeName;
        this.attrDefList = attrDefList;
        try {
            if (!existsEntityType(entityTypeName, pgsqlCon))
                this.createEntityType(pgsqlCon);
            else {
                System.out.println("ERROR.NVQLEntityType: Entity <" + entityTypeName + "> already exists");
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("NVQLEntityType: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }

    }

    static Boolean existsEntityType(String entityTypeName, Connection pgsqlCon) throws Exception {
        String query;
        Boolean exists = false;

        try {
            query = "SELECT EXISTS (SELECT 1 FROM \"ENTITY_TYPE_DEF\" WHERE \"Entity_Type_Name\" ='" + entityTypeName
                    + "')";
            PreparedStatement statement = pgsqlCon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();

            if (result.getString(1).equals("t"))
                exists = true;
        } catch (Exception e) {
            System.out.println("NVQLEntityTypeDef: PostgreSQL Query Failed");
            System.out.println(e);
            System.exit(0);
        }
        return (exists);
    }

    void createEntityType(Connection pgsqlCon) throws Exception {

        String query = "INSERT INTO \"ENTITY_TYPE_DEF\" (\"Entity_Type_Name\",\"Entity_Type_Attr_Def_List\",\"Entity_Type_Unique_Attr_List\") VALUES ('"
                + this.entityTypeName + "','" + this.attrDefList + "','')";
        PreparedStatement posted = pgsqlCon.prepareStatement(query);
        posted.executeUpdate();
        System.out.println("1 row added Values: [(" + this.entityTypeName + ") , (" + this.attrDefList
                + ") , ()] inserted in Table 'ENTITY_TYPE_DEF'");

        String adl = this.attrDefList;
        String quelist = "";

        NVQLAttrDefList adlObject = new NVQLAttrDefList(adl);

        quelist = adlObject.convertADL4SQL();

        if (quelist != "")
            query = "CREATE TABLE \"" + this.entityTypeName + "\" (\"" + this.entityTypeName + "_Id\" SERIAL," + quelist
                    + ")";
        else
            query = "CREATE TABLE \"" + this.entityTypeName + "\" (\"" + this.entityTypeName + "_Id\" SERIAL)";

        posted = pgsqlCon.prepareStatement(query);
        posted.executeUpdate();
        System.out.println("Entity Type '" + this.entityTypeName + "' created successfully");
    }

    public static String getAttrDefList(String entityTypeName, Connection pgsqlCon) throws Exception {

        if (!existsEntityType(entityTypeName, pgsqlCon)) {
            System.out.println("ERROR.NVQLEntityType: Entity type <" + entityTypeName + "> is not defined");
            System.exit(0);
            return null;
        }

        String query = "SELECT \"Entity_Type_Attr_Def_List\" FROM \"ENTITY_TYPE_DEF\" WHERE \"Entity_Type_Name\" ='"
                + entityTypeName + "'";

        PreparedStatement statement1 = pgsqlCon.prepareStatement(query);
        ResultSet result1 = statement1.executeQuery();

        result1.next();

        return (result1.getString(1));
    }

}
