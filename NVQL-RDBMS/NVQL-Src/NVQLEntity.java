import java.sql.*;
import java.util.*;

public class NVQLEntity {

        String entityTypeName;
        String attrValList;

        NVQLEntity(String entityTypeName, String attrValList, Connection pgsqlCon) {

                this.entityTypeName = entityTypeName;
                this.attrValList = attrValList;

                try {
                        if (!NVQLEntityType.existsEntityType(entityTypeName, pgsqlCon)) {
                                System.out.println("ERROR.NVQLEntity: Entity Type <" + entityTypeName
                                                + "> is not defined!");
                                System.exit(0);
                        } else {
                                this.createEntity(pgsqlCon);
                        }
                } catch (Exception e) {
                        System.out.println("NVQLEntity: PostgreSQL Error");
                        System.out.println(e);
                        System.exit(0);
                }
        }

        static Boolean existsEntityAttrType(String entityTypeName, ArrayList<String> valPred, Connection pgsqlCon) {

                Boolean exists = false;
                try {
                        String query = "SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema= 'public' AND table_name = '"
                                        + entityTypeName + "' AND column_name = '" + valPred.get(1) + "')";

                        // System.out.println(query);
                        PreparedStatement statement = pgsqlCon.prepareStatement(query);
                        ResultSet result = statement.executeQuery();
                        result.next();

                        if (result.getString(1).equals("t"))
                                exists = true;
                }

                catch (Exception e) {
                        System.out.println("NVQLexistsEntityAttrType: PostgreSQL Error");
                        System.out.println(e);
                        System.exit(0);
                }
                return exists;
        }

        static Boolean existsEntity(String entityTypeName, ArrayList<String> valPred, Connection pgsqlCon) {
                // String query;
                Boolean exists = false;
                try {
                        if (!NVQLEntityType.existsEntityType(entityTypeName, pgsqlCon)) {
                                if (!NVQLEntity.existsEntityAttrType(entityTypeName, valPred, pgsqlCon)) {
                                        // System.out.println("ERROR: No such column exists in Entity Type <" +
                                        // entityTypeName + ">");
                                        // System.exit(0);
                                        return null;
                                }
                                // System.out.println("ERROR: Entity Type <" + entityTypeName + "> doesn't
                                // exists");
                                // System.exit(0);
                        }
                        String query = "SELECT EXISTS (SELECT 1 FROM \"" + entityTypeName + "\" WHERE \""
                                        + valPred.get(1) + "\" " + valPred.get(2).replace("==", "=") + " "
                                        + valPred.get(3).replace("\"", "'") + ")";

                        // System.out.println(query);

                        PreparedStatement statement = pgsqlCon.prepareStatement(query);
                        ResultSet result = statement.executeQuery();
                        result.next();

                        if (result.getString(1).equals("t"))
                                exists = true;
                }

                catch (Exception e) {
                        System.out.println("NVQLexistsEntity: PostgreSQL Error");
                        System.out.println(e);
                        System.exit(0);
                }
                return exists;
        }

        static String EntityName(String entityTypeName, ArrayList<String> valPred, Connection pgsqlCon)
                        throws Exception {

                String query = "SELECT \"name\" FROM \"" + entityTypeName + "\" WHERE \"" + valPred.get(1) + "\" "
                                + valPred.get(2).replace("==", "=") + " " + valPred.get(3).replace("\"", "'");

                // System.out.println(query);

                PreparedStatement statement = pgsqlCon.prepareStatement(query);
                ResultSet result = statement.executeQuery();
                result.next();

                return (result.getString(1));
        }

