import java.util.ArrayList;
import java.sql.*;

public class NVQLDelete {

    static void delete(String vt, ArrayList<String> pred, Connection neo4jCon) {

        String name = vt.substring(vt.indexOf(':') + 1, vt.length());
        // System.out.println("Name = " + name);

        String attrName = pred.get(1);
        //attrName = "\"" + attrName + "\"";
        String relop = pred.get(2);
        relop = relop.replace("==", "=");
        String value = pred.get(3).replace("\"", "'");

        // MATCH (n:`host`) WHERE ID(n)>=109 AND n.`name`="h1" delete(n)
        // DELETE FROM "host" WHERE "host_Id" >= 3
        String query = "MATCH (n:`" + name + "`) WHERE n." + attrName + " " + relop + " " + value + " delete n";
        System.out.println(query);
        try {
            PreparedStatement stmt = neo4jCon.prepareStatement(query);
            int num = stmt.executeUpdate();
            System.out.println("@" + query);
            System.out.println(num + " Node(s) are deleted");

        } catch (Exception e) {
            System.out.println("NVQLEntity: Neo4j Error");
            System.out.println(e);
            System.exit(0);
        }

    }

}