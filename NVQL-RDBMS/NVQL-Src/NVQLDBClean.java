import java.io.*;
import java.net.*;
import java.nio.*;
import java.sql.*;
import java.util.*;
import java.lang.*;

public class NVQLDBClean{

    public static void main(String[] args) throws Exception {
        
        String pgsqlHost="localhost";
        String pgsqlPort="5432";
        String pgsqlDBName="NVQL";
        String pgsqlUserName="postgres";
        String pgsqlPassword="abcdefgh";
        Connection pgsqlCon=null;
        
            
        System.out.println("Starting...");       
        try{
            String driver = "org.postgresql.Driver";
            String url = "jdbc:postgresql://" + pgsqlHost + ":" + pgsqlPort + "/" + pgsqlDBName;

            Class.forName(driver);
            pgsqlCon = DriverManager.getConnection(url, pgsqlUserName, pgsqlPassword);
            System.out.println("PostgreSQL Connection Established");
        }catch (Exception e){
            System.out.println("PostgreSQL Connection Failed");
            System.out.println(e);
            System.exit(0);
        }

        String query = "SELECT string_agg(table_name, ',') FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE'";
        ResultSet result = null;
        try {
            PreparedStatement pstmt = pgsqlCon.prepareStatement(query);
            
            result = pstmt.executeQuery();
            System.out.println("Get Table Information: Success");
        } catch (Exception e) {
            System.out.println("Get Table Information: Failure");
            System.out.println(e);
            System.exit(0);
        }
        
        String tableNames = null;
        while(result.next())
            tableNames = result.getString(1);
        //System.out.println(tableNames);
        if (tableNames == null){
            System.out.println("DB is already clean.");
            return;
        }
        String tableName[] = tableNames.split(",");

        String tables = "";
        int i;
        for(i=0; i<tableName.length-1; i++){
            //System.out.println(tableName[i]);
            tables = tables + "\"" + tableName[i] + "\"" + ","; 
        }
        //System.out.println(tableName[i]);
        tables = tables + "\"" + tableName[i] + "\""; 
        
        //System.out.println(tables);

        query = "DROP TABLE IF EXISTS" + tables + "CASCADE";
        result = null;
        try {
            PreparedStatement pstmt = pgsqlCon.prepareStatement(query);
            
            //pstmt.executeQuery();
            pstmt.executeUpdate();
            System.out.println("Drop Tables: Success");
        } catch (Exception e) {
            System.out.println("Drop Tables: Failure");
            System.out.println(e);
            System.exit(0);
        }
    }
}