import java.sql.*;
import java.util.*;

public class NVQLEntity {

        String entityTypeName;
        String attrValList;
        static Boolean DEBUG = true;

        NVQLEntity(String entityTypeName, String attrValList, Connection neo4jCon) {

                this.entityTypeName = entityTypeName;
                this.attrValList = attrValList;

                try {
                        if (!NVQLEntityType.existsEntityType(entityTypeName, neo4jCon)) {
                                System.out.println("ERROR.NVQLEntity: Entity Type <" + entityTypeName
                                                + "> is not defined!");
                                System.exit(0);
                        } else {
                                if (NVQLEntity.existsEntityWithSameName(entityTypeName, attrValList, neo4jCon)) {
                                        System.out.println("ERROR.NVQLEntity: Entity <" + entityTypeName
                                                        + "> with same name value already exists!");
                                        System.exit(0);
                                }
                                this.createEntity(neo4jCon);

                        }
                } catch (Exception e) {
                        System.out.println("NVQLEntity: Neo4j Error");
                        System.out.println(e);
                        System.exit(0);
                }
        }

        // static Boolean existsEntityAttrType(String entityTypeName, ArrayList<String>
        // valPred, Connection neo4jCon) {

        // Boolean exists = false;
        // try {
        // String query = "SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE
        // table_schema= 'public' AND table_name = '"
        // + entityTypeName + "' AND column_name = '" + valPred.get(1) + "')";

        // // System.out.println(query);
        // PreparedStatement statement = neo4jCon.prepareStatement(query);
        // ResultSet result = statement.executeQuery();
        // result.next();
        // // System.out.println("@" + query);

        // if (result.getString(1).equals("t"))
        // exists = true;
        // }

        // catch (Exception e) {
        // System.out.println("NVQLexistsEntityAttrType: Neo4j Error");
        // System.out.println(e);
        // System.exit(0);
        // }
        // return exists;
        // }
        static Boolean existsEntityWithSameName(String entityTypeName, String attrVal, Connection neo4jCon) {
                String query;
                Boolean exists = false;
                try {
                        // System.out.println(attrValList);
                        // int indexOfName = attrValList.toLowerCase().indexOf("$name$:#");
                        // int indexOfSep = attrValList.toLowerCase().indexOf(",",
                        // attrValList.toLowerCase().indexOf("$name$:#"));
                        // System.out.println(indexOfName + "$$$$$$" + indexOfSep);
                        String valOfName = "";
                        String temp = attrVal + ",";
                        valOfName = temp.substring(temp.toLowerCase().indexOf("$name$:#"),
                                        temp.toLowerCase().indexOf(",", temp.toLowerCase().indexOf("$name$:#")));
                        System.out.println(valOfName);
                        if (!valOfName.isEmpty()) {
                                // System.out.println(valOfName);

                                query = "MATCH (n:`" + entityTypeName + "`) WHERE n.`"
                                                + valOfName.substring(valOfName.indexOf("$") + 1,
                                                                valOfName.lastIndexOf("$"))
                                                + "` = " + valOfName.substring(valOfName.indexOf("#") + 1,
                                                                valOfName.lastIndexOf("#"))
                                                + " RETURN (COUNT(n))";

                                // System.out.println("XXXXXX" + query);

                                PreparedStatement statement = neo4jCon.prepareStatement(query);
                                ResultSet result = statement.executeQuery();
                                result.next();
                                if (DEBUG)
                                        System.out.println("@" + query);

                                if (Integer.valueOf(result.getString(1).toString()) > 0)
                                        exists = true;
                        }

                }

                catch (Exception e) {
                        System.out.println("NVQLexistsEntityWithSameName: Neo4j Error");
                        System.out.println(e);
                        System.exit(0);
                }
                return exists;
        }

