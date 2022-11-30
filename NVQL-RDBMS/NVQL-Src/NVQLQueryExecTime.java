public class NVQLQueryExecTime{
    
    void displayTime(long start_time, long end_time){
        double totalTime=Math.round(0.000001*(end_time-start_time)*100.0)/100.0;
        System.out.println("Query Processing Time: " +totalTime+ " ms.\n");
    }

    /*
    public static void main(String args[]){
        
        AGQLQueryExecTime timeobj = new AGQLQueryExecTime(200,34); 
    }
    */
    
    
}