import java.sql.*;
import java.util.*;

public class NVQLRelationType {
    String relationTypeName;
    String attrDefList;
    String srcEntityType;
    String tgtEntityType;
    ArrayList<String> srcEntityTypeList = new ArrayList<String>();
    ArrayList<String> tgtEntityTypeList = new ArrayList<String>();

    ArrayList<String> entityNames(String EntityList) {

        ArrayList<String> enameList = new ArrayList<String>();
        EntityList = EntityList.trim().replace(" ", "");
        StringTokenizer st = new StringTokenizer(EntityList, ",");
        while (st.hasMoreTokens()) {
            StringTokenizer t = new StringTokenizer(st.nextToken(), ",");
            enameList.add(t.nextToken());
        }
        return enameList;
    }

    NVQLRelationType(String relationTypeName, String attrDefList, String srcEntityType, String tgtEntityType,
            Connection pgsqlCon) {
        this.relationTypeName = relationTypeName;
        this.attrDefList = attrDefList;
        this.srcEntityType = srcEntityType;
        this.tgtEntityType = tgtEntityType;
        srcEntityTypeList = entityNames(srcEntityType);
        tgtEntityTypeList = entityNames(tgtEntityType);

        try {
            if (!existsRelationType(relationTypeName, pgsqlCon))
                this.createRelationType(pgsqlCon);
            else {
                System.out.println("ERROR.NVQLRelationType: Relation <" + relationTypeName + "> already exists");
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("NVQLRelationType: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }
    }

    static Boolean existsRelationType(String RelationTypeName, Connection pgsqlCon) throws Exception {
        String query;
        Boolean exists = false;

        try {
            query = "SELECT EXISTS (SELECT 1 FROM \"RELATION_TYPE_DEF\" WHERE \"Relation_Type_Name\" ='"
                    + RelationTypeName + "')";
            PreparedStatement statement = pgsqlCon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();

            if (result.getString(1).equals("t"))
                exists = true;
        } catch (Exception e) {
            System.out.println("NVQLRelationTypeDef: PostgreSQL Query Failed");
            System.out.println(e);
            System.exit(0);
        }

        return (exists);
    }

    void createRelationType(Connection pgsqlCon) throws Exception {

        String query = "", quelist = "";
        for (int i = 0; i < srcEntityTypeList.size(); i++) {
            query = "SELECT EXISTS (SELECT 1 FROM \"ENTITY_TYPE_DEF\" WHERE \"Entity_Type_Name\" ='"
                    + srcEntityTypeList.get(i) + "')";
            PreparedStatement statement1 = pgsqlCon.prepareStatement(query);
            ResultSet result1 = statement1.executeQuery();
            result1.next();

            if (result1.getString(1).equals("f")) {
                System.out.println("ERROR.NVQLRelationType: Entity '" + srcEntityTypeList.get(i) + "' is not defined");
                System.exit(0);
                return;
            }
        }

        for (int i = 0; i < tgtEntityTypeList.size(); i++) {
            query = "SELECT EXISTS (SELECT 1 FROM \"ENTITY_TYPE_DEF\" WHERE \"Entity_Type_Name\" ='"
                    + tgtEntityTypeList.get(i) + "')";
            PreparedStatement statement2 = pgsqlCon.prepareStatement(query);
            ResultSet result2 = statement2.executeQuery();
            result2.next();

            if (result2.getString(1).equals("f")) {
                System.out.println("ERROR.NVQLRelationType: Entity '" + tgtEntityTypeList.get(i) + "' is not defined");
                System.exit(0);
                return;
            }
        }

        query = "INSERT INTO \"RELATION_TYPE_DEF\" (\"Relation_Type_Name\", \"From_Entity_Type_Name\", \"To_Entity_Type_Name\","
                + "\"Relation_Type_Attr_Def_List\", \"Relation_Type_Cardinality_Constraint\")" + "VALUES ('"
                + relationTypeName + "','" + srcEntityType + "','" + tgtEntityType + "','" + attrDefList + "','')";

        PreparedStatement posted = pgsqlCon.prepareStatement(query);
        int num = posted.executeUpdate();
        System.out.println(num + " row(s) added Values: [(" + relationTypeName + ") , (" + srcEntityType + ") , ("
                + tgtEntityType + "), (" + attrDefList + "), ()] inserted in Table 'ENTITY_TYPE_DEF'");

        NVQLAttrDefList adlObject = new NVQLAttrDefList(attrDefList);
        quelist = adlObject.convertADL4SQL();

        if (quelist != "")
            query = "CREATE TABLE \"" + relationTypeName + "\" (\"" + relationTypeName
                    + "_Id\" SERIAL, \"From_Entity_Name\" TEXT," + " \"To_Entity_Name\" TEXT, " + quelist + ")";
        else
            query = "CREATE TABLE \"" + relationTypeName + "\" (\"" + relationTypeName
                    + "_Id\" SERIAL, \"From_Entity_Name\" TEXT," + " \"To_Entity_Name\" TEXT)";

        posted = pgsqlCon.prepareStatement(query);
        posted.executeUpdate();
        System.out.println("Table '" + relationTypeName + "' created successfully");

    }

    public static String getAttrDefList(String relationTypeName, Connection pgsqlCon) {

        try {
            if (!existsRelationType(relationTypeName, pgsqlCon)) {
                System.out.println("ERROR.NVQLRelationType: Relation type <" + relationTypeName + "> is not defined");
                System.exit(0);
                return null;
            }

            String query = "SELECT \"Relation_Type_Attr_Def_List\" FROM \"RELATION_TYPE_DEF\" WHERE \"Relation_Type_Name\" ='"
                    + relationTypeName + "'";

            PreparedStatement statement1 = pgsqlCon.prepareStatement(query);
            ResultSet result1 = statement1.executeQuery();

            result1.next();

            return (result1.getString(1));
        } catch (Exception e) {
            System.out.println("NVQLRelation: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }
        return null;
    }

}
