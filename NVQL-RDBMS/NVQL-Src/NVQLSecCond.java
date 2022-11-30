import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NVQLSecCond {

    static String querydisplay(ArrayList<String> relationTypeNameList, ArrayList<String> relationCond,
            ArrayList<ArrayList<String>> EntityTypeNameList) {
        String SecCondSyntax = "";
        for (int i = 0; i < relationTypeNameList.size() && i < relationCond.size(); i++) {

            SecCondSyntax = SecCondSyntax + " " + relationTypeNameList.get(i) + " {" + relationCond.get(i);

            for (int j = 0; j < EntityTypeNameList.get(i).size(); j++) {

                if (j == 0)
                    SecCondSyntax = SecCondSyntax + " (" + EntityTypeNameList.get(i).get(j);
                else if (EntityTypeNameList.get(i).size() == 4 && (j == 1))
                    SecCondSyntax = SecCondSyntax + "." + EntityTypeNameList.get(i).get(j);
                else if (EntityTypeNameList.get(i).size() == 5 && (j == 1 || j == 4))
                    SecCondSyntax = SecCondSyntax + ":" + EntityTypeNameList.get(i).get(j);
                else
                    SecCondSyntax = SecCondSyntax + " " + EntityTypeNameList.get(i).get(j);

                if (j == EntityTypeNameList.get(i).size() - 1)
                    SecCondSyntax = SecCondSyntax + ")}";
            }
        }

        return SecCondSyntax;
    }

    static void createSecCond(String secCondTypeName, String attrValList, ArrayList<String> relationTypeNameList,
            ArrayList<String> relationCond, ArrayList<ArrayList<String>> EntityTypeNameList, Connection pgsqlCon) {

        try {
            if (NVQLSecCondType.existsSecCondType(secCondTypeName, pgsqlCon))
                beginSecCond(secCondTypeName, attrValList, relationTypeNameList, relationCond, EntityTypeNameList,
                        pgsqlCon);
            else {
                System.out
                        .println("ERROR.NVQLSecCond: Security-Condition-Type <" + secCondTypeName + "> is not defined");
                System.exit(0);
            }
        }

        catch (Exception e) {
            System.out.println("NVQLSecCondType: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }

    }

    static Boolean existsSecCondAttrType(String SecCondTypeName, ArrayList<String> valPred, Connection pgsqlCon) {

        Boolean exists = false;
        try {
            String query = "SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema= 'public' AND table_name = '"
                    + SecCondTypeName + "' AND column_name = '" + valPred.get(1) + "')";

            // System.out.println(query);

            PreparedStatement statement = pgsqlCon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();

            if (result.getString(1).equals("t"))
                exists = true;
        }

        catch (Exception e) {
            System.out.println("NVQLexistsSecCondAttrType: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }
        return exists;
    }

    static Boolean existsSecCond(String SecCondTypeName, ArrayList<String> valPred, Connection pgsqlCon)
            throws Exception {
        Boolean exists = false;
        try {
            if (!NVQLSecCondType.existsSecCondType(SecCondTypeName, pgsqlCon)) {
                if (!NVQLSecCond.existsSecCondAttrType(SecCondTypeName, valPred, pgsqlCon)) {
                    // System.out.println("ERROR.NVQLSecCond: No such column exists in Security
                    // Condition Type
                    // <" + SecCondTypeName + ">");
                    // System.exit(0);
                    return null;
                }
                // System.out.println("ERROR.NVQLSecCond: Security Condition Type <" +
                // SecCondTypeName + ">
                // doesn't exists");
                // System.exit(0);
            }

            String query = "SELECT EXISTS (SELECT 1 FROM \"" + SecCondTypeName + "\" WHERE \"" + valPred.get(1) + "\" "
                    + valPred.get(2).replace("==", "=") + " " + valPred.get(3).replace("\"", "'") + ")";

            // System.out.println(query);

            PreparedStatement statement = pgsqlCon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();

            if (result.getString(1).equals("t"))
                exists = true;
        }

        catch (Exception e) {
            System.out.println("NVQLexistsSecCond: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }
        return exists;
    }

    static String SecCondName(String SecCondTypeName, ArrayList<String> valPred, Connection pgsqlCon) throws Exception {
        try {
            String query = "SELECT \"name\" FROM \"" + SecCondTypeName + "\" WHERE \"" + valPred.get(1) + "\" "
                    + valPred.get(2).replace("==", "=") + " " + valPred.get(3).replace("\"", "'");

            // System.out.println(query);

            PreparedStatement statement = pgsqlCon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();
            return (result.getString(1));

        } catch (Exception e) {
            System.out.println("NVQLexistsSecCondName: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }
        return null;
    }

    static LinkedHashMap<String, String> EntRelNames(String erlist) {

        LinkedHashMap<String, String> ernames = new LinkedHashMap<>();
        Pattern regexrel = Pattern.compile("(.*?)\\{(.*?)\\}");
        Matcher regexrelMatcher = regexrel.matcher(erlist);
        Pattern regexent = Pattern.compile("\\{(.*?)\\}");
        Matcher regexentMatcher = regexent.matcher(erlist);

        while (regexrelMatcher.find() && regexentMatcher.find()) {// Finds Matching Pattern in String
            ernames.put(regexrelMatcher.group(1).trim().replace(" ", ""),
                    regexentMatcher.group(1).trim().replace(" ", ""));// Fetching Group from String

        }
        return ernames;
    }

    static void beginSecCond(String secCondTypeName, String attrValList, ArrayList<String> relationTypeNameList,
            ArrayList<String> relationCond, ArrayList<ArrayList<String>> EntityTypeNameList, Connection pgsqlCon) {
        try {

            String datalist = "";
            String erlist = "";
            String query;

            datalist = NVQLSecCondType.getAttrDefList(secCondTypeName, pgsqlCon);
            erlist = NVQLSecCondType.getEntityRelationList(secCondTypeName, pgsqlCon);

            LinkedHashMap<String, String> ernames = EntRelNames(erlist);

            String attrList = "", attrList1 = "", valList = "", valList1 = "";

            NVQLAttrDefList aDefList = null;
            NVQLAttrValList aValList = null;

            if (datalist != null && datalist != "") {

                if (!attrValList.isEmpty()) {
                    aDefList = new NVQLAttrDefList(datalist);
                    aValList = new NVQLAttrValList(attrValList);

                    for (String attr : aValList.getAttrList()) {
                        if (!aDefList.existsAttr(attr)) {
                            System.out.println("ERROR.NVQLSecCond: Undefined attribute <" + attr + ">");
                            System.exit(0);
                            return;
                        }
                    }
                }

            }
            if (aValList.getAttrList().size() != aDefList.getAttrList().size()) {
                System.out.println("WARNING: No. of Attributes in Creation = [ " + aValList.getAttrList().size()
                        + " ] & Defintion = [ " + aDefList.getAttrList().size() + " ] are unequal ");

            }
            attrList = aValList.getAttrList4SQL();
            valList = aValList.getValueList4SQL(aDefList);

            if (valList != null && !valList.isEmpty()) {

                for (int i = 0; i < relationTypeNameList.size(); i++) {
                    if (!ernames.containsKey(relationTypeNameList.get(i))) {
                        System.out.println("ERROR.NVQLSecCond: Relation <" + relationTypeNameList.get(i)
                                + "> is not present in Security Condition defintion");
                        System.exit(0);
                        return;
                    } else {
                        ArrayList<String> eList = new ArrayList<String>(
                                Arrays.asList(ernames.get(relationTypeNameList.get(i)).toString().split(",")));
                        if (!eList.contains(relationCond.get(i).substring(relationCond.get(i).indexOf(":") + 1))) {
                            System.out.println("ERROR.NVQLSecCond: Entity Type <"
                                    + relationCond.get(i).substring(relationCond.get(i).indexOf(":") + 1)
                                    + "> is not mentioned in definition");
                            System.exit(0);
                            return;
                        }

                        if (!NVQLEntityType.existsEntityType(
                                relationCond.get(i).substring(relationCond.get(i).indexOf(":") + 1), pgsqlCon)) {
                            System.out.println("ERROR.NVQLSecCond: Entity type "
                                    + relationCond.get(i).substring(relationCond.get(i).indexOf(":") + 1)
                                    + " is not defined");
                            System.exit(0);
                            return;
                        }

                        if (NVQLEntity.existsEntity(relationCond.get(i).substring(relationCond.get(i).indexOf(":") + 1),
                                EntityTypeNameList.get(i), pgsqlCon) == null) {
                            System.out.println("ERROR.NVQLSecCond: Entity has no Attribute named as \""
                                    + EntityTypeNameList.get(i).get(1) + "\"");
                            System.exit(0);
                            return;

                        } else if (!NVQLEntity.existsEntity(
                                relationCond.get(i).substring(relationCond.get(i).indexOf(":") + 1),
                                EntityTypeNameList.get(i), pgsqlCon)) {
                            System.out.println("ERROR.NVQLSecCond: Entity "
                                    + relationCond.get(i).substring(relationCond.get(i).indexOf(":") + 1) + " with "
                                    + EntityTypeNameList.get(i).get(1) + " "
                                    + EntityTypeNameList.get(i).get(2).replace("==", "=") + " "
                                    + EntityTypeNameList.get(i).get(3) + " does not exist");
                            System.exit(0);
                            return;
                        }
                        if (attrList1.equals(""))
                            attrList1 = attrList1 + "\"" + relationTypeNameList.get(i) + "\"";
                        else
                            attrList1 = attrList1 + ",\"" + relationTypeNameList.get(i) + "\"";

                        if (valList1.equals(""))
                            valList1 = valList1 + "\'"
                                    + NVQLEntity.EntityName(
                                            relationCond.get(i).substring(relationCond.get(i).indexOf(":") + 1),
                                            EntityTypeNameList.get(i), pgsqlCon)
                                    + "\'";
                        else
                            valList1 = valList1 + ",'"
                                    + NVQLEntity.EntityName(
                                            relationCond.get(i).substring(relationCond.get(i).indexOf(":") + 1),
                                            EntityTypeNameList.get(i), pgsqlCon)
                                    + "'";

                    }
                }

                if (ernames.size() != relationTypeNameList.size() || ernames.size() != relationCond.size()) {
                    System.out.println(
                            "ERROR.NVQLSecCond: No. of Security Conditions while creation must be equal with its definitions");
                    System.exit(0);
                    return;
                }

                query = "INSERT INTO \"" + secCondTypeName + "\" (" + attrList + "," + attrList1 + ") VALUES ("
                        + valList + "," + valList1 + ")";

                // System.out.println(query);
                PreparedStatement statement3 = pgsqlCon.prepareStatement(query);
                statement3.executeUpdate();
                System.out.println("1 row added Values: [(" + attrList + ") , (" + valList + ")] inserted in Table '"
                        + secCondTypeName + "'");
            }

        } catch (Exception e) {
            System.out.println("NVQLcreateSecCond: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }

    }

    static Boolean existsexpDefCondition(String secCondTypeName, String secCondName, String SecCondAttr,
            Connection pgsqlCon) throws Exception {
        String query, cond = "";
        Boolean exists = false;

        try {
            if (!SecCondAttr.isEmpty()) {
                cond = " AND " + SecCondAttr.replace("#,$", "# AND $").replace("$:#", "$ = #").replace("$", "\"")
                        .replace("#\"", "'").replace("\"#", "'");
                // System.out.println("@@ " + cond);
            }
            query = "SELECT EXISTS (SELECT 1 FROM \"" + secCondTypeName + "\" WHERE \"name\" = '" + secCondName + "' "
                    + cond + ")";
            // System.out.println(query);
            PreparedStatement statement = pgsqlCon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();

            if (result.getString(1).equals("t"))
                exists = true;
        } catch (Exception e) {
            System.out.println("NVQLSecCond.existsexpDefCondition: PostgreSQL Query Failed");
            System.out.println(e);
            System.exit(0);
        }
        // System.out.println("@@ " + exists);
        return (exists);
    }
}
// inside void createSecCond(String secCondName, String attrValList, Connection
// pgsqlCon) {
// try{
/*
 * 
 */