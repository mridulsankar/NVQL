import java.sql.*;
import java.util.*;

public class NVQLRelationType {
    String relationTypeName;
    String attrDefList;
    String srcEntityType;
    String tgtEntityType;
    ArrayList<String> srcEntityTypeList = new ArrayList<String>();
    ArrayList<String> tgtEntityTypeList = new ArrayList<String>();
    static Boolean DEBUG = true;

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
            Connection neo4jCon) {
        this.relationTypeName = relationTypeName;
        this.attrDefList = attrDefList.replace(":", "`:\"").replace(", ", "\", `");
        this.srcEntityType = srcEntityType;
        this.tgtEntityType = tgtEntityType;
        srcEntityTypeList = entityNames(srcEntityType);
        tgtEntityTypeList = entityNames(tgtEntityType);

        try {
            if (!existsRelationType(relationTypeName, neo4jCon))
                this.createRelationType(neo4jCon);
            else {
                System.out.println("ERROR.NVQLRelationType: Relation <" + relationTypeName + "> already exists");
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("NVQLRelationType: Neo4j Error");
            System.out.println(e);
            System.exit(0);
        }
    }

    static Boolean existsRelationType(String RelationTypeName, Connection neo4jCon) throws Exception {
        String query;
        Boolean exists = false;

        try {
            // query = "SELECT EXISTS (SELECT 1 FROM \"RELATION_TYPE_DEF\" WHERE
            // \"Relation_Type_Name\" ='"
            // + RelationTypeName + "')";
            query = "MATCH (a)-[n: `RELATION_TYPE_DEF:" + RelationTypeName
                    + "`]->(b) RETURN CASE WHEN (COUNT(n) = 0) THEN \"f\" ELSE \"t\" END as n";
            PreparedStatement statement = neo4jCon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();
            if(DEBUG)
                System.out.println("@1rt" + query);

            if (result.getString(1).equals("t"))
                exists = true;
        } catch (Exception e) {
            System.out.println("NVQLRelationTypeDef: Neo4j Query Failed");
            System.out.println(e);
            System.exit(0);
        }

        return (exists);
    }

    void createRelationType(Connection neo4jCon) throws Exception {

        String query = "";
        for (int i = 0; i < srcEntityTypeList.size(); i++) {
            // query = "SELECT EXISTS (SELECT 1 FROM \"ENTITY_TYPE_DEF\" WHERE
            // \"Entity_Type_Name\" ='"
            // + srcEntityTypeList.get(i) + "')";
            query = "MATCH (n: `ENTITY_TYPE_DEF` : `" + srcEntityTypeList.get(i)
                    + "`) RETURN CASE WHEN (COUNT(n) = 0) THEN \"f\" ELSE \"t\" END as n";
            PreparedStatement statement1 = neo4jCon.prepareStatement(query);
            ResultSet result1 = statement1.executeQuery();
            result1.next();
            if(DEBUG)
                System.out.println("@2rt" + query);

            if (result1.getString(1).equals("f")) {
                System.out.println("ERROR.NVQLRelationType: Entity '" + srcEntityTypeList.get(i) + "' is not defined");
                System.exit(0);
                return;
            }
        }

        for (int i = 0; i < tgtEntityTypeList.size(); i++) {
            // query = "SELECT EXISTS (SELECT 1 FROM \"ENTITY_TYPE_DEF\" WHERE
            // \"Entity_Type_Name\" ='"
            // + tgtEntityTypeList.get(i) + "')";

            query = "MATCH (n: `ENTITY_TYPE_DEF` : `" + tgtEntityTypeList.get(i)
                    + "`) RETURN CASE WHEN (COUNT(n) = 0) THEN \"f\" ELSE \"t\" END as n";
            PreparedStatement statement2 = neo4jCon.prepareStatement(query);
            ResultSet result2 = statement2.executeQuery();
            result2.next();
            if(DEBUG)
                System.out.println("@3rt" + query);

            if (result2.getString(1).equals("f")) {
                System.out.println("ERROR.NVQLRelationType: Entity '" + tgtEntityTypeList.get(i) + "' is not defined");
                System.exit(0);
                return;
            }
        }

        // query = "INSERT INTO \"RELATION_TYPE_DEF\" (\"Relation_Type_Name\",
        // \"From_Entity_Type_Name\", \"To_Entity_Type_Name\","
        // + "\"Relation_Type_Attr_Def_List\",
        // \"Relation_Type_Cardinality_Constraint\")" + "VALUES ('"
        // + relationTypeName + "','" + srcEntityType + "','" + tgtEntityType + "','" +
        // attrDefList + "','')";

        // System.out.println("#$" + this.attrDefList);
        for (int i = 0; i < srcEntityTypeList.size(); i++) {
            for (int j = 0; j < tgtEntityTypeList.size(); j++) {
                query = "MATCH (a:`ENTITY_TYPE_DEF`:`" + srcEntityTypeList.get(i) + "`),(b:`ENTITY_TYPE_DEF`:`"
                        + tgtEntityTypeList.get(j) + "`) CREATE (a)-[r:`RELATION_TYPE_DEF:" + relationTypeName + "` {`"
                        + relationTypeName + "_id`:\"integer\",`Relation_Type_Cardinality_Constraint`:\"\"";
                if (!this.attrDefList.isEmpty())
                    query = query + ",`" + this.attrDefList + "\"";
                query = query + "}]->(b) RETURN r";
                PreparedStatement posted = neo4jCon.prepareStatement(query);
                int num = posted.executeUpdate();

                if(DEBUG)
                    System.out.println("@4rt" + query);
                if (!attrDefList.isEmpty())
                    System.out.println(num + " edge(s) added Values: [(" + relationTypeName + ") , ("
                            + srcEntityTypeList.get(i) + ") , (" + tgtEntityTypeList.get(j) + "), (`" + attrDefList
                            + "\")] inserted in 'ENTITY_TYPE_DEF'");
                else
                    System.out.println(num + " edge(s) added Values: [(" + relationTypeName + ") , ("
                            + srcEntityTypeList.get(i) + ") , (" + tgtEntityTypeList.get(j) + "), (" + attrDefList
                            + ")] inserted in 'ENTITY_TYPE_DEF'");
            }
        }

        // NVQLAttrDefList adlObject = new NVQLAttrDefList(attrDefList);
        // quelist = adlObject.convertADL4SQL();
        // System.out.println(quelist);

        /*
         * if (quelist != "") query = "CREATE TABLE \"" + relationTypeName + "\" (\"" +
         * relationTypeName + "_Id\" SERIAL, \"From_Entity_Name\" TEXT," +
         * " \"To_Entity_Name\" TEXT, " + quelist + ")"; else query = "CREATE TABLE \""
         * + relationTypeName + "\" (\"" + relationTypeName +
         * "_Id\" SERIAL, \"From_Entity_Name\" TEXT," + " \"To_Entity_Name\" TEXT)";
         * 
         * posted = neo4jCon.prepareStatement(query); posted.executeUpdate();
         * System.out.println("@" + query);
         */
        System.out.println("Definition of Relationship Type '" + relationTypeName + "'  created successfully");

    }

    public static String getAttrDefList(String relationTypeName, Connection neo4jCon) {

        try {
            if (!existsRelationType(relationTypeName, neo4jCon)) {
                System.out.println("ERROR.NVQLRelationType: Relation type <" + relationTypeName + "> is not defined");
                System.exit(0);
                return null;
            }

            // String query = "SELECT \"Relation_Type_Attr_Def_List\" FROM
            // \"RELATION_TYPE_DEF\" WHERE \"Relation_Type_Name\" ='"
            // + relationTypeName + "'";
            String query = "MATCH (a)-[n: `RELATION_TYPE_DEF:" + relationTypeName
                    + "`]->(b) RETURN apoc.map.removeKey(n,'Relation_Type_Cardinality_Constraint') As props";

            PreparedStatement statement1 = neo4jCon.prepareStatement(query);
            ResultSet result1 = statement1.executeQuery();

            result1.next();
            // System.out.println("@5rt" + query);
            // System.out.println("@" + result1.getString(1));
            return (result1.getObject(1).toString());
        } catch (Exception e) {
            System.out.println("NVQLRelation: Neo4j Error");
            System.out.println(e);
            System.exit(0);
        }
        return null;
    }

    public static String getModAttrDefList(String AttrDefList, Connection neo4jCon) throws Exception {

        String new_AttrDefList = AttrDefList.substring(1, AttrDefList.length() - 1).replace("=", ":");
        return new_AttrDefList;
    }

}
