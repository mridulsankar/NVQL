/*
 * import java.util.ArrayList; import java.sql.*;
 * 
 * public class NVQLSelect {
 * 
 * private NVQLSelect() {
 * 
 * }
 * 
 * static void select(String vt, ArrayList<ArrayList<String>> selectpred,
 * ArrayList<String> relop, Connection pgSQLCon) {
 * 
 * String name = vt.substring(vt.indexOf(':') + 1, vt.length()); //
 * System.out.println("Name = " + name); ArrayList<String> query = new
 * ArrayList<String>(); String attrName = "", oper, value = "", temp_query = "",
 * tabcolName = "";
 * 
 * for (int i = 0; i < selectpred.size(); i++) { switch
 * (selectpred.get(i).size()) { case 4: attrName = selectpred.get(i).get(1);
 * attrName = "\"" + attrName + "\""; oper = selectpred.get(i).get(2); oper =
 * oper.replace("==", "="); value = selectpred.get(i).get(3).replace("\"", "'");
 * 
 * query.add("SELECT * FROM " + " \"" + name + "\"" + " WHERE " + attrName + " "
 * + oper + " " + value); break;
 * 
 * case 5: if (name.equals(selectpred.get(i).get(1))) { attrName = "'" +
 * selectpred.get(i).get(3) + "'"; tabcolName = "\"To_Entity_Name\""; } else if
 * (name.equals(selectpred.get(i).get(4))) { attrName = "'" +
 * selectpred.get(i).get(0) + "'"; tabcolName = "\"From_Entity_Name\""; } //
 * value = selectpred.get(i).get(3).replace("\"", "'");
 * 
 * query.add("SELECT * FROM " + " \"" + selectpred.get(i).get(2) + "\"" +
 * " WHERE " + tabcolName + " = " + attrName); break;
 * 
 * } System.out.println(query.get(i)); }
 * 
 * for (int i = 0; i < selectpred.size(); i++) {
 * 
 * if (i > 0 && relop.get(i - 1).equals("or")) temp_query = temp_query +
 * " UNION "; else if (i > 0 && relop.get(i - 1).equals("and")) temp_query =
 * temp_query + " INTERSECT ";
 * 
 * temp_query = temp_query + "(" + query.get(i) + ")";
 * 
 * }
 * 
 * try { PreparedStatement stmt1 = pgSQLCon.prepareStatement("SELECT EXISTS(" +
 * temp_query + ")"); // System.out.println("SELECT EXISTS(" + temp_query +
 * ")"); ResultSet result1 = stmt1.executeQuery(); result1.next(); System.out.println("@"+query);
 * 
 * if (result1.getString(1).equals("t")) { PreparedStatement stmt2 =
 * pgSQLCon.prepareStatement(temp_query); ResultSet result2 =
 * stmt2.executeQuery(); ResultSetMetaData rsmd2 = result2.getMetaData(); int
 * columnsNumber2 = rsmd2.getColumnCount(); ArrayList<String> ans3 = new
 * ArrayList<String>(); String temp = ""; int records = 0; System.out.println(
 * "=================================================================================================================="
 * ); while (result2.next()) { for (int l = 0; l < columnsNumber2; l++) { temp =
 * temp + result2.getString(l + 1) + "      ||        "; }
 * System.out.println(temp); ans3.add(temp); temp = ""; records++; }
 * 
 * System.out.println(
 * "===================================================================================================================="
 * ); if (records > 0) System.out.println("[" + records +
 * "] Records from have been found"); } else
 * System.out.println("No Records from have been found");
 * 
 * } catch (Exception e) {
 * 
 * }
 * 
 * }
 * 
 * }
 */