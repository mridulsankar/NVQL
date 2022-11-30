import java.sql.*;
import java.util.ArrayList;

public class NVQLRelation {

        static Boolean existsRelation(String RelationTypeName, String attrValList, Connection pgsqlCon)
                        throws Exception {
                Boolean exists = false;

                return (exists);
        }

        static Boolean checkCardinalityConstraint(String RelationTypeName, String srcEntityType,
                        ArrayList<String> valPred1, String tgtEntityType, ArrayList<String> valPred2,
                        Connection pgsqlCon) {

                try {
                        Boolean exists = false;

                        String query = "SELECT \"Relation_Type_Cardinality_Constraint\" FROM \"RELATION_TYPE_DEF\" WHERE \"Relation_Type_Name\" ='"
                                        + RelationTypeName + "'";
                        PreparedStatement statement = pgsqlCon.prepareStatement(query);
                        ResultSet result = statement.executeQuery();
                        result.next();
                        if (!result.getString(1).isEmpty()) {

                                int limit1_check = 0;
                                int limit2_check = 0;

                                int limit1 = Integer.parseInt(
                                                result.getString(1).substring(0, result.getString(1).indexOf(':')));
                                int limit2 = Integer.parseInt(result.getString(1).substring(
                                                result.getString(1).indexOf(':') + 1, result.getString(1).length()));

                                query = "SELECT COALESCE((SELECT COUNT(*) FROM \"" + RelationTypeName
                                                + "\" GROUP BY \"From_Entity_Name\" HAVING \"From_Entity_Name\" = '"
                                                + NVQLEntity.EntityName(srcEntityType, valPred1, pgsqlCon) + "'),0)";
                                // System.out.println(query);
                                PreparedStatement statement1 = pgsqlCon.prepareStatement(query);
                                ResultSet result1 = statement1.executeQuery();
                                result1.next();
                                limit1_check = result1.getInt(1);

                                query = "SELECT COALESCE((SELECT COUNT(*) FROM \"" + RelationTypeName
                                                + "\" GROUP BY \"To_Entity_Name\" HAVING \"To_Entity_Name\" = '"
                                                + NVQLEntity.EntityName(tgtEntityType, valPred2, pgsqlCon) + "'),0)";
                                // System.out.println(query);
                                PreparedStatement statement2 = pgsqlCon.prepareStatement(query);
                                ResultSet result2 = statement2.executeQuery();
                                result2.next();
                                limit2_check = result2.getInt(1);

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

        static Boolean VarCondMatching(String varType1, String valPred1, Connection pgsqlCon) {

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
                        String attrValList, String varType2, ArrayList<String> valPred2, Connection pgsqlCon) {

                try {
                        String srcEntityType = "";
                        String tgtEntityType = "";

                        if (!NVQLRelation.VarCondMatching(varType1.substring(0, varType1.indexOf(":")), valPred1.get(0),
                                        pgsqlCon)) {
                                System.out.println(
                                                "ERROR.NVQLRelation: The Variable of Source Entity Type don't match with its condition variable");
                                System.exit(0);
                                return;
                        }

                        if (!NVQLRelation.VarCondMatching(varType2.substring(0, varType2.indexOf(":")), valPred2.get(0),
                                        pgsqlCon)) {
                                System.out.println(
                                                "ERROR.NVQLRelation: The Variable of Target Entity Type don't match with its condition variable");
                                System.exit(0);
                                return;
                        }

                        if (!NVQLRelationType.existsRelationType(relationTypeName, pgsqlCon)) {
                                System.out.println("ERROR.NVQLRelation: Relation Type <" + relationTypeName
                                                + "> is not defined!");
                                System.exit(0);
                                return;
                        } else {
                                srcEntityType = varType1.substring(varType1.indexOf(":") + 1, varType1.length());
                                tgtEntityType = varType2.substring(varType2.indexOf(":") + 1, varType2.length());

                                if (!NVQLEntityType.existsEntityType(srcEntityType, pgsqlCon)) {
                                        System.out.println("ERROR.NVQLRelation: Source entity type " + srcEntityType
                                                        + " is not defined");
                                        System.exit(0);
                                        return;
                                }

                                if (!NVQLEntityType.existsEntityType(tgtEntityType, pgsqlCon)) {
                                        System.out.println("ERROR.NVQLRelation: Target entity type " + tgtEntityType
                                                        + " is not defined");

                                        System.exit(0);
                                        return;
                                }

                                if (NVQLEntity.existsEntity(srcEntityType, valPred1, pgsqlCon) == null) {
                                        System.out.println(
                                                        "ERROR.NVQLRelation: Source entity has no Attribute named as \""
                                                                        + valPred1.get(1) + "\"");

                                        System.exit(0);
                                        return;

                                } else if (!NVQLEntity.existsEntity(srcEntityType, valPred1, pgsqlCon)) {
                                        System.out.println("ERROR.NVQLRelation: Source entity " + srcEntityType
                                                        + " with " + valPred1.get(1) + " "
                                                        + valPred1.get(2).replace("==", "=") + " " + valPred1.get(3)
                                                        + " does not exist");

                                        System.exit(0);
                                        return;
                                }

                                if (NVQLEntity.existsEntity(tgtEntityType, valPred2, pgsqlCon) == null) {
                                        System.out.println(
                                                        "ERROR.NVQLRelation: Target entity has no Attribute named as \""
                                                                        + valPred2.get(1) + "\"");

                                        System.exit(0);
                                        return;

                                } else if (!NVQLEntity.existsEntity(tgtEntityType, valPred2, pgsqlCon)) {
                                        System.out.println("ERROR.NVQLRelation: Target entity " + tgtEntityType
                                                        + " with " + valPred2.get(1) + " "
                                                        + valPred2.get(2).replace("==", "=") + " " + valPred2.get(3)
                                                        + " does not exist");

                                        System.exit(0);
                                        return;
                                }

                                // Check whether attrValList is in accordance with AttrDefList
                                // static Boolean matchAVLwithADL(avl, adl)

                                String datalist = NVQLRelationType.getAttrDefList(relationTypeName, pgsqlCon);
                                String attrList = "", valList = "";
                                NVQLAttrDefList aDefList = null;
                                NVQLAttrValList aValList = null;
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
                                                        valPred1, tgtEntityType, valPred2, pgsqlCon)) {
                                                if (!attrValList.isEmpty()) {
                                                        if (aValList.getAttrList().size() != aDefList.getAttrList()
                                                                        .size()) {
                                                                System.out.println(
                                                                                "WARNING: No. of Attributes in Creation = [ "
                                                                                                + aValList.getAttrList()
                                                                                                                .size()
                                                                                                + " ] & Defintion = [ "
                                                                                                + aDefList.getAttrList()
                                                                                                                .size()
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
                                                        if (!attrList.isEmpty() || !valList.isEmpty()) {
                                                                query = "INSERT INTO \"" + relationTypeName + "\" ("
                                                                                + "\"From_Entity_Name\",\"To_Entity_Name\","
                                                                                + attrList + ") VALUES ('"
                                                                                + NVQLEntity.EntityName(srcEntityType,
                                                                                                valPred1, pgsqlCon)
                                                                                + "','"
                                                                                + NVQLEntity.EntityName(tgtEntityType,
                                                                                                valPred2, pgsqlCon)
                                                                                + "'," + valList + ")";
                                                        }

                                                        else {
                                                                query = "INSERT INTO \"" + relationTypeName + "\" ("
                                                                                + "\"From_Entity_Name\",\"To_Entity_Name\") VALUES ('"
                                                                                + NVQLEntity.EntityName(srcEntityType,
                                                                                                valPred1, pgsqlCon)
                                                                                + "','"
                                                                                + NVQLEntity.EntityName(tgtEntityType,
                                                                                                valPred2, pgsqlCon)
                                                                                + "')";
                                                        }
                                                        // System.out.println(query);
                                                        PreparedStatement statement3 = pgsqlCon.prepareStatement(query);
                                                        int num = statement3.executeUpdate();
                                                        System.out.println(num + " row(s) added Values: [(" + attrList
                                                                        + ") , (" + valList + ")] inserted in Table '"
                                                                        + relationTypeName + "'");
                                                }
                                                /*
                                                 * if(result.getString(1).isEmpty()) {
                                                 * query="INSERT INTO \""+rt+"\" ("+inslist+") VALUES ("+newlist+")";
                                                 * //System.out.println(query); PreparedStatement statement3 =
                                                 * con.prepareStatement(query); int num=statement3.executeUpdate();
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