        static Boolean existsEntity(String entityTypeName, ArrayList<String> valPred, Connection neo4jCon) {
                // String query;
                Boolean exists = false;
                try {
                        /*
                         * if (!NVQLEntityType.existsEntityType(entityTypeName, neo4jCon)) { if
                         * (!NVQLEntity.existsEntityAttrType(entityTypeName, valPred, neo4jCon)) { //
                         * System.out.println("ERROR: No such column exists in Entity Type <" + //
                         * entityTypeName + ">"); // System.exit(0); return null; } //
                         * System.out.println("ERROR: Entity Type <" + entityTypeName + "> doesn't //
                         * exists"); // System.exit(0); }
                         */
                        // String query = "SELECT EXISTS (SELECT 1 FROM \"" + entityTypeName + "\" WHERE
                        // \""
                        // + valPred.get(1) + "\" " + valPred.get(2).replace("==", "=") + " "
                        // + valPred.get(3).replace("\"", "'") + ")";
                        // MATCH (n:`host`) WHERE n.`ipAddr` = "192.168.148.1" RETURN CASE WHEN
                        // (COUNT(n) = 0) THEN "f" ELSE "t" END as n
                        String query = "MATCH (n:`" + entityTypeName + "`) WHERE n.`" + valPred.get(1) + "` "
                                        + valPred.get(2).replace("==", "=") + " " + valPred.get(3).replace("\"", "'")
                                        + " RETURN CASE WHEN (COUNT(n) = 0) THEN \"f\" ELSE \"t\" END as n";

                        // System.out.println("XXXXXX" + query);

                        PreparedStatement statement = neo4jCon.prepareStatement(query);
                        ResultSet result = statement.executeQuery();
                        result.next();
                        if (DEBUG)
                                System.out.println("@" + query);

                        if (result.getString(1).equals("t"))
                                exists = true;
                }

                catch (Exception e) {
                        System.out.println("NVQLexistsEntity: Neo4j Error");
                        System.out.println(e);
                        System.exit(0);
                }
                return exists;
        }

        static String EntityName(String entityTypeName, ArrayList<String> valPred, Connection neo4jCon)
                        throws Exception {

                // String query = "SELECT \"name\" FROM \"" + entityTypeName + "\" WHERE \"" +
                // valPred.get(1) + "\" "
                // + valPred.get(2).replace("==", "=") + " " + valPred.get(3).replace("\"",
                // "'");
                String query = "MATCH (n:`" + entityTypeName + "`) WHERE n.`" + valPred.get(1) + "`"
                                + valPred.get(2).replace("==", "=") + " " + valPred.get(3).replace("\"", "'")
                                + " RETURN (n.`name`)";
                // System.out.println(query);

                PreparedStatement statement = neo4jCon.prepareStatement(query);
                ResultSet result = statement.executeQuery();
                result.next();
                if (DEBUG)
                        System.out.println("@" + query);

                return (result.getString(1));
        }

