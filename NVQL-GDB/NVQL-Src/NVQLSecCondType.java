import java.sql.*;
import java.util.*;

public class NVQLSecCondType {

    static Boolean DEBUG = true;

    static String querydisplay(ArrayList<String> relationTypeNameList, ArrayList<String> EntityTypeNameList) {
        String SecCondDefSyntax = "";
        for (int i = 0; i < relationTypeNameList.size(); i++) {
            SecCondDefSyntax = SecCondDefSyntax + relationTypeNameList.get(i) + " {" + EntityTypeNameList.get(i) + "} ";
        }
        return SecCondDefSyntax;
    }

    static Boolean existsSecCondAttrType(String secCondTypeName, String attrType, Connection pgsqlCon) {

        Boolean exists = false;
        try {
            String query = "SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema= 'public' AND table_name = '"
                    + secCondTypeName + "' AND column_name = '" + attrType + "')";
            // System.out.println(query);
            PreparedStatement statement = pgsqlCon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();
            // System.out.println("@" + query);

            if (result.getString(1).equals("t"))
                exists = true;
        }

        catch (Exception e) {
            System.out.println("NVQLexistsSecCondAttrType: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }
        return exists;
    }

    static String convertERDL4SQL(ArrayList<String> EntRelDefList) {
        String erlist = "";
        for (int i = 0; i < EntRelDefList.size(); i++) {
            if (i == 0)
                erlist = erlist + "\"" + EntRelDefList.get(i).toString() + "\" TEXT";
            else
                erlist = erlist + ", \"" + EntRelDefList.get(i).toString() + "\" TEXT";
        }
        return erlist;
    }

    static void createSecCondType(String secCondTypeName, String attrDefList, ArrayList<String> relationTypeNameList,
            ArrayList<String> EntityTypeNameList, Connection pgsqlCon) {

        try {
            if (!existsSecCondType(secCondTypeName, pgsqlCon))
                beginSecCondType(secCondTypeName, attrDefList, relationTypeNameList, EntityTypeNameList, pgsqlCon);
            else {
                System.out.println(
                        "ERROR.NVQLSecCondType: Security-Condition-Type <" + secCondTypeName + "> already exists");
                System.exit(0);
            }
        }

        catch (Exception e) {
            System.out.println("NVQLSecCondType: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }

    }

    static Boolean existsSecCondType(String secCondTypeName, Connection pgsqlCon) throws Exception {
        String query;
        Boolean exists = false;

        try {

            query = "MATCH (n: `SECURITY_CONDITION_TYPE_DEF` : `" + secCondTypeName
                    + "`) RETURN CASE WHEN (COUNT(n) = 0) THEN \"f\" ELSE \"t\" END as n";
            PreparedStatement statement = pgsqlCon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();
            if(DEBUG)
                System.out.println("@" + query);

            if (result.getString(1).equals("t"))
                exists = true;
        } catch (Exception e) {
            System.out.println("NVQLSecCondTypeDef: PostgreSQL Query Failed");
            System.out.println(e);
            System.exit(0);
        }
        return (exists);
    }

    static void beginSecCondType(String secCondTypeName, String attrDefList, ArrayList<String> relationTypeNameList,
            ArrayList<String> EntityTypeNameList, Connection pgsqlCon) {
        try {
            for (int i = 0; i < relationTypeNameList.size(); i++) {

                if (!NVQLRelationType.existsRelationType(relationTypeNameList.get(i), pgsqlCon)) {
                    System.out.println("ERROR.NVQLSecCondType: Relation type <" + relationTypeNameList.get(i)
                            + "> is not defined");
                    System.exit(0);
                    return;
                }

                for (String ename : EntityTypeNameList.get(i).toString().split(",")) {

                    if (!NVQLEntityType.existsEntityType(ename, pgsqlCon)) {
                        System.out.println("ERROR.NVQLSecCondType: Entity type <" + ename + "> is not defined");
                        System.exit(0);
                        return;
                    }
                }

            }
            String query = "";
            if (NVQLEntityType.existsEntityType(secCondTypeName, pgsqlCon)) {
                // ====================================commented for testing
                // query = "DROP TABLE \"" + secCondTypeName + "\"";

                query = "MATCH (n: `ENTITY_TYPE_DEF` : `" + secCondTypeName
                        + "`{}) RETURN CASE WHEN (COUNT(n) = 0) THEN \"f\" ELSE \"t\" END as n";
                PreparedStatement statement2 = pgsqlCon.prepareStatement(query);
                ResultSet result2 = statement2.executeQuery();
                result2.next();
                if(DEBUG)
                    System.out.println("@" + query);
                // System.out.println("***********" + result2.getString(1));
                if (result2.getString(1).equals("f")) {

                    query = "CREATE ( n: `SECURITY_CONDITION_TYPE_DEF`: `" + secCondTypeName + "` {`" + secCondTypeName
                            + "_id`:\"integer\", `Entity_Type_Unique_Attr_List`:\"\"";
                    // System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    if (!attrDefList.isEmpty())
                        query = query + ",`" + attrDefList + "\"";

                    query = query + "})";
                    PreparedStatement posted = pgsqlCon.prepareStatement(query);
                    posted.executeUpdate();
                    if(DEBUG)
                        System.out.println("@##" + query);
                    System.out.println("1 node added Values: [(`" + secCondTypeName + ") , (" + attrDefList
                            + "\") , ()] inserted with label 'SECURITY_CONDITION_TYPE_DEF'");
                } else {

                    String node_tag = "(n: `ENTITY_TYPE_DEF` : `" + secCondTypeName + "`)";
                    query = "MATCH ";

                    String EntRelDefList = NVQLSecCondType.querydisplay(relationTypeNameList, EntityTypeNameList);
                    EntRelDefList = EntRelDefList.replaceAll(" +", "");
                    // System.out.println("^^" + EntRelDefList);
                    String temp = "", entity_hold = "", relation_hold = "";
                    int flag = 0, count = 0, count_rels = 0, next_flag = 0;
                    for (int i = 0; i < EntRelDefList.length(); i++) {

                        if (next_flag == 1) {
                            query = query + ", ";
                            next_flag = 0;
                        }

                        if (flag == 2) {
                            query = query + node_tag + " - [:`RELATION_TYPE_DEF:" + relation_hold + "`]->(n" + (count++)
                                    + ":ENTITY_TYPE_DEF:`" + entity_hold + "`),";
                            flag = 0;
                        } else if (flag == 1) {
                            query = query + node_tag + " - [:`RELATION_TYPE_DEF:" + relation_hold + "`]->(n" + (count++)
                                    + ":ENTITY_TYPE_DEF:`" + entity_hold + "`)";
                            flag = 0;
                        }

                        if (EntRelDefList.charAt(i) == '{') {
                            relation_hold = temp;
                            temp = "";
                            continue;
                        } else if (EntRelDefList.charAt(i) == ',') {
                            entity_hold = temp;
                            temp = "";
                            flag = 2;
                            // System.out.print(count_rels + " " + EntRelDefList.charAt(i));
                            count_rels++;
                            continue;
                        } else if (EntRelDefList.charAt(i) == '}') {

                            // System.out.print(count_rels + " " + EntRelDefList.charAt(i));
                            count_rels++;
                            entity_hold = temp;
                            query = query + node_tag + " - [:`RELATION_TYPE_DEF:" + relation_hold + "`]->(n" + (count++)
                                    + ":ENTITY_TYPE_DEF:`" + entity_hold + "`)";
                            temp = "";
                            entity_hold = "";
                            relation_hold = "";
                            flag = 0;
                            next_flag = 1;
                            continue;
                        }
                        temp = temp + EntRelDefList.charAt(i);

                    }

                    String query3 = "MATCH (n: `ENTITY_TYPE_DEF` : `" + secCondTypeName
                            + "`)-[]->() RETURN count(n) as count";

                    PreparedStatement check_statement3 = pgsqlCon.prepareStatement(query3);
                    ResultSet check_result3 = check_statement3.executeQuery();
                    check_result3.next();
                    if(DEBUG)
                        System.out.println("@" + query3);

                    if (!check_result3.getString(1).equals(String.valueOf(count_rels))) {
                        System.out.println(
                                "The definition is not exactly matching with the previously created relationships");
                        System.exit(0);
                    }

                    String query1 = query + " RETURN CASE WHEN (COUNT(n) = 1) THEN \"t\" ELSE \"f\" END as n";
                    // System.out.println("@@" + query1);
                    PreparedStatement check_statement = pgsqlCon.prepareStatement(query1);
                    ResultSet check_result = check_statement.executeQuery();
                    check_result.next();
                    if(DEBUG)
                        System.out.println("@" + query1);

                    if (!check_result.getString(1).equals("t")) {
                        System.out.println("Ambiguity while defining: More than one same node definition exists");
                        System.exit(0);
                    }
                    String query2 = query + " REMOVE n:ENTITY_TYPE_DEF:" + secCondTypeName
                            + " SET n:SECURITY_CONDITION_TYPE_DEF:" + secCondTypeName + ", n = {`" + secCondTypeName
                            + "_id`:\"integer\", `Entity_Relation_List`: \"" + EntRelDefList + "\"";

                    attrDefList = attrDefList.replace(":", "`:\"").replace(", ", "\", `");
                    if (!attrDefList.isEmpty())
                        query2 = query2 + ",`" + attrDefList + "\"";

                    query2 = query2 + "}";
                    // System.out.println("###" + query2);
                    PreparedStatement posted1 = pgsqlCon.prepareStatement(query2);
                    posted1.executeUpdate();
                    if(DEBUG)
                        System.out.println("@" + query2);
                        
                    System.out.println("1 Entity_Type got changed to Security_Condition_Type : [" + secCondTypeName
                            + "] and attributes got updated");

                }
            } else {
                System.out.println(
                        "Security-Condition_Type must be defined previously as Entity_Type [" + secCondTypeName + "]");
                System.exit(0);
            }

            System.out.println("Security Condition Type '" + secCondTypeName + "' definition created successfully");
        } catch (

        Exception e) {
            System.out.println("NVQLSecCondType: Neo4j Error");
            System.out.println(e);
            System.exit(0);
        }
    }

    public static String getAttrDefList(String secCondTypeName, Connection pgsqlCon) throws Exception {

        if (!existsSecCondType(secCondTypeName, pgsqlCon)) {
            System.out
                    .println("ERROR.NVQLSecCondType: Security Conditon type <" + secCondTypeName + "> is not defined");
            System.exit(0);
            return null;
        }

        

        // return (result1.getString(1));
        // String query = "MATCH (n: `SECURITY_CONDITION_TYPE_DEF`: `" + secCondTypeName
        // + "`) RETURN As props";
        String query = "MATCH (n: `SECURITY_CONDITION_TYPE_DEF`: `" + secCondTypeName
                + "`) RETURN apoc.map.removeKeys(n,['_id', 'Entity_Relation_List', '_labels']) As props";

        if(DEBUG)
            System.out.println("@" + query);
        PreparedStatement statement1 = pgsqlCon.prepareStatement(query);
        ResultSet result1 = statement1.executeQuery();

        result1.next();
        if(DEBUG)
            System.out.println("@" + query);

        return (result1.getObject(1).toString());

    }

    public static String getEntityRelationList(String secCondTypeName, Connection pgsqlCon) throws Exception {

        try {
            if (!existsSecCondType(secCondTypeName, pgsqlCon)) {
                System.out.println(
                        "ERROR.NVQLSecCondType: Security Conditon type <" + secCondTypeName + "> is not defined");
                System.exit(0);
                return null;
            }

            // String query = "SELECT \"Entity_Relation_List\" FROM
            // \"SECURITY_CONDITION_TYPE_DEF\" WHERE \"Security_Condition_Type_Name\" ='"
            // + secCondTypeName + "'";
            String query = "MATCH (p:SECURITY_CONDITION_TYPE_DEF:" + secCondTypeName
                    + ") RETURN p.Entity_Relation_List";
            PreparedStatement statement1 = pgsqlCon.prepareStatement(query);
            ResultSet result1 = statement1.executeQuery();
            result1.next();
            if(DEBUG)
                System.out.println("@" + query);

            return (result1.getString(1));
        } catch (Exception e) {
            System.out.println("NVQLSecCondType.getEntityRelationList(): PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }
        return null;
    }

    public static String getModAttrDefList(String AttrDefList, Connection pgsqlCon) throws Exception {

        String new_AttrDefList = AttrDefList.substring(1, AttrDefList.length() - 1).replace("=", ":");
        return new_AttrDefList;
    }

}
