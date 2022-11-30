import java.sql.*;
import java.util.ArrayList;

public class NVQLRelation {

        static Boolean DEBUG = true;

        static Boolean existsRelation(String RelationTypeName, String attrValList, Connection neo4jCon)
                        throws Exception {
                Boolean exists = false;

                return (exists);
        }

        static Boolean checkCardinalityConstraint(String RelationTypeName, String srcEntityType,
                        ArrayList<String> valPred1, String tgtEntityType, ArrayList<String> valPred2,
                        Connection neo4jCon) {

                try {
                        Boolean exists = false;

                        // String query = "SELECT \"Relation_Type_Cardinality_Constraint\" FROM
                        // \"RELATION_TYPE_DEF\" WHERE \"Relation_Type_Name\" ='"
                        // + RelationTypeName + "'";
                        String query = "MATCH (a)-[r:`RELATION_TYPE_DEF:" + RelationTypeName
                                        + "`]->(b) return r.`Relation_Type_Cardinality_Constraint`";
                        PreparedStatement statement = neo4jCon.prepareStatement(query);
                        ResultSet result = statement.executeQuery();
                        result.next();
                        if(DEBUG)
                                System.out.println("@r1" + query);
                        if (!result.getString(1).isEmpty()) {

                                int limit1_check = 0;
                                int limit2_check = 0;

                                int limit1 = Integer.parseInt(
                                                result.getString(1).substring(0, result.getString(1).indexOf(':')));
                                int limit2 = Integer.parseInt(result.getString(1).substring(
                                                result.getString(1).indexOf(':') + 1, result.getString(1).length()));

                                // query = "SELECT COALESCE((SELECT COUNT(*) FROM \"" + RelationTypeName
                                // + "\" GROUP BY \"From_Entity_Name\" HAVING \"From_Entity_Name\" = '"
                                // + NVQLEntity.EntityName(srcEntityType, valPred1, neo4jCon) + "'),0)";

                                query = "MATCH (n:`" + srcEntityType + "`) WHERE n.name = '"
                                                + NVQLEntity.EntityName(srcEntityType, valPred1, neo4jCon)
                                                + "' RETURN size((n)-->()) AS count";
                                if(DEBUG)
                                        System.out.println(query);
                                PreparedStatement statement1 = neo4jCon.prepareStatement(query);
                                ResultSet result1 = statement1.executeQuery();
                                result1.next();
                                // System.out.println("@r2" + query);
                                limit1_check = result1.getInt(1);
                                // System.out.println("#" + limit1_check);
                                // query = "SELECT COALESCE((SELECT COUNT(*) FROM \"" + RelationTypeName
                                // + "\" GROUP BY \"To_Entity_Name\" HAVING \"To_Entity_Name\" = '"
                                // + NVQLEntity.EntityName(tgtEntityType, valPred2, neo4jCon) + "'),0)";
                                // System.out.println(query);
                                // System.out.println("$$");
                                query = "MATCH (n:`" + tgtEntityType + "`) WHERE n.name = '"
                                                + NVQLEntity.EntityName(tgtEntityType, valPred2, neo4jCon)
                                                + "' RETURN size(()-->(n)) AS count";
                                if(DEBUG)
                                        System.out.println("@r3" + query);
                                PreparedStatement statement2 = neo4jCon.prepareStatement(query);
                                ResultSet result2 = statement2.executeQuery();
                                result2.next();
                                limit2_check = result2.getInt(1);
                                // System.out.println("#" + limit2_check);

                                // System.out.println(limit1_check + "^^" + limit1);
                                // System.out.println(limit2_check + "^^" + limit2);

                                if (limit1_check >= limit2) {
                                        exists = true;
                                        System.out.println(
                                                        "ERROR.NVQLRelation: No insertion as Violating Cardinality Constraint; max limit of "
                                                                        + srcEntityType + " is [ " + limit1_check
                                                                        + " ]");
                                        System.exit(0);
                                }
                                if (limit2_check >= limit1) {
                                        exists = true;
                                        System.out.println(
                                                        "ERROR.NVQLRelation: No insertion as Violating Cardinality Constraint; max limit of "
                                                                        + tgtEntityType + " is [ " + limit2_check
                                                                        + " ]");
                                        System.exit(0);
                                }
                                // System.out.println(limit1_check + "%%" + limit2 + "%%" + limit2_check + "%%"
                                // + limit1);
                                // if ((limit1_check < limit2) && (limit2_check < limit1)) {
                                // exists = false;
                                // }
                        }
                        return exists;
                }

                catch (Exception e) {
                        System.out.println("NVQLcheckCardinalityConstraint: PostgreSQL Error");
                        System.out.println(e);
                        System.exit(0);
                        return null;
                }

        }

