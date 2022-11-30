import java.util.ArrayList;
import java.sql.*;

public class NVQLDrop {

    NVQLDrop(String dropList, Connection pgsqlCon) {

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
                    tablist = tablist + "\"" + colNamePrefix + "_Name\" = '" + temp + "'";
                    temp = "";
                    condlist.add(tablist);
                    tablist = "";
                } else if (dropList.charAt(i) != ',' || dropList.charAt(i) != ' ') {
                    temp = temp + dropList.charAt(i);
                }
            }

            try {

                for (int i = 0; i < condlist.size(); i++) {
                    String query = "SELECT EXISTS (SELECT 1 FROM \"" + tableName + "\" WHERE " + condlist.get(i) + ")";
                    // System.out.println(query);
                    PreparedStatement statement = pgsqlCon.prepareStatement(query);
                    ResultSet result = statement.executeQuery();
                    result.next();

                    if (result.getString(1).equals("f")) {
                        System.out.println("WARNING.NVQLDrop: " + condlist.get(i) + " Does not exist");
                    } else {

                        query = "DELETE FROM \"" + tableName + "\" WHERE " + condlist.get(i);
                        // System.out.println(query);
                        PreparedStatement posted = pgsqlCon.prepareStatement(query);
                        int num = posted.executeUpdate();
                        System.out.println(num + " row(s) deleted Values: [" + condlist.get(i) + "]");

                        temp = condlist.get(i).substring(condlist.get(i).indexOf("'") + 1,
                                condlist.get(i).lastIndexOf("'"));
                        // System.out.println(condlist.get(i).lastIndexOf("'"));

                        if (tableName.equals("ENTITY_TYPE_DEF")) {
                            query = "SELECT EXISTS (SELECT 1 FROM \"SECURITY_CONDITION_TYPE_DEF\" WHERE " + condlist
                                    .get(i).toString().replace("Entity_Type_Name", "Security_Condition_Type_Name")
                                    + ")";
                            // System.out.println(query);
                            PreparedStatement statement1 = pgsqlCon.prepareStatement(query);
                            ResultSet result1 = statement1.executeQuery();
                            result1.next();
                            if (result1.getString(1).equals("t")) {
                                System.out.println("WARNING.NVQLDrop: Security Condition " + temp + " also exists");
                                continue;
                            }
                        }
                        query = "DROP TABLE \"" + temp + "\"";
                        // System.out.println(query);
                        PreparedStatement posted1 = pgsqlCon.prepareStatement(query);
                        posted1.executeUpdate();
                        System.out.println("1 Table deleted: [" + temp + "] ");

                    }
                }
            } catch (Exception e) {
                System.out.println("NVQLDrop: PostgreSQL Error");
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}