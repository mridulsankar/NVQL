import java.util.ArrayList;
import java.sql.*;

public class NVQLSelect {

    static String querydisplay(String selectSyntax, ArrayList<ArrayList<String>> selectpred, ArrayList<String> relop) {

        // for (int i = 0; i < selectpred.size(); i++) {

        // if (i > 0)
        // selectSyntax = selectSyntax + " " + relop.get(i - 1);

        // for (int j = 0; j < selectpred.get(i).size(); j++) {

        // if (j == 0)
        // selectSyntax = selectSyntax + " (" + selectpred.get(i).get(j);
        // else if (selectpred.get(i).size() == 4 && (j == 1))
        // selectSyntax = selectSyntax + "." + selectpred.get(i).get(j);
        // else if (selectpred.get(i).size() == 5 && (j == 1 || j == 4))
        // selectSyntax = selectSyntax + ":" + selectpred.get(i).get(j);
        // else
        // selectSyntax = selectSyntax + " " + selectpred.get(i).get(j);

        // if (j == selectpred.get(i).size() - 1)
        // selectSyntax = selectSyntax + ")";
        // }
        // }
        return selectSyntax;
    }

    static void select(String vt, ArrayList<ArrayList<String>> selectpred, ArrayList<String> relop,
            Connection pgSQLCon) {
        String query = "";
        String Definitions[] = { "EXPLOIT_TYPE_DEF", "SECURITY_CONDITION_TYPE_DEF", "RELATION_TYPE_DEF",
                "ENTITY_TYPE_DEF" };
        // String name = vt.substring(vt.indexOf(':') + 1, vt.length());
        // // System.out.println("Name = " + name);
        // ArrayList<String> query = new ArrayList<String>();
        // ArrayList<String> tabjoin = new ArrayList<String>();
        // String attrName = "", oper, value = "", temp_query = "", tabcolName = "",
        // join_cond = "";
        // int flag1 = 0, flag2 = 0;
        // for (int i = 0; i < selectpred.size(); i++) {
        // switch (selectpred.get(i).size()) {
        // case 4:
        // attrName = selectpred.get(i).get(1);
        // attrName = "\"" + attrName + "\"";
        // oper = selectpred.get(i).get(2);
        // oper = oper.replace("==", "=");
        // value = selectpred.get(i).get(3).replace("\"", "'");

        // if (!tabjoin.contains("\"" + name + "\""))
        // tabjoin.add("\"" + name + "\"");
        // query.add("\"" + name + "\"." + attrName + " " + oper + " " + value);
        // flag1 = 1;
        // break;

        // case 5:
        // if (name.equals(selectpred.get(i).get(1))) {
        // attrName = "'" + selectpred.get(i).get(3) + "'";
        // tabcolName = "\"To_Entity_Name\"";
        // if (flag1 == 1 && flag2 == 1)
        // join_cond = " AND \"" + selectpred.get(i).get(2) + "\"." +
        // "\"From_Entity_Name\" = \"" + name
        // + "\"" + "." + "\"name\"";
        // else
        // join_cond = "";

        // } else if (name.equals(selectpred.get(i).get(4))) {
        // attrName = "'" + selectpred.get(i).get(0) + "'";
        // tabcolName = "\"From_Entity_Name\"";
        // if (flag1 == 1 && flag2 == 1)
        // join_cond = " AND " + selectpred.get(i).get(2) + "\"." + "\"To_Entity_Name\"
        // = \"" + name + "\""
        // + "." + "\"name\"";
        // else
        // join_cond = "";
        // }
        // // value = selectpred.get(i).get(3).replace("\"", "'");
        // if (!tabjoin.contains("\"" + selectpred.get(i).get(2).toString() + "\""))
        // tabjoin.add("\"" + selectpred.get(i).get(2) + "\"");
        // query.add("\"" + selectpred.get(i).get(2) + "\"." + tabcolName + " = " +
        // attrName + join_cond);
        // flag2 = 1;
        // break;

        // }
        // // System.out.println(query.get(i));
        // }
        // temp_query = "SELECT * FROM ";

        // for (int i = 0; i < tabjoin.size(); i++) {
        // if (i > 0)
        // temp_query = temp_query + " CROSS JOIN " + tabjoin.get(i);
        // else if (i == 0)
        // temp_query = temp_query + tabjoin.get(i);

        // }
        // temp_query = temp_query + " WHERE ";

        // for (int i = 0; i < selectpred.size(); i++) {

        // if (i > 0 && relop.get(i - 1).equals("or"))
        // temp_query = temp_query + " OR ";
        // else if (i > 0 && relop.get(i - 1).equals("and"))
        // temp_query = temp_query + " AND ";
        // temp_query = temp_query + "(" + query.get(i) + ")";

        // }
        // System.out.println(temp_query);

        try {
            // PreparedStatement stmt1 = pgSQLCon.prepareStatement("SELECT EXISTS (" +
            // temp_query + ")");
            // System.out.println("SELECT EXISTS (" + temp_query + ")");
            // for (int i = 0; i < selectpred.size(); i++) {
            // for (int j = 0; j < selectpred.get(i).size(); j++) {
            // System.out.print(" **" + selectpred.get(i).get(j));
            // }
            // System.out.println("%%%");

            // }
            // for (int i = 0; i < relop.size(); i++) {
            // System.out.println("###" + relop.get(i));
            // }
            // System.out.println("!!!" + vt.substring(vt.indexOf(":") + 1));

            query = "MATCH (" + vt.substring(0, vt.indexOf(":")) + ": `" + vt.substring(vt.indexOf(":") + 1)
                    + "`) RETURN CASE WHEN (COUNT(" + vt.substring(0, vt.indexOf(":"))
                    + ") = 0) THEN \"f\" ELSE \"t\" END as " + vt.substring(0, vt.indexOf(":"));

            PreparedStatement stmt1 = pgSQLCon.prepareStatement(query);
            // System.out.println(query);
            ResultSet result1 = stmt1.executeQuery();
            result1.next();
            // System.out.println("@" + query);
            // System.out.println("-->" + result1.getString(1));

            if (result1.getString(1).equals("t")) {

                for (int type_defs = 0; type_defs < Definitions.length; type_defs++) {
                    if (!Definitions[type_defs].equalsIgnoreCase("RELATION_TYPE_DEF")) {
                        query = "MATCH (" + vt.substring(0, vt.indexOf(":")) + ": `" + vt.substring(vt.indexOf(":") + 1)
                                + "`: `" + Definitions[type_defs] + "`) RETURN CASE WHEN (COUNT("
                                + vt.substring(0, vt.indexOf(":")) + ") = 0) THEN \"f\" ELSE \"t\" END as "
                                + vt.substring(0, vt.indexOf(":"));
                    } else {
                        query = "MATCH (a)-[" + vt.substring(0, vt.indexOf(":")) + ": `" + Definitions[type_defs] + ":"
                                + vt.substring(vt.indexOf(":") + 1) + "`]-(b) RETURN CASE WHEN (COUNT("
                                + vt.substring(0, vt.indexOf(":")) + ") = 0) THEN \"f\" ELSE \"t\" END as "
                                + vt.substring(0, vt.indexOf(":"));

                    }
                    PreparedStatement stmt2 = pgSQLCon.prepareStatement(query);
                    // System.out.println(query);
                    ResultSet result2 = stmt2.executeQuery();
                    result2.next();
                    // System.out.println("@" + query);
                    // System.out.println("-->" + result2.getString(1));

                    if (result2.getString(1).equals("t")) {
                        for (int k = 0; k < selectpred.size(); k++) {
                            if (!vt.substring(0, vt.indexOf(":")).equals(selectpred.get(k).get(0))) {
                                System.out.println(
                                        "The calling variable(s) of the property for the matched node doesn't match with the defined variable");
                                System.exit(0);
                            }
                        }
                        String conditions = "";
                        for (int i = 0; i < selectpred.size(); i++) {
                            if (i > 0)
                                conditions = conditions + " " + relop.get(i - 1) + " ";
                            for (int j = 0; j < selectpred.get(i).size(); j++) {
                                if (j == 1)
                                    conditions = conditions + "." + selectpred.get(i).get(j);
                                else {
                                    if (selectpred.get(i).get(j).equalsIgnoreCase("=="))
                                        conditions = conditions + "=";
                                    else
                                        conditions = conditions + selectpred.get(i).get(j);
                                }
                            }

                        }
                        // System.out.println(conditions);

                        if (!Definitions[type_defs].equalsIgnoreCase("RELATION_TYPE_DEF")) {
                            query = "MATCH (" + vt.substring(0, vt.indexOf(":")) + ": `"
                                    + vt.substring(vt.indexOf(":") + 1) + "`) WHERE " + conditions + " RETURN count("
                                    + vt.substring(0, vt.indexOf(":")) + ")";
                        } else {
                            query = "MATCH (a)-[" + vt.substring(0, vt.indexOf(":")) + ": `:"
                                    + vt.substring(vt.indexOf(":") + 1) + "`]-(b) WHERE " + conditions
                                    + " RETURN count(" + vt.substring(0, vt.indexOf(":")) + ")";

                        }
                        PreparedStatement stmt7 = pgSQLCon.prepareStatement(query);
                        // System.out.println(query);
                        ResultSet result7 = stmt7.executeQuery();
                        result7.next();
                        //System.out.println("@" + query);
                        if (Integer.parseInt(result7.getObject(1).toString()) <= 0) {
                            System.out.println("No such Node exists");
                            break;
                        }

                        if (!Definitions[type_defs].equalsIgnoreCase("RELATION_TYPE_DEF")) {
                            query = "MATCH (" + vt.substring(0, vt.indexOf(":")) + ": `"
                                    + vt.substring(vt.indexOf(":") + 1) + "`) WHERE " + conditions + " RETURN keys("
                                    + vt.substring(0, vt.indexOf(":")) + ")";
                        } else {
                            query = "MATCH (a)-[" + vt.substring(0, vt.indexOf(":")) + ": `:"
                                    + vt.substring(vt.indexOf(":") + 1) + "`]-(b) WHERE " + conditions + " RETURN keys("
                                    + vt.substring(0, vt.indexOf(":")) + ")";

                        }
                        PreparedStatement stmt3 = pgSQLCon.prepareStatement(query);
                        // System.out.println(query);
                        ResultSet result3 = stmt3.executeQuery();
                        result3.next();
                        System.out.println("MATCH FOUND: [" + Definitions[type_defs] + "]");
                        // System.out.println("@" + query);
                        System.out.println(result3.getObject(1).toString());
                        System.out.println("==================================================");

                        if (!Definitions[type_defs].equalsIgnoreCase("RELATION_TYPE_DEF")) {
                            query = "MATCH (" + vt.substring(0, vt.indexOf(":")) + ": `"
                                    + vt.substring(vt.indexOf(":") + 1) + "`) WHERE " + conditions + " RETURN "
                                    + result3.getObject(1).toString()
                                            .replace("[", "[" + vt.substring(0, vt.indexOf(":")) + ".")
                                            .replace(",", "," + vt.substring(0, vt.indexOf(":")) + ".")
                                            .replaceAll(" ", "").substring(1,
                                                    result3.getObject(1).toString()
                                                            .replace("[", "[" + vt.substring(0, vt.indexOf(":")) + ".")
                                                            .replace(",", "," + vt.substring(0, vt.indexOf(":")) + ".")
                                                            .replaceAll(" ", "").length() - 1);
                        } else {
                            query = "MATCH (a)-[" + vt.substring(0, vt.indexOf(":")) + ": `:"
                                    + vt.substring(vt.indexOf(":") + 1) + "`]-(b) WHERE " + conditions + " RETURN "
                                    + result3.getObject(1).toString()
                                            .replace("[", "[" + vt.substring(0, vt.indexOf(":")) + ".")
                                            .replace(",", "," + vt.substring(0, vt.indexOf(":")) + ".")
                                            .replaceAll(" ", "").substring(1,
                                                    result3.getObject(1).toString()
                                                            .replace("[", "[" + vt.substring(0, vt.indexOf(":")) + ".")
                                                            .replace(",", "," + vt.substring(0, vt.indexOf(":")) + ".")
                                                            .replaceAll(" ", "").length() - 1);

                        }
                        // System.out.println(result3.getObject(1).toString());
                        // System.out.println(result3.getObject(1).toString().replace("[",
                        // "[" + vt.substring(0, vt.indexOf(":")) + "."));
                        // System.out.println(result3.getObject(1).toString()
                        // .replace("[", "[" + vt.substring(0, vt.indexOf(":")) + ".")
                        // .replace(",", "," + vt.substring(0, vt.indexOf(":")) + ".").replaceAll(" ",
                        // "")
                        // .substring(1,
                        // result3.getObject(1).toString()
                        // .replace("[", "[" + vt.substring(0, vt.indexOf(":")) + ".")
                        // .replace(",", "," + vt.substring(0, vt.indexOf(":")) + ".")
                        // .replaceAll(" ", "").length() - 1));
                        // System.exit(0);
                        PreparedStatement stmt4 = pgSQLCon.prepareStatement(query);
                        // System.out.println(query);
                        ResultSet result4 = stmt4.executeQuery();
                        result4.next();
                        // System.out.println("@" + query);
                        System.out.print("[");
                        int ctr = 0;
                        for (int ch = 0; ch < result3.getObject(1).toString().length(); ch++) {
                            if (result3.getObject(1).toString().charAt(ch) == ',')
                                ctr++;
                        }

                        for (int loop = 0; loop <= ctr; loop++) {
                            if (loop == 0)
                                System.out.print(" " + String.valueOf(result4.getObject(loop + 1)));
                            else
                                System.out.print(", " + String.valueOf(result4.getObject(loop + 1)));
                        }
                        System.out.println("]");

                        if (!Definitions[type_defs].equalsIgnoreCase("RELATION_TYPE_DEF")) {
                            query = "MATCH (" + vt.substring(0, vt.indexOf(":")) + ": `"
                                    + vt.substring(vt.indexOf(":") + 1) + "`) WHERE " + conditions + " RETURN labels("
                                    + vt.substring(0, vt.indexOf(":")) + ")";
                        } else {
                            query = "MATCH (a)-[" + vt.substring(0, vt.indexOf(":")) + ": `:"
                                    + vt.substring(vt.indexOf(":") + 1) + "`]-(b) WHERE " + conditions
                                    + " RETURN labels(" + vt.substring(0, vt.indexOf(":")) + ")";

                        }
                        PreparedStatement stmt5 = pgSQLCon.prepareStatement(query);
                        // System.out.println(query);
                        ResultSet result5 = stmt5.executeQuery();
                        result5.next();
                        // System.out.println("@" + query);
                        System.out.println("LABELS OF THE NODE: " + result5.getObject(1).toString());
                        // break;

                    }
                }

                /*
                 * PreparedStatement stmt2 = pgSQLCon.prepareStatement(query); ResultSet result2
                 * = stmt2.executeQuery(); ResultSetMetaData rsmd2 = result2.getMetaData(); int
                 * columnsNumber2 = rsmd2.getColumnCount(); String temp = "";
                 * 
                 * for (int l = 0; l < columnsNumber2; l++) { temp = temp +
                 * rsmd2.getColumnName(l + 1) + " || "; }
                 * 
                 * System.out.println(temp); temp = "";
                 * 
                 * ArrayList<String> ans3 = new ArrayList<String>(); int records = 0;
                 * System.out.println(
                 * "=================================================================================================================="
                 * ); while (result2.next()) { for (int l = 0; l < columnsNumber2; l++) { temp =
                 * temp + result2.getString(l + 1) + " || "; } System.out.println(temp);
                 * ans3.add(temp); temp = ""; records++; }
                 * 
                 * System.out.println(
                 * "===================================================================================================================="
                 * ); if (records > 0) System.out.println("[" + records +
                 * "] Records from have been found");
                 */
            } else
                System.out.println("No Records of " + vt.substring(vt.indexOf(":") + 1) + " from have been found");

        } catch (Exception e) {
            System.out.println("NVQLSelect: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }

    }

}