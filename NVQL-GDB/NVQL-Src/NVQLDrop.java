import java.util.ArrayList;
import java.sql.*;

public class NVQLDrop {

    NVQLDrop(String dropList, Connection neo4jCon) {

        String category = "";
        String tableName = "";
        String colNamePrefix = "";

        category = dropList.substring(0, dropList.indexOf('(') - 1);

        if (category.equals("entity-type")) {
            tableName = "ENTITY_TYPE_DEF";
            colNamePrefix = "Entity_Type";
        } else if (category.equals("relation-type")) {
            tableName = "RELATION_TYPE_DEF";
            colNamePrefix = "Relation_Type";
        } else if (category.equals("security-condition-type")) {
            tableName = "SECURITY_CONDITION_TYPE_DEF";
            colNamePrefix = "Security_Condition_Type";
        } else if (category.equals("exploit-type")) {
            tableName = "EXPLOIT_TYPE_DEF";
            colNamePrefix = "Exploit_Type";
        }

        if (tableName == "") {
            System.out.println("ERROR.NVQLDrop: Unrecognized Type");
            System.exit(0);
            // System.out.println("entity-type/relation-type/security-condition-type/exploit-type");
        } else {
            ArrayList<String> condlist = new ArrayList<String>();
            String tablist = "";
            String temp = "";

            for (int i = dropList.indexOf('(') + 1; i <= dropList.indexOf(')'); i++) {
                if (dropList.charAt(i) == ',' || dropList.charAt(i) == ')') {
                    tablist = tablist + temp;
                    temp = "";
                    condlist.add(tablist);
                    tablist = "";
                } else if (dropList.charAt(i) != ',' || dropList.charAt(i) != ' ') {
                    temp = temp + dropList.charAt(i);
                }
            }

            try {

                for (int i = 0; i < condlist.size(); i++) {

                    // String query = "SELECT EXISTS (SELECT 1 FROM \"" + tableName + "\" WHERE " +
                    // condlist.get(i) + ")";
                    String query = "MATCH (n: `" + tableName + "` : `" + condlist.get(i)
                            + "`) RETURN CASE WHEN (COUNT(n) = 0) THEN \"f\" ELSE \"t\" END as n";
                    // System.out.println(query);
                    PreparedStatement statement = neo4jCon.prepareStatement(query);
                    ResultSet result = statement.executeQuery();
                    result.next();
                    // System.out.println("@" + query + "#" + result.getString(1));

                    if (result.getString(1).equals("f")) {
                        System.out.println("WARNING.NVQLDrop: " + condlist.get(i) + " Does not exist");
                    } else {

                        /*
                         * UNCOMMENT THE BELOW LINES KEPT FOR FUTURE USE WHEN SECURITY CONDITION GETS
                         * ADDED.
                         */
                        // temp = condlist.get(i).substring(condlist.get(i).indexOf("'") + 1,
                        // condlist.get(i).lastIndexOf("'"));
                        // System.out.println(condlist.get(i).lastIndexOf("'"));
                        // if (tableName.equals("ENTITY_TYPE_DEF")) {
                        // query = "SELECT EXISTS (SELECT 1 FROM \"SECURITY_CONDITION_TYPE_DEF\" WHERE "
                        // + condlist
                        // .get(i).toString().replace("Entity_Type_Name",
                        // "Security_Condition_Type_Name")
                        // + ")";
                        // System.out.println(query);
                        // PreparedStatement statement1 = neo4jCon.prepareStatement(query);
                        // ResultSet result1 = statement1.executeQuery();
                        // result1.next();
                        // System.out.println("@" + query);
                        // if (result1.getString(1).equals("t")) {
                        // System.out.println("WARNING.NVQLDrop: Security Condition " + temp + " also
                        // exists");
                        // continue;
                        // }
                        // }
                        /*
                         * UNCOMMENT THE ABOVE LINES KEPT FOR FUTURE USE WHEN SECURITY CONDITION GETS
                         * ADDED.
                         */
                        String node_index = "";
                        query = "MATCH (n:`" + tableName + "` : `" + condlist.get(i)
                                + "`) RETURN (n.`Entity_Type_Unique_Attr_List`)";
                        PreparedStatement statement2 = neo4jCon.prepareStatement(query);
                        ResultSet result2 = statement2.executeQuery();
                        result2.next();
                        // System.out.println("@" + query);
                        node_index = result2.getString(1);
                        // System.out.println("here1");
                        // System.out.println(result2.getString(1));
                        if (result2.getString(1) != null && !node_index.isEmpty()) {
                            // System.out.println("here3");
                            query = "DROP CONSTRAINT ON (n:`" + condlist.get(i) + "`) ASSERT (n.`"
                                    + node_index.replaceAll(",", "`,n.`") + "`) IS NODE KEY";
                            // System.out.println(query);
                            PreparedStatement posted2 = neo4jCon.prepareStatement(query);
                            posted2.executeUpdate();
                            // System.out.println("@" + query);
                            System.out.println("Unique Constraint(s) removed for: [" + condlist.get(i) + "] ");

                            query = "MATCH (n:`" + tableName + "` : `" + condlist.get(i)
                                    + "`) SET n.`Entity_Type_Unique_Attr_List` = \"\"";
                            // System.out.println(query);
                            PreparedStatement statement3 = neo4jCon.prepareStatement(query);
                            int rowAffected1 = statement3.executeUpdate();
                            // System.out.println("@" + query);
                            if (rowAffected1 > 0)
                                System.out.println(
                                        "NODE with LABEL '" + tableName + "' unique constraint updated successfully.");

                        }

                        // System.out.println("here2");
                        // query = "DELETE FROM \"" + tableName + "\" WHERE " + condlist.get(i);
                        query = "MATCH (n:`" + tableName + "` : `" + condlist.get(i) + "`) DETACH DELETE (n)";
                        // System.out.println(query);
                        PreparedStatement posted = neo4jCon.prepareStatement(query);
                        int num2 = posted.executeUpdate();
                        // System.out.println("@" + query);
                        System.out.println(num2 + " Node Definition deleted: [" + condlist.get(i) + "]");

                        query = "MATCH (n:`" + condlist.get(i) + "`) DETACH DELETE (n)";
                        // System.out.println(query);
                        PreparedStatement posted1 = neo4jCon.prepareStatement(query);
                        int num1 = posted1.executeUpdate();
                        // System.out.println("@" + query);
                        System.out.println(num1 + " Node(s) deleted with label: [" + condlist.get(i) + "] ");

                    }
                    /*
                     * query = "CALL db.indexes()"; // System.out.println(query); PreparedStatement
                     * statement2 = neo4jCon.prepareStatement(query); ResultSet result2 =
                     * statement2.executeQuery(); System.out.println("@" + query); while
                     * (result2.next()) {
                     * 
                     * String node_name = result2.getObject(3).toString().substring(1,
                     * result2.getObject(3).toString().length() - 1); String node_index =
                     * result2.getObject(4).toString().substring(1,
                     * result2.getObject(4).toString().length() - 1); //
                     * System.out.println(node_name); // System.out.println(condlist.get(i)); if
                     * (node_name.equals(condlist.get(i))) {
                     */
                    // }
                    // }
                }

            } catch (Exception e) {
                System.out.println("NVQLDrop: Neo4j Error");
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}