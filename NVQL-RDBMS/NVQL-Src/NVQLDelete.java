import java.util.ArrayList;
import java.sql.*;

public class NVQLDelete {

    static void delete(String vt, ArrayList<String> pred, Connection pgSQLCon) {

        String name = vt.substring(vt.indexOf(':') + 1, vt.length());
        // System.out.println("Name = " + name);

        String attrName = pred.get(1);
        attrName = "\"" + attrName + "\"";
        String relop = pred.get(2);
        relop = relop.replace("==", "=");
        String value = pred.get(3).replace("\"", "'");

        String query = "DELETE FROM" + " \"" + name + "\"" + " WHERE " + attrName + " " + relop + " " + value;

        // System.out.println(query);

        try {
            PreparedStatement stmt = pgSQLCon.prepareStatement(query);
            int num = stmt.executeUpdate();
            System.out.println(num + " No. of " + name + " objects deleted");

        } catch (Exception e) {

        }

    }

}