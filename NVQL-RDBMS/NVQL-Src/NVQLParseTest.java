
import java.io.*;
import java.util.*;

public class NVQLParseTest {

    public static void main(String[] args) throws Exception {
        try {
            // NVQLParser parser = new NVQLParser(new FileReader("MSB-Demo1.NVQL"));
            NVQLParser parser = new NVQLParser(new FileReader(args[0]));
            parser.initParser();
            // System.out.println("Success: No Syntax error found. Keep your Hard Work!\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }
}