        static Boolean VarCondMatching(String varType1, String valPred1, Connection neo4jCon) {

                Boolean exists = false;
                try {
                        if (varType1.equals(valPred1))
                                exists = true;
                } catch (Exception e) {
                        System.out.println("NVQLVarCondMatching: PostgreSQL Error");
                        System.out.println(e);
                        System.exit(0);
                }
                return exists;
        }

        static void createRelation(String varType1, ArrayList<String> valPred1, String relationTypeName,
                        String attrValList, String varType2, ArrayList<String> valPred2, Connection neo4jCon) {

                try {
                        String srcEntityType = "";
                        String tgtEntityType = "";

                        if (!NVQLRelation.VarCondMatching(varType1.substring(0, varType1.indexOf(":")), valPred1.get(0),
                                        neo4jCon)) {
                                System.out.println(
                                                "ERROR.NVQLRelation: The Variable of Source Entity Type don't match with its condition variable");
                                System.exit(0);
                                return;
                        }

                        if (!NVQLRelation.VarCondMatching(varType2.substring(0, varType2.indexOf(":")), valPred2.get(0),
                                        neo4jCon)) {
                                System.out.println(
                                                "ERROR.NVQLRelation: The Variable of Target Entity Type don't match with its condition variable");
                                System.exit(0);
                                return;
                        }

                        if (!NVQLRelationType.existsRelationType(relationTypeName, neo4jCon)) {
                                System.out.println("ERROR.NVQLRelation: Relation Type <" + relationTypeName
                                                + "> is not defined!");
                                System.exit(0);
                                return;
                        } else {
                                srcEntityType = varType1.substring(varType1.indexOf(":") + 1, varType1.length());
                                tgtEntityType = varType2.substring(varType2.indexOf(":") + 1, varType2.length());

                                if (!NVQLEntityType.existsEntityType(srcEntityType, neo4jCon)) {
                                        System.out.println("ERROR.NVQLRelation: Source entity type " + srcEntityType
                                                        + " is not defined");
                                        System.exit(0);
                                        return;
                                }

                                if (!NVQLEntityType.existsEntityType(tgtEntityType, neo4jCon)) {
                                        System.out.println("ERROR.NVQLRelation: Target entity type " + tgtEntityType
                                                        + " is not defined");

                                        System.exit(0);
                                        return;
                                }

                                if (NVQLEntity.existsEntity(srcEntityType, valPred1, neo4jCon) == null) {
                                        System.out.println(
                                                        "ERROR.NVQLRelation: Source entity has no Attribute named as \""
                                                                        + valPred1.get(1) + "\"");

                                        System.exit(0);
                                        return;

                                } else if (!NVQLEntity.existsEntity(srcEntityType, valPred1, neo4jCon)) {
                                        System.out.println("ERROR.NVQLRelation: Source entity " + srcEntityType
                                                        + " with " + valPred1.get(1) + " "
                                                        + valPred1.get(2).replace("==", "=") + " " + valPred1.get(3)
                                                        + " does not exist");

                                        System.exit(0);
                                        return;
                                }

                                if (NVQLEntity.existsEntity(tgtEntityType, valPred2, neo4jCon) == null) {
                                        System.out.println(
                                                        "ERROR.NVQLRelation: Target entity has no Attribute named as \""
                                                                        + valPred2.get(1) + "\"");

                                        System.exit(0);
                                        return;

                                } else if (!NVQLEntity.existsEntity(tgtEntityType, valPred2, neo4jCon)) {
                                        System.out.println("ERROR.NVQLRelation: Target entity " + tgtEntityType
                                                        + " with " + valPred2.get(1) + " "
                                                        + valPred2.get(2).replace("==", "=") + " " + valPred2.get(3)
                                                        + " does not exist");

                                        System.exit(0);
                                        return;
                                }

                                // Check whether attrValList is in accordance with AttrDefList
                                // static Boolean matchAVLwithADL(avl, adl)
                                String new_datalist = "";
                                String datalist = NVQLRelationType.getAttrDefList(relationTypeName, neo4jCon);
                                String attrList = "", valList = "";
                                NVQLAttrDefList aDefList = null;
                                NVQLAttrValList aValList = null;
                                // System.out.println("#" + datalist);
                                new_datalist = NVQLRelationType.getModAttrDefList(datalist, neo4jCon);
                                datalist = new_datalist;
                                if (datalist != null && datalist != "") {

                                        if (!attrValList.isEmpty()) {
                                                aDefList = new NVQLAttrDefList(datalist);
                                                aValList = new NVQLAttrValList(attrValList);

                                                for (String attr : aValList.getAttrList()) {
                                                        if (!aDefList.existsAttr(attr)) {
                                                                System.out.println(
                                                                                "ERROR.NVQLRelation: Undefined attribute <"
                                                                                                + attr + ">");

                                                                System.exit(0);
                                                                return;
                                                        }
                                                }
                                        }

                                        // ==> avl.length equals adl.length (optional)
                                        // ==> check if all attr in avl exists in adl
                                        // ==> check if all attr val in av macthes with corresponding def in adl (to be
                                        // done later)
                                        // Boolean checkCardinality(srcEntityType, tgtEntityType, relationTypeName,
                                        if (!NVQLRelation.checkCardinalityConstraint(relationTypeName, srcEntityType,
                                                        valPred1, tgtEntityType, valPred2, neo4jCon)) {
                                                if (!attrValList.isEmpty()) {
                                                        if (aValList.getAttrList()
                                                                        .size() != (aDefList.getAttrList().size() - 1)) {
                                                                System.out.println(
                                                                                "WARNING: No. of Attributes in Creation = [ "
                                                                                                + aValList.getAttrList()
                                                                                                                .size()
                                                                                                + " ] & Defintion = [ "
                                                                                                + (aDefList.getAttrList()
                                                                                                                .size() - 1)
                                                                                                + " ] are unequal ");

                                                                // return; if we uncomment return then if no. of
                                                                // attributes in
                                                                // definition and
                                                                // creation doesnt match then we cannot create
                                                        }
                                                        attrList = aValList.getAttrList4SQL(); // Comma separated list
                                                                                               // of
                                                                                               // attribute names
                                                                                               // (within
                                                                                               // double quotes) to be
                                                                                               // used in INSERT
                                                                                               // statement
                                                        valList = aValList.getValueList4SQL(aDefList); // Comma
                                                                                                       // separated
                                                                                                       // list of values
                                                }
                                                String query = "";
                                                if (valList != null) {

                                                        // MATCH p = (n)-[r:`RELATION:memberOf1`]->() RETURN
                                                        // COUNT(r)
                                                        query = "MATCH (n:`" + srcEntityType + "`)-[r:`RELATION:"
                                                                        + relationTypeName + "`]-> () RETURN COUNT(r)";
                                                        if(DEBUG) 
                                                                System.out.println("@r-last" + query);
                                                        PreparedStatement statement4 = neo4jCon.prepareStatement(query);
                                                        ResultSet result4 = statement4.executeQuery();
                                                        result4.next();
                                                        int id_check = result4.getInt(1);
                                                        // System.out.println("========" + id_check);

                                                        if (!attrList.isEmpty() || !valList.isEmpty()) {

                                                                /*
                                                                 * query = "INSERT INTO \"" + relationTypeName + "\" ("
                                                                 * + "\"From_Entity_Name\",\"To_Entity_Name\"," +
                                                                 * attrList + ") VALUES ('" +
                                                                 * NVQLEntity.EntityName(srcEntityType, valPred1,
                                                                 * neo4jCon) + "','" +
                                                                 * NVQLEntity.EntityName(tgtEntityType, valPred2,
                                                                 * neo4jCon) + "'," + valList + ")";
                                                                 * System.out.println(query);
                                                                 */

                                                                query = "MATCH (" + valPred1.get(0) + ":`"
                                                                                + srcEntityType + "`),("
                                                                                + valPred2.get(0) + " :`"
                                                                                + tgtEntityType + "`) WHERE "
                                                                                + valPred1.get(0) + "."
                                                                                + valPred1.get(1);
                                                                if (valPred1.get(2).equals("=="))
                                                                        query = query + "=";
                                                                else
                                                                        query = query + valPred1.get(2);

                                                                query = query + valPred1.get(3) + " AND "
                                                                                + valPred2.get(0) + "."
                                                                                + valPred2.get(1);
                                                                if (valPred2.get(2).equals("=="))
                                                                        query = query + "=";
                                                                else
                                                                        query = query + valPred2.get(2);

                                                                query = query + valPred2.get(3) + " CREATE ("
                                                                                + valPred1.get(0) + ")-[r:`RELATION:"
                                                                                + relationTypeName + "` {`"
                                                                                + relationTypeName + "_id`:"
                                                                                + (id_check + 1) + " ,`"
                                                                                + attrValList.substring(1,
                                                                                                attrValList.length()
                                                                                                                - 1)
                                                                                                .replaceAll("( )+", "")
                                                                                                .replace("$:#", "`:")
                                                                                                .replace("#,$", ", `")
                                                                                + "}] -> (" + valPred2.get(0) + ")";

                                                        }

                                                        else {
                                                                /*
                                                                 * query = "INSERT INTO \"" + relationTypeName + "\" ("
                                                                 * +
                                                                 * "\"From_Entity_Name\",\"To_Entity_Name\") VALUES ('"
                                                                 * + NVQLEntity.EntityName(srcEntityType, valPred1,
                                                                 * neo4jCon) + "','" +
                                                                 * NVQLEntity.EntityName(tgtEntityType, valPred2,
                                                                 * neo4jCon) + "')";
                                                                 */
                                                                query = "MATCH (" + valPred1.get(0) + ":`"
                                                                                + srcEntityType + "`),("
                                                                                + valPred2.get(0) + " :`"
                                                                                + tgtEntityType + "`) WHERE "
                                                                                + valPred1.get(0) + "."
                                                                                + valPred1.get(1);
                                                                if (valPred1.get(2).equals("=="))
                                                                        query = query + "=";
                                                                else
                                                                        query = query + valPred1.get(2);

                                                                query = query + valPred1.get(3) + " AND "
                                                                                + valPred2.get(0) + "."
                                                                                + valPred2.get(1);
                                                                if (valPred2.get(2).equals("=="))
                                                                        query = query + "=";
                                                                else
                                                                        query = query + valPred2.get(2);

                                                                query = query + valPred2.get(3) + " CREATE ("
                                                                                + valPred1.get(0) + ")-[r:`RELATION:"
                                                                                + relationTypeName + "` {`"
                                                                                + relationTypeName + "_id`:"
                                                                                + (id_check + 1) + " }] -> ("
                                                                                + valPred2.get(0) + ")";

                                                        }
                                                        if(DEBUG)
                                                                System.out.println(query);
                                                        PreparedStatement statement3 = neo4jCon.prepareStatement(query);
                                                        int num = statement3.executeUpdate();
                                                        // System.out.println("@" + query);
                                                        System.out.println(num + " relation added Values: [(" + attrList
                                                                        + ") , (" + valList + ")] inserted with label '"
                                                                        + relationTypeName + "'");
                                                }
                                                /*
                                                 * if(result.getString(1).isEmpty()) {
                                                 * query="INSERT INTO \""+rt+"\" ("+inslist+") VALUES ("+newlist+")";
                                                 * //System.out.println(query); PreparedStatement statement3 =
                                                 * con.prepareStatement(query); int num=statement3.executeUpdate();
                                                 * System.out.println("@"+query);
                                                 * System.out.println(num+" row(s) added Values: [("+inslist+") , ("
                                                 * +newlist+")] inserted in Table '"+rt+"'"); }
                                                 */
                                                // valPred1, valPre2)
                                                // for insertion into aprropriate relation table
                                                // insert one row
                                        }
                                }
                        }
                } catch (Exception e) {
                        System.out.println("NVQLRelation: PostgreSQL Error");
                        System.out.println(e);
                        System.exit(0);
                }

        }

}