        Boolean checkUniquenessConstraint(NVQLAttrValList aValList, Connection neo4jCon) {

                Boolean exists = false;
                try {
                        String uniList = "";
                        /*
                         * The Next Query returns the unique list that is associated with the
                         * entity-name defined during entity definition
                         */

                        String query = "MATCH (n: `ENTITY_TYPE_DEF` : `" + this.entityTypeName
                                        + "`) RETURN n.`Entity_Type_Unique_Attr_List`";
                        PreparedStatement statement2 = neo4jCon.prepareStatement(query);
                        ResultSet result = statement2.executeQuery();
                        result.next();
                        // System.out.println("@!" + query);
                        // System.out.println("@%" + result.getString(1));

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

                                                        uniList = uniList + "n.`" + attr + "`" + "="
                                                                        + aValList.getAttrVal(attr).toString();
                                                        count++;
                                                } else {

                                                        uniList = uniList + " AND n.`" + attr + "`" + "="
                                                                        + aValList.getAttrVal(attr).toString();
                                                }
                                        }

                                }
                                // System.out.println(uniList);
                                // MATCH (n: `ENTITY_TYPE_DEF` : `host`) WHERE n.`ipAddr`="string" AND
                                // n.`macAddr`="string" RETURN CASE WHEN (COUNT(n) = 0) THEN "f" ELSE "t" END as
                                // n
                                // query = "SELECT EXISTS (SELECT 1 FROM \"" + this.entityTypeName + "\" WHERE "
                                // + uniList
                                // + " )";
                                query = "MATCH (n: `" + this.entityTypeName + "`) WHERE " + uniList
                                                + " RETURN CASE WHEN (COUNT(n) = 0) THEN \"f\" ELSE \"t\" END as n";
                                // System.out.println(query);
                                PreparedStatement statement1 = neo4jCon.prepareStatement(query);
                                ResultSet result1 = statement1.executeQuery();
                                result1.next();
                                // System.out.println("@" + query);

                                if (result1.getString(1).equals("t")) {
                                        exists = true;
                                        System.out.println("ERROR.NVQLEntity: No insertion due to unique key ("
                                                        + uniList + ") violation.");
                                        System.exit(0);
                                }

                                // Execute the query and check whether the tuple exists or not
                        }
                } catch (Exception e) {
                        System.out.println("NVQLcheckUniquenessConstraint: Neo4j Error");
                        System.out.println(e);
                        System.exit(0);
                }

                return exists;

        }

        void createEntity(Connection neo4jCon) {

                /*
                 * Get the attribute definition list of the entityTypeName from ENTITY_TYPE_DEF
                 * table
                 */
                try {
                        String datalist = "", new_datalist = "";
                        String query;
                        String avl = this.attrValList;

                        datalist = NVQLEntityType.getAttrDefList(this.entityTypeName, neo4jCon);
                        String mixList = "";
                        NVQLAttrDefList aDefList = null;
                        NVQLAttrValList aValList = null;
                        // System.out.println("Datalist = " + datalist);
                        // datalist = datalist + ",";
                        new_datalist = NVQLEntityType.getModAttrDefList(datalist, neo4jCon);
                        // System.out.println("New Datalist = " + new_datalist);
                        // System.out.println("avl = " + avl);
                        datalist = new_datalist;
                        if (datalist != null && datalist != "") {
                                // System.out.println("**");
                                if (!avl.isEmpty()) {
                                        // System.out.println("&&&" + avl);
                                        aDefList = new NVQLAttrDefList(datalist);
                                        // System.out.println("@@" + aDefList);
                                        aValList = new NVQLAttrValList(avl);
                                        // System.out.println("%%" + aValList);
                                        for (String attr : aValList.getAttrList()) {
                                                if (!aDefList.existsAttr(attr)) {
                                                        System.out.println("ERROR.NVQLEntity: Undefined attribute <"
                                                                        + attr + ">");
                                                        System.exit(0);
                                                        return;
                                                }
                                        }
                                        // System.out.println("@@" + checkUniquenessConstraint(aValList, neo4jCon));

                                        if (!checkUniquenessConstraint(aValList, neo4jCon)) {

                                                if (aValList.getAttrList()
                                                                .size() != (aDefList.getAttrList().size() - 1)) {
                                                        System.out.println("WARNING: No. of Attributes in Creation = [ "
                                                                        + aValList.getAttrList().size()
                                                                        + " ] & Defintion = [ "
                                                                        + (aDefList.getAttrList().size() - 1)
                                                                        + " ] are unequal ");

                                                        // return; if we uncomment return then if no. of attributes in
                                                        // definition and
                                                        // creation doesnt match then we cannot create
                                                }
                                                // intuition==>commenting the below next line as before testing
                                                // attrList = aValList.getAttrList4SQL(); // Comma separated list of
                                                // attribute
                                                // names
                                                // (within
                                                // double quotes) to be used in
                                                // INSERT
                                                // statement
                                                // valList = aValList.getValueList4SQL(aDefList); // Comma separated
                                                // list
                                                // of values
                                                // to be
                                                // used in INSERT
                                                // statement
                                                mixList = aValList.getMixList4Neo4j(aDefList);
                                                int max_value = 0;

                                                query = "MATCH (n: `" + this.entityTypeName + "`) RETURN MAX(n.`"
                                                                + this.entityTypeName + "_id`)";
                                                // System.out.println(query);
                                                PreparedStatement statement4 = neo4jCon.prepareStatement(query);
                                                ResultSet result4 = statement4.executeQuery();
                                                result4.next();
                                                // System.out.println("@" + query);
                                                // System.out.println("$$" + result4.getString(1));
                                                if (result4.getString(1).equals("integer"))
                                                        max_value = 0;
                                                else {
                                                        try {
                                                                max_value = Integer.parseInt(result4.getString(1));
                                                        } catch (NumberFormatException e) {
                                                                System.out.println("Unexpected Input");
                                                        }
                                                }

                                                if (mixList != null && !mixList.isEmpty()) {
                                                        query = "CREATE (n: `" + this.entityTypeName + "` {" + mixList;
                                                        // System.out.println(query);

                                                        if (!mixList.isEmpty())
                                                                query = query + ",`" + this.entityTypeName + "_id`:"
                                                                                + (max_value + 1) + "})";
                                                        else
                                                                query = query + "`" + this.entityTypeName + "_id`:"
                                                                                + (max_value + 1) + "})";

                                                        PreparedStatement statement3 = neo4jCon.prepareStatement(query);
                                                        statement3.executeUpdate();
                                                        System.out.println("@" + query);
                                                        System.out.println("1 node added; Values: [(" + mixList
                                                                        + ")] inserted with label '"
                                                                        + this.entityTypeName + "'");
                                                }
                                        }
                                }

                        }

                } catch (Exception e) {
                        System.out.println("NVQLcreateEntity: Neo4j Error");
                        System.out.println(e);
                        System.exit(0);
                }

        }
}