        Boolean checkUniquenessConstraint(NVQLAttrValList aValList, Connection pgsqlCon) {

                Boolean exists = false;
                try {
                        String uniList = "";
                        /*
                         * The Next Query returns the unique list that is associated with the
                         * entity-name defined during entity definition
                         */

                        String query = "SELECT \"Entity_Type_Unique_Attr_List\" FROM \"ENTITY_TYPE_DEF\" WHERE \"Entity_Type_Name\" ='"
                                        + this.entityTypeName + "'";
                        PreparedStatement statement2 = pgsqlCon.prepareStatement(query);
                        ResultSet result = statement2.executeQuery();
                        result.next();

                        if (!result.getString(1).isEmpty()) { // unique attribute definition exists
                                String uniqueAttrs = result.getString(1);
                                // System.out.println("%%" + uniqueAttrs);
                                uniList = "";

                                int count = 0;

                                for (String attr : uniqueAttrs.split(",")) {
                                        if (aValList.getAttrVal(attr) == null) {
                                                exists = true;
                                                System.out.println(
                                                                "ERROR.NVQLEntity: Unique Value(s) are not mentioned in the list");
                                                System.exit(0);
                                                return exists;
                                        } else {
                                                if (count == 0) {

                                                        uniList = uniList + "\"" + attr + "\"" + "=" + aValList
                                                                        .getAttrVal(attr).toString().replace('"', '\'');
                                                        count++;
                                                } else {

                                                        uniList = uniList + " AND \"" + attr + "\"" + "=" + aValList
                                                                        .getAttrVal(attr).toString().replace('"', '\'');
                                                }
                                        }

                                }
                                // System.out.println(uniList);
                                query = "SELECT EXISTS (SELECT 1 FROM \"" + this.entityTypeName + "\" WHERE " + uniList
                                                + " )";
                                // System.out.println(query);
                                PreparedStatement statement1 = pgsqlCon.prepareStatement(query);
                                ResultSet result1 = statement1.executeQuery();
                                result1.next();

                                if (result1.getString(1).equals("t")) {
                                        exists = true;
                                        System.out.println("ERROR.NVQLEntity: No insertion due to unique key ("
                                                        + uniList + ") violation.");
                                        System.exit(0);
                                }

                                // Execute the query and check whether the tuple exists or not
                        }
                } catch (Exception e) {
                        System.out.println("NVQLcheckUniquenessConstraint: PostgreSQL Error");
                        System.out.println(e);
                        System.exit(0);
                }

                return exists;

        }

        void createEntity(Connection pgsqlCon) {

                /*
                 * Get the attribute definition list of the entityTypeName from ENTITY_TYPE_DEF
                 * table
                 */
                try {
                        String datalist = "";
                        String query;
                        String avl = this.attrValList;

                        datalist = NVQLEntityType.getAttrDefList(this.entityTypeName, pgsqlCon);
                        String attrList = "", valList = "";
                        NVQLAttrDefList aDefList = null;
                        NVQLAttrValList aValList = null;
                        // System.out.println("Datalist = "+datalist);
                        // datalist = datalist + ",";

                        if (datalist != null && datalist != "") {

                                if (!avl.isEmpty()) {
                                        aDefList = new NVQLAttrDefList(datalist);
                                        aValList = new NVQLAttrValList(avl);

                                        for (String attr : aValList.getAttrList()) {
                                                if (!aDefList.existsAttr(attr)) {
                                                        System.out.println("ERROR.NVQLEntity: Undefined attribute <"
                                                                        + attr + ">");
                                                        System.exit(0);
                                                        return;
                                                }
                                        }
                                        // System.out.println("@@" + checkUniquenessConstraint(aValList, pgsqlCon));

                                        if (!checkUniquenessConstraint(aValList, pgsqlCon)) {

                                                if (aValList.getAttrList().size() != aDefList.getAttrList().size()) {
                                                        System.out.println("WARNING: No. of Attributes in Creation = [ "
                                                                        + aValList.getAttrList().size()
                                                                        + " ] & Defintion = [ "
                                                                        + aDefList.getAttrList().size()
                                                                        + " ] are unequal ");

                                                        // return; if we uncomment return then if no. of attributes in
                                                        // definition and
                                                        // creation doesnt match then we cannot create
                                                }
                                                attrList = aValList.getAttrList4SQL(); // Comma separated list of
                                                                                       // attribute
                                                                                       // names
                                                                                       // (within
                                                                                       // double quotes) to be used in
                                                                                       // INSERT
                                                                                       // statement
                                                valList = aValList.getValueList4SQL(aDefList); // Comma separated list
                                                                                               // of values
                                                                                               // to be
                                                                                               // used in INSERT
                                                                                               // statement
                                                if (valList != null && !valList.isEmpty()) {
                                                        query = "INSERT INTO \"" + this.entityTypeName + "\" ("
                                                                        + attrList + ") VALUES (" + valList + ")";

                                                        // System.out.println(query);
                                                        PreparedStatement statement3 = pgsqlCon.prepareStatement(query);
                                                        statement3.executeUpdate();
                                                        System.out.println("1 row added Values: [(" + attrList + ") , ("
                                                                        + valList + ")] inserted in Table '"
                                                                        + this.entityTypeName + "'");
                                                }
                                        }
                                }

                        }

                } catch (Exception e) {
                        System.out.println("NVQLcreateEntity: PostgreSQL Error");
                        System.out.println(e);
                        System.exit(0);
                }

        }
}