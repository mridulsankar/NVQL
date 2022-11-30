import java.io.*;
//import java.net.*;
//import java.nio.*;
import java.sql.*;
//import java.util.*;
//import java.lang.*;
//import java.util.Properties;
//import java.sql.*;
import java.util.*;


public class NVQL2GDBAGGen {

    public static boolean execQuery(Connection neo4jCon, String query){
        try{
            PreparedStatement statement = neo4jCon.prepareStatement(query);
            statement.executeUpdate();
            return(true);
        }
        catch (Exception e) {
            System.out.println("Neo4j Data Generation Failed");
            System.out.println(e);
            return(false);
        }   
    }

    public static void generateData(Connection neo4jCon){

        //create hosts

        /*

        String query = "MATCH (n:host) where not exists(n.Entity_Type_Unique_Attr_List) " +  
        "with n as map " +
        "create (copy:aghost) " + 
        "set copy=map"; 
        
        if(execQuery(neo4jCon, query)){
            System.out.println("Hosts created successfully...");
        }

        query = "MATCH (n:service) where not exists(n.Entity_Type_Unique_Attr_List) " +
        "with n as map " + 
        "create (copy:agservice) " +
        "set copy=map"; 
        
        if(execQuery(neo4jCon, query)){
            System.out.println("Services created successfully...");
        }

        query = "MATCH (n:vulnerability) where not exists(n.Entity_Type_Unique_Attr_List) " +
        "with n as map " +
        "create (copy:agvulnerability) " +
        "set copy=map ";
        
        if(execQuery(neo4jCon, query)){
            System.out.println("Vunerabilities created successfully...");
        }
        */

        System.out.println("Hello World");
        String query = "match (n:host) where not exists(n.Entity_Type_Unique_Attr_List) return n.name, properties(n)";

        try{
            PreparedStatement statement = neo4jCon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            
            //result.next();
            //System.out.println(result.getFetchSize());
            //System.out.println(result.getString("props"));
            while(result.next()){
                System.out.println(result.getRow());
                System.out.println(result.getString(1));
                
            }
        }
        catch (Exception e) {
            System.out.println("Neo4j Data Generation Failed");
            System.out.println(e);
            
        }   
        
        

        
        
    }

    
    
    
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

        generateData(neo4jCon);
        
    }
}
