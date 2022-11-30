
import java.io.*;
//import java.net.*;
//import java.nio.*;
import java.sql.*;
//import java.util.*;
//import java.lang.*;
import java.util.Properties;

public class NVQLDBClean {

    public static void main(String[] args) throws Exception {

        Connection neo4jCon = null;
        String propFileName = "./Resources/config.properties";
        String neo4jPort = "", neo4jPassword = "", neo4jIpAddress = "", neo4jUserName = "";

        NVQLReadPropertiesFileTest readProperties = new NVQLReadPropertiesFileTest();
        try {
            Properties prop = readProperties.readPropertiesFile(propFileName);
            neo4jPort = prop.getProperty("neo4jPort");
            neo4jPassword = prop.getProperty("neo4jPassword");
            neo4jIpAddress = prop.getProperty("neo4jIpAddress");
            neo4jUserName = prop.getProperty("neo4jUserName");
            System.out.println("Reading values from config.properites...");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        NVQLInitNeo4jDB t = new NVQLInitNeo4jDB(neo4jIpAddress, neo4jPort, "", neo4jUserName, neo4jPassword);

        neo4jCon = t.getConnection();

        String query = "match (n) detach delete n";
        ResultSet result = null;
        try {
            PreparedStatement pstmt = neo4jCon.prepareStatement(query);

            result = pstmt.executeQuery();

            System.out.println("Clean Ne04J DB: Success");
        } catch (Exception e) {
            System.out.println("Clean Ne04J DB: Failure");
            System.out.println(e);
            System.exit(0);
        }

    }
}