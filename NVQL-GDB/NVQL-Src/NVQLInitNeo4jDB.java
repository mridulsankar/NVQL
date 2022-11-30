import java.sql.*;

public class NVQLInitNeo4jDB {
    String neo4jHost;
    String neo4jPort;
    String neo4jDBName;
    String neo4jUserName;
    String neo4jPassword;
    Connection neo4jCon;

    NVQLInitNeo4jDB(String host, String port, String dbName, String uName, String passwd) {
        this.neo4jHost = host;
        this.neo4jPort = port;
        this.neo4jDBName = dbName;
        this.neo4jUserName = uName;
        this.neo4jPassword = passwd;
        try {
            String driver = "org.neo4j.jdbc.Driver";
            String url = "jdbc:neo4j:bolt://" + neo4jHost + ":" + neo4jPort + "/" + neo4jDBName;
            // System.out.println(url);
            Class.forName(driver);
            this.neo4jCon = DriverManager.getConnection(url, neo4jUserName, neo4jPassword);
            System.out.println("Neo4j Connection Established");
        } catch (Exception e) {
            System.out.println("Neo4j Connection Failed");
            System.out.println(e);
            System.exit(0);
        }
    }

    public Connection getConnection() throws Exception {
        return this.neo4jCon;
    }

    public void createEntityTypeDefStore() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS \"ENTITY_TYPE_DEF\" (\"Entity_Type_Id\" SERIAL, \"Entity_Type_Name\" TEXT, \"Entity_Type_Attr_Def_List\" TEXT, \"Entity_Type_Unique_Attr_List\" TEXT)";
        try {
            PreparedStatement pstmt = this.neo4jCon.prepareStatement(query);
            pstmt.executeUpdate();
            System.out.println("Create Entity Type Definition Store: Success");
        } catch (Exception e) {
            System.out.println("Create Entity Type Definition Store: Failure");
            System.out.println(e);
            System.exit(0);
        }
    }

    public void createRelationTypeDefStore() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS \"RELATION_TYPE_DEF\" (\"Relation_Type_Id\" SERIAL,\"Relation_Type_Name\" TEXT, \"From_Entity_Type_Name\" TEXT, \"To_Entity_Type_Name\" TEXT, \"Relation_Type_Attr_Def_List\" TEXT, \"Relation_Type_Cardinality_Constraint\" TEXT)";
        try {
            PreparedStatement pstmt = this.neo4jCon.prepareStatement(query);
            pstmt.executeUpdate();
            System.out.println("Create Relation Type Definition Store: Success");
        } catch (Exception e) {
            System.out.println("Create Relation Type Definition Store: Failure");
            System.out.println(e);
            System.exit(0);
        }
    }

    public void createSecurityConditionTypeDefStore() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS \"SECURITY_CONDITION_TYPE_DEF\" (\"Security_Condition_Type_Id\" SERIAL, \"Security_Condition_Type_Name\" TEXT, \"Security_Condition_Type_Attr_Def_List\" TEXT,\"Entity_Relation_List\" TEXT)";
        try {
            PreparedStatement pstmt = this.neo4jCon.prepareStatement(query);
            pstmt.executeUpdate();
            System.out.println("Create Security Condition Type Definition Store: Success");
        } catch (Exception e) {
            System.out.println("Create Security Condition Type Definition Store: Failure");
            System.out.println(e);
            System.exit(0);
        }
    }

    public void createExploitTypeDefStore() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS \"EXPLOIT_TYPE_DEF\" (\"Exploit_Type_Id\" SERIAL,\"Exploit_Type_Name\" TEXT, \"CveId(s)\" TEXT, \"Exploit_Type_Attr_Def_List\" TEXT, \"Precond\" TEXT, \"Precond_Attr_Val_List\" TEXT, \"Postcond_Attr_Val_List\" TEXT, \"Postcond\" TEXT)";
        try {
            PreparedStatement pstmt = this.neo4jCon.prepareStatement(query);
            pstmt.executeUpdate();
            System.out.println("Create Exploit Type Definition Store: Success");
        } catch (Exception e) {
            System.out.println("Create Exploit Type Definition Store: Failure");
            System.out.println(e);
            System.exit(0);
        }
    }

    public void createDefStores() throws Exception {
        // this.createEntityTypeDefStore();
        // this.createRelationTypeDefStore();
        // this.createSecurityConditionTypeDefStore();
        // this.createExploitTypeDefStore();
        System.out.println();
    }

}