
import java.io.*;
//import java.net.*;
//import java.nio.*;
import java.sql.*;
//import java.util.*;
//import java.lang.*;
import java.util.Properties;

public class NVQLPathQuery {

    public static void main(String[] args) throws Exception {

        Connection neo4jCon = null;
        long startTime, endTime;
        NVQLQueryExecTime timeobj = new NVQLQueryExecTime();
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
        
        startTime=System.nanoTime();

        String query = "match (src:aghost{ipAddr: '192.168.148.1'})<-[:AT_HOST]-(gi1:aggi)-[:INSTANCE_OF]->(g:agag{name:'user'}), " +
        "(dst:aghost{ipAddr: '192.168.148.14'})<-[:AT_HOST]-(gi2:aggi)-[:INSTANCE_OF]->(g:agag{name:'user'})" +
        "match p=shortestPath((gi1)-[:NEXT*1..10]->(gi2)) return nodes(p), length(p";

        ResultSet result = null;
        try {
            PreparedStatement pstmt = neo4jCon.prepareStatement(query);

            result = pstmt.executeQuery();

            System.out.println("NVQL Path Query: Success");

            System.out.println(result);
            
        } catch (Exception e) {
            System.out.println("NVQL Path Query: Failure");
            System.out.println(e);
            System.exit(0);
        }

        endTime=System.nanoTime();
        timeobj.displayTime(startTime,endTime);

    }
}