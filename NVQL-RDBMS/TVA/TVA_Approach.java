import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class TVA_Approach {

    static Connection con_tva;
    static Connection con_nval;

    public static void toPopulateHH() throws Exception {
        try {
            //Connection con_tva = getConnection("TVA");
            String SQL = "CREATE TABLE IF NOT EXISTS HH (Src_Id INTEGER, Dst_Id INTEGER)";
            // System.out.println(SQL);
            PreparedStatement pstmt = con_tva.prepareStatement(SQL);
            pstmt.executeUpdate();

            //Connection con_nval = getConnection("nval");
            SQL = "SELECT \"reachability\".\"accessBy\",\"reachability\".\"accessTo\",\"runAt\".\"From_Entity_Name\",\"runAt\".\"To_Entity_Name\" from \"reachability\" INNER JOIN \"runAt\" ON \"reachability\".\"accessTo\" = \"runAt\".\"From_Entity_Name\"";
            // System.out.println(SQL);
            PreparedStatement statement = con_nval.prepareStatement(SQL);
            ResultSet result = statement.executeQuery();

            while (result.next()) {

                SQL = "SELECT EXISTS(SELECT 1 FROM \"network-domain\" WHERE \"name\" = '" + result.getString("accessBy")
                        + "')";
                // System.out.println(SQL);
                PreparedStatement statement1 = con_nval.prepareStatement(SQL);
                ResultSet result1 = statement1.executeQuery();
                result1.next();

                if (result1.getString(1).equals("t")) {

                    SQL = "SELECT DISTINCT \"From_Entity_Name\" FROM \"memberOf\" WHERE \"To_Entity_Name\" = '"
                            + result.getString("accessBy") + "'";
                    // System.out.println(SQL);
                    PreparedStatement statement2 = con_nval.prepareStatement(SQL);
                    ResultSet result2 = statement2.executeQuery();

                    while (result2.next()) {
                        SQL = "INSERT INTO HH (Src_Id, Dst_Id) VALUES ('" + getID(result2.getString("From_Entity_Name"))
                                + "','" + getID(result.getString("To_Entity_Name")) + "')";
                        // System.out.println(SQL);
                        PreparedStatement posted = con_tva.prepareStatement(SQL);
                        posted.executeUpdate();
                    }

                } else {
                    SQL = "INSERT INTO HH (Src_Id, Dst_Id) VALUES ('" + getID(result.getString("accessBy")) + "','"
                            + getID(result.getString("To_Entity_Name")) + "')";
                    // System.out.println(SQL);
                    PreparedStatement posted = con_tva.prepareStatement(SQL);
                    posted.executeUpdate();
                }

            }
            //con_tva.close();
            //con_nval.close();

            // System.out.println("All records have been selected");
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out.println("Connectivity -> (HH) Relation Schema Populated successfully...\n");
        }
    }

    public static void toPopulateHC() throws Exception {
        try {
            //Connection con_tva = getConnection("TVA");
            String SQL = "CREATE TABLE IF NOT EXISTS HC (Host_Id INTEGER , Cond TEXT)";
            PreparedStatement pstmt = con_tva.prepareStatement(SQL);
            pstmt.executeUpdate();

            //Connection con_nval = getConnection("nval");
            SQL = "SELECT DISTINCT \"privType\",\"atHost\" FROM \"privilege\"";
            // System.out.println(SQL);
            PreparedStatement statement = con_nval.prepareStatement(SQL);
            ResultSet result = statement.executeQuery();

            while (result.next()) {

                SQL = "INSERT INTO HC (Host_Id, Cond) VALUES ('" + getID(result.getString("atHost")) + "','"
                        + result.getString("privType") + "')";
                // System.out.println(SQL);
                PreparedStatement posted = con_tva.prepareStatement(SQL);
                posted.executeUpdate();

            }

            SQL = "SELECT DISTINCT \"service\".\"swName\", \"runAt\".\"To_Entity_Name\" from \"reachability\" INNER JOIN \"service\" ON \"reachability\".\"accessTo\" = \"service\".\"name\" INNER JOIN \"runAt\" ON \"service\".\"name\" = \"runAt\".\"From_Entity_Name\"";
            // System.out.println(SQL);
            PreparedStatement statement1 = con_nval.prepareStatement(SQL);
            ResultSet result1 = statement1.executeQuery();

            while (result1.next()) {

                SQL = "INSERT INTO HC (Host_Id, Cond) VALUES ('" + getID(result1.getString("To_Entity_Name")) + "','"
                        + result1.getString("swName") + "')";
                // System.out.println(SQL);
                PreparedStatement posted = con_tva.prepareStatement(SQL);
                posted.executeUpdate();

            }
            //con_tva.close();
            //con_nval.close();

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out.println("Condition -> (HC) Relation Schema Populated successfully...\n ");
        }
    }

    public static void toPopulateCVandVC() throws Exception {
        try {
            //Connection con_tva = getConnection("TVA");
            String SQL = "CREATE TABLE IF NOT EXISTS VC (Vulnerability TEXT, Cond TEXT)";
            PreparedStatement pstmt1 = con_tva.prepareStatement(SQL);
            pstmt1.executeUpdate();
            //con_tva.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out
                    .println("Vulnerability Condition Dependency -> (VC) Relation Schema Populated successfully...\n");
        }

        try {
            //Connection con_tva = getConnection("TVA");
            String SQL = "CREATE TABLE IF NOT EXISTS CV (Cond TEXT, Place TEXT, Vulnerability TEXT)";
            PreparedStatement pstmt = con_tva.prepareStatement(SQL);
            pstmt.executeUpdate();

            //Connection con_nval = getConnection("nval");
            SQL = "SELECT DISTINCT \"CveId(s)\", \"Precond\", \"Precond_Attr_Val_List\", \"Postcond\", \"Postcond_Attr_Val_List\" FROM \"EXPLOIT_TYPE_DEF\"";
            // System.out.println(SQL);
            PreparedStatement statement = con_nval.prepareStatement(SQL);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String cve = result.getString("CveId(s)").substring(1, result.getString("CveId(s)").length() - 1);
                // System.out.println(cve);
                for (String cve_id : cve.replace(" ", "").split(",")) {

                    SQL = "SELECT \"vulnerability\".\"cveId\",\"vulnerability\".\"name\",\"hasVuln\".\"To_Entity_Name\",\"hasVuln\".\"From_Entity_Name\",\"service\".\"swName\" from \"vulnerability\" INNER JOIN \"hasVuln\" ON \"vulnerability\".\"name\" = \"hasVuln\".\"To_Entity_Name\" AND \"vulnerability\".\"cveId\" = '"
                            + cve_id
                            + "' INNER JOIN \"service\" ON \"service\".\"name\" = \"hasVuln\".\"From_Entity_Name\"";
                    // System.out.println(SQL);
                    PreparedStatement statement1 = con_nval.prepareStatement(SQL);
                    ResultSet result1 = statement1.executeQuery();

                    while (result1.next()) {

                        SQL = "INSERT INTO CV (Cond, Place, Vulnerability) VALUES ('" + result1.getString("swName")
                                + "','" + "D" + "','" + cve_id + "')";
                        // System.out.println(SQL);
                        PreparedStatement posted = con_tva.prepareStatement(SQL);
                        posted.executeUpdate();

                    }

                    ArrayList<String> preList = new ArrayList<String>(
                            Arrays.asList(result.getString("Precond_Attr_Val_List")
                                    .substring(1, result.getString("Precond_Attr_Val_List").length() - 1)
                                    .replace(" ", "").split("\\),\\(")));
                    ArrayList<String> postList = new ArrayList<String>(
                            Arrays.asList(result.getString("Postcond_Attr_Val_List")
                                    .substring(1, result.getString("Postcond_Attr_Val_List").length() - 1)
                                    .replace(" ", "").split("\\),\\(")));

                    String precond_list = result.getString("Precond");
                    int precounter = 0;

                    for (String precond_item : precond_list.replace(" ", "").split(",")) {
                        // System.out.println(precond_item);
                        if (precond_item.equals("privilege")) {

                            String target = preList.get(precounter).substring(1, preList.get(precounter).length());
                            ArrayList<String> targetList = new ArrayList<String>(Arrays.asList(target.split("#,$")));

                            LinkedHashMap<String, String> targetMap = new LinkedHashMap<>();

                            for (int i = 0; i < targetList.size(); i++) {
                                targetMap.put(targetList.get(i).substring(0, targetList.get(i).indexOf("$:#")),
                                        targetList.get(i).substring(targetList.get(i).indexOf("$:#") + "$:#".length(),
                                                targetList.get(i).length() - 1));
                            }

                            if (!targetMap.containsKey("privType")) {
                                // System.out.println("ERROR.topopulateCV(): privType value missing");
                            } else {
                                SQL = "INSERT INTO CV (Cond, Place, Vulnerability) VALUES ('"
                                        + targetMap.get("privType").replace("\"", "") + "','" + "S" + "','" + cve_id
                                        + "')";
                                // System.out.println(SQL);
                                PreparedStatement posted1 = con_tva.prepareStatement(SQL);
                                posted1.executeUpdate();
                            }

                        }
                        precounter++;
                    }

                    String postcond_list = result.getString("Postcond");
                    int postcounter = 0;

                    for (String postcond_item : postcond_list.replace(" ", "").split(",")) {
                        // System.out.println(precond_item);
                        if (postcond_item.equals("privilege")) {

                            String target1 = postList.get(postcounter).substring(1, postList.get(postcounter).length());
                            ArrayList<String> targetList1 = new ArrayList<String>(Arrays.asList(target1.split("#,$")));

                            LinkedHashMap<String, String> targetMap1 = new LinkedHashMap<>();

                            for (int i = 0; i < targetList1.size(); i++) {
                                targetMap1.put(targetList1.get(i).substring(0, targetList1.get(i).indexOf("$:#")),
                                        targetList1.get(i).substring(targetList1.get(i).indexOf("$:#") + "$:#".length(),
                                                targetList1.get(i).length() - 1));
                            }

                            if (!targetMap1.containsKey("privType")) {
                                // System.out.println("ERROR.topopulateCV(): privType value missing");
                            } else {
                                SQL = "INSERT INTO VC (Vulnerability,Cond) VALUES ('"
                                        + cve_id + "','" + targetMap1.get("privType").replace("\"", "") + "')";
                                // System.out.println(SQL);
                                PreparedStatement posted2 = con_tva.prepareStatement(SQL);
                                posted2.executeUpdate();
                            }

                        }
                        postcounter++;
                    }

                }
                
            }
            //con_tva.close();
            //con_nval.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out
                    .println("Condition Vulnerability Dependency -> (CV) Relation Schema Populated successfully...\n");
        }
    }

    public static Connection getConnection(String dbName) throws Exception {
        try {
            String driver = "org.postgresql.Driver";
            String url = "jdbc:postgresql://localhost:5432/" + dbName;
            String username = "postgres";
            String password = "abcdefgh";
            Class.forName(driver);
            Connection c = DriverManager.getConnection(url, username, password);
            return c;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;

    }

    public static void destroyTable(String tablename) throws Exception {

        try {
            //Connection con = getConnection("TVA");
            String SQL = "DROP TABLE IF EXISTS " + tablename;
            PreparedStatement pstmt = con_tva.prepareStatement(SQL);
            pstmt.executeUpdate();
            //con.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out.println("Relation Schema " + tablename + ": Destroyed if present\n");
        }
    }

    public static int getID(String inputname) throws Exception {

        try {
            //Connection con_tva = getConnection("TVA");
            String SQL = "SELECT Host_Id FROM host WHERE Host_Name = '" + inputname + "'";
            // System.out.println(SQL);
            PreparedStatement statement = con_tva.prepareStatement(SQL);
            ResultSet result = statement.executeQuery();
            result.next();
            return (Integer.parseInt(result.getString("host_Id")));
        } catch (Exception e) {
            System.out.println(e);
        }
        return -99999999;
    }

    public static void toPopulatehost() throws Exception {
        try {
            //Connection con_tva = getConnection("TVA");
            String SQL = "CREATE TABLE IF NOT EXISTS host (Host_Id INTEGER , Host_Name TEXT)";
            PreparedStatement pstmt = con_tva.prepareStatement(SQL);
            pstmt.executeUpdate();

            //Connection con_nval = getConnection("nval");
            SQL = "SELECT DISTINCT \"host_Id\",\"name\" FROM \"host\"";
            // System.out.println(SQL);
            PreparedStatement statement = con_nval.prepareStatement(SQL);
            ResultSet result = statement.executeQuery();

            while (result.next()) {

                SQL = "INSERT INTO host (Host_Id, Host_Name) VALUES ('" + Integer.parseInt(result.getString("host_Id"))
                        + "','" + result.getString("name") + "')";
                // System.out.println(SQL);
                PreparedStatement posted = con_tva.prepareStatement(SQL);
                posted.executeUpdate();

            }
            //con_tva.close();
            //con_nval.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out.println("Host Mapping done successfully..\n");
        }
    }

    public static void main(String args[]) throws Exception {
        con_tva=getConnection("TVA");
        con_nval=getConnection("NVQL");

        System.out.println("\nTVA Model Starting..\n");
        destroyTable("host,HH,CV,HC,VC");
        toPopulatehost();
        toPopulateHH();
        toPopulateHC();
        toPopulateCVandVC();
        System.out.println("TVA Model finishing..\n");
        con_tva.close();
        con_nval.close();
        // System.out.println(getID("h1"));
    }

}