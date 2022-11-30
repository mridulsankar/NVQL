import java.sql.*;
import java.util.*;

public class NVQLSecCondType {

    static String querydisplay(ArrayList<String> relationTypeNameList, ArrayList<String> EntityTypeNameList) {
        String SecCondDefSyntax = "";
        for (int i = 0; i < relationTypeNameList.size(); i++) {
            SecCondDefSyntax = SecCondDefSyntax + relationTypeNameList.get(i) + " {" + EntityTypeNameList.get(i) + "} ";
        }
        return SecCondDefSyntax;
    }

    static Boolean existsSecCondAttrType(String secCondTypeName, String attrType, Connection pgsqlCon) {

        Boolean exists = false;
        try {
            String query = "SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema= 'public' AND table_name = '"
                    + secCondTypeName + "' AND column_name = '" + attrType + "')";

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

    static String convertERDL4SQL(ArrayList<String> EntRelDefList) {
        String erlist = "";
        for (int i = 0; i < EntRelDefList.size(); i++) {
            if (i == 0)
                erlist = erlist + "\"" + EntRelDefList.get(i).toString() + "\" TEXT";
            else
                erlist = erlist + ", \"" + EntRelDefList.get(i).toString() + "\" TEXT";
        }
        return erlist;
    }

    static void createSecCondType(String secCondTypeName, String attrDefList, ArrayList<String> relationTypeNameList,
            ArrayList<String> EntityTypeNameList, Connection pgsqlCon) {

        try {
            if (!existsSecCondType(secCondTypeName, pgsqlCon))
                beginSecCondType(secCondTypeName, attrDefList, relationTypeNameList, EntityTypeNameList, pgsqlCon);
            else {
                System.out.println(
                        "ERROR.NVQLSecCondType: Security-Condition-Type <" + secCondTypeName + "> already exists");
                System.exit(0);
            }
        }

        catch (Exception e) {
            System.out.println("NVQLSecCondType: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }

    }

    static Boolean existsSecCondType(String secCondTypeName, Connection pgsqlCon) throws Exception {
        String query;
        Boolean exists = false;

        try {
            query = "SELECT EXISTS (SELECT 1 FROM \"SECURITY_CONDITION_TYPE_DEF\" WHERE \"Security_Condition_Type_Name\" ='"
                    + secCondTypeName + "')";
            PreparedStatement statement = pgsqlCon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();

            if (result.getString(1).equals("t"))
                exists = true;
        } catch (Exception e) {
            System.out.println("NVQLSecCondTypeDef: PostgreSQL Query Failed");
            System.out.println(e);
            System.exit(0);
        }
        return (exists);
    }

    static void beginSecCondType(String secCondTypeName, String attrDefList, ArrayList<String> relationTypeNameList,
            ArrayList<String> EntityTypeNameList, Connection pgsqlCon) {
        try {
            for (int i = 0; i < relationTypeNameList.size(); i++) {

                if (!NVQLRelationType.existsRelationType(relationTypeNameList.get(i), pgsqlCon)) {
                    System.out.println("ERROR.NVQLSecCondType: Relation type <" + relationTypeNameList.get(i)
                            + "> is not defined");
                    System.exit(0);
                    return;
                }

                for (String ename : EntityTypeNameList.get(i).toString().split(",")) {

                    if (!NVQLEntityType.existsEntityType(ename, pgsqlCon)) {
                        System.out.println("ERROR.NVQLSecCondType: Entity type <" + ename + "> is not defined");
                        System.exit(0);
                        return;
                    }
                }

            }
            String query = "";
            if (NVQLEntityType.existsEntityType(secCondTypeName, pgsqlCon)) {

                query = "DROP TABLE \"" + secCondTypeName + "\"";
                // System.out.println(query);
                PreparedStatement posted1 = pgsqlCon.prepareStatement(query);
                posted1.executeUpdate();
                System.out.println("1 Entity_Type dropped: [" + secCondTypeName + "] ");

            } else {
                System.out.println(
                        "Security-Condition_Type must be defined previously as Entity_Type [" + secCondTypeName + "]");
                System.exit(0);
            }

            String EntRelDefList = NVQLSecCondType.querydisplay(relationTypeNameList, EntityTypeNameList);
            query = "INSERT INTO \"SECURITY_CONDITION_TYPE_DEF\" (\"Security_Condition_Type_Name\",\"Security_Condition_Type_Attr_Def_List\",\"Entity_Relation_List\") VALUES ('"
                    + secCondTypeName + "','" + attrDefList + "','" + EntRelDefList + "')";
            // System.out.println(query);
            PreparedStatement posted = pgsqlCon.prepareStatement(query);
            posted.executeUpdate();
            System.out.println("1 row added Values: [(" + secCondTypeName + ") , (" + attrDefList
                    + ")] inserted in Table 'SECURITY_CONDITION_TYPE_DEF'");

            NVQLAttrDefList adlObject = new NVQLAttrDefList(attrDefList);

            String quelist = adlObject.convertADL4SQL();

            String quelist2 = convertERDL4SQL(relationTypeNameList);

            if (quelist != "")
                query = "CREATE TABLE \"" + secCondTypeName + "\" (\"" + secCondTypeName + "_Id\" SERIAL," + quelist
                        + ", " + quelist2 + ")";
            else
                query = "CREATE TABLE \"" + secCondTypeName + "\" (\"" + secCondTypeName + "_Id\" SERIAL, " + quelist2
                        + ")";
            // System.out.println(query);
            posted = pgsqlCon.prepareStatement(query);
            posted.executeUpdate();
            System.out.println("Security Condition Type '" + secCondTypeName + "' created successfully");
        } catch (

        Exception e) {
            System.out.println("NVQLSecCondType: PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }
    }

    public static String getAttrDefList(String secCondTypeName, Connection pgsqlCon) throws Exception {

        try {
            if (!existsSecCondType(secCondTypeName, pgsqlCon)) {
                System.out.println(
                        "ERROR.NVQLSecCondType: Security Conditon type <" + secCondTypeName + "> is not defined");
                System.exit(0);
                return null;
            }

            String query = "SELECT \"Security_Condition_Type_Attr_Def_List\" FROM \"SECURITY_CONDITION_TYPE_DEF\" WHERE \"Security_Condition_Type_Name\" ='"
                    + secCondTypeName + "'";

            PreparedStatement statement1 = pgsqlCon.prepareStatement(query);
            ResultSet result1 = statement1.executeQuery();
            result1.next();

            return (result1.getString(1));
        } catch (Exception e) {
            System.out.println("NVQLSecCondType.getAttrDefList(): PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }
        return null;
    }

    public static String getEntityRelationList(String secCondTypeName, Connection pgsqlCon) throws Exception {

        try {
            if (!existsSecCondType(secCondTypeName, pgsqlCon)) {
                System.out.println(
                        "ERROR.NVQLSecCondType: Security Conditon type <" + secCondTypeName + "> is not defined");
                System.exit(0);
                return null;
            }

            String query = "SELECT \"Entity_Relation_List\" FROM \"SECURITY_CONDITION_TYPE_DEF\" WHERE \"Security_Condition_Type_Name\" ='"
                    + secCondTypeName + "'";

            PreparedStatement statement1 = pgsqlCon.prepareStatement(query);
            ResultSet result1 = statement1.executeQuery();
            result1.next();

            return (result1.getString(1));
        } catch (Exception e) {
            System.out.println("NVQLSecCondType.getEntityRelationList(): PostgreSQL Error");
            System.out.println(e);
            System.exit(0);
        }
        return null;
    }

}