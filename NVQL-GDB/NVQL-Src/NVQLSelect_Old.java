import java.util.ArrayList;
import java.sql.*;

public class NVQLSelect_Old {

    static String querydisplay(String selectSyntax, ArrayList<ArrayList<String>> selectpred, ArrayList<String> relop) {

        for (int i = 0; i < selectpred.size(); i++) {

            if (i > 0)
                selectSyntax = selectSyntax + " " + relop.get(i - 1);

            for (int j = 0; j < selectpred.get(i).size(); j++) {

                if (j == 0)
                    selectSyntax = selectSyntax + " (" + selectpred.get(i).get(j);
                else if (selectpred.get(i).size() == 4 && (j == 1))
                    selectSyntax = selectSyntax + "." + selectpred.get(i).get(j);
                else if (selectpred.get(i).size() == 5 && (j == 1 || j == 4))
                    selectSyntax = selectSyntax + ":" + selectpred.get(i).get(j);
                else
                    selectSyntax = selectSyntax + " " + selectpred.get(i).get(j);

                if (j == selectpred.get(i).size() - 1)
                    selectSyntax = selectSyntax + ")";
            }
        }
        return selectSyntax;
    }

    static void select(String vt, ArrayList<ArrayList<String>> selectpred, ArrayList<String> relop,
            Connection pgSQLCon) {

        String name = vt.substring(vt.indexOf(':') + 1, vt.length());
        // System.out.println("Name = " + name);
        ArrayList<String> query = new ArrayList<String>();
        ArrayList<String> tabjoin = new ArrayList<String>();
        String attrName = "", oper, value = "", temp_query = "", tabcolName = "", join_cond = "";
        int flag1 = 0, flag2 = 0;
        for (int i = 0; i < selectpred.size(); i++) {
            switch (selectpred.get(i).size()) {
            case 4:
                attrName = selectpred.get(i).get(1);
                attrName = "\"" + attrName + "\"";
                oper = selectpred.get(i).get(2);
                oper = oper.replace("==", "=");
                value = selectpred.get(i).get(3).replace("\"", "'");

                if (!tabjoin.contains("\"" + name + "\""))
                    tabjoin.add("\"" + name + "\"");
                query.add("\"" + name + "\"." + attrName + " " + oper + " " + value);
                flag1 = 1;
                break;

            case 5:
                if (name.equals(selectpred.get(i).get(1))) {
                    attrName = "'" + selectpred.get(i).get(3) + "'";
                    tabcolName = "\"To_Entity_Name\"";
                    if (flag1 == 1 && flag2 == 1)
                        join_cond = " AND \"" + selectpred.get(i).get(2) + "\"." + "\"From_Entity_Name\" = \"" + name
                                + "\"" + "." + "\"name\"";
                    else
                        join_cond = "";

                } else if (name.equals(selectpred.get(i).get(4))) {
                    attrName = "'" + selectpred.get(i).get(0) + "'";
                    tabcolName = "\"From_Entity_Name\"";
                    if (flag1 == 1 && flag2 == 1)
                        join_cond = " AND " + selectpred.get(i).get(2) + "\"." + "\"To_Entity_Name\" = \"" + name + "\""
                                + "." + "\"name\"";
                    else
                        join_cond = "";
                }
                // value = selectpred.get(i).get(3).replace("\"", "'");
                if (!tabjoin.contains("\"" + selectpred.get(i).get(2).toString() + "\""))
                    tabjoin.add("\"" + selectpred.get(i).get(2) + "\"");
                query.add("\"" + selectpred.get(i).get(2) + "\"." + tabcolName + " = " + attrName + join_cond);
                flag2 = 1;
                break;

            }
            // System.out.println(query.get(i));
        }
        temp_query = "SELECT * FROM ";

        for (int i = 0; i < tabjoin.size(); i++) {
            if (i > 0)
                temp_query = temp_query + " CROSS JOIN " + tabjoin.get(i);
            else if (i == 0)
                temp_query = temp_query + tabjoin.get(i);

        }
        temp_query = temp_query + " WHERE ";

        for (int i = 0; i < selectpred.size(); i++) {

            if (i > 0 && relop.get(i - 1).equals("or"))
                temp_query = temp_query + " OR ";
            else if (i > 0 && relop.get(i - 1).equals("and"))
                temp_query = temp_query + " AND ";
            temp_query = temp_query + "(" + query.get(i) + ")";

        }
        // System.out.println(temp_query);

        try {
            PreparedStatement stmt1 = pgSQLCon.prepareStatement("SELECT EXISTS (" + temp_query + ")");
            System.out.println("SELECT EXISTS (" + temp_query + ")");
            ResultSet result1 = stmt1.executeQuery();
            result1.next();
            System.out.println("@" + query);

            if (result1.getString(1).equals("t")) {
                PreparedStatement stmt2 = pgSQLCon.prepareStatement(temp_query);
                ResultSet result2 = stmt2.executeQuery();
                ResultSetMetaData rsmd2 = result2.getMetaData();
                int columnsNumber2 = rsmd2.getColumnCount();
                String temp = "";

                for (int l = 0; l < columnsNumber2; l++) {
                    temp = temp + rsmd2.getColumnName(l + 1) + " || ";
                }

                System.out.println(temp);
                temp = "";

                ArrayList<String> ans3 = new ArrayList<String>();
                int records = 0;
                System.out.println(
                        "==================================================================================================================");
                while (result2.next()) {
                    for (int l = 0; l < columnsNumber2; l++) {
                        temp = temp + result2.getString(l + 1) + " || ";
                    }
                    System.out.println(temp);
                    ans3.add(temp);
                    temp = "";
                    records++;
                }

                System.out.println(
                        "====================================================================================================================");
                if (records > 0)
                    System.out.println("[" + records + "] Records from have been found");
            } else
                System.out.println("No Records from have been found");

        } catch (Exception e) {
            System.out.println("NVQLSelect: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }

    }

}
