import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

public class NVQLAttrDefList {

    private LinkedHashMap<String, String> attrDefs = new LinkedHashMap<>(); // Attribute definition list is implemented
                                                                            // as a hash map

    private String attrDefSeparator = ","; // Attribute definitions are separated by ','
    private String attrNameTypeSeparator = ":"; // Individual attribute definitions consists of attribute name and it's
                                                // data type separated by a ':'

    // Constructor - creates the hash map from attribute definition list string

    NVQLAttrDefList(String attrDefList) {

        attrDefList = attrDefList.replace(" ", "").trim(); // Remove any blank space in attribute definition list string
        StringTokenizer st = new StringTokenizer(attrDefList, this.attrDefSeparator); // Separete the individual
                                                                                      // attribute definitions
                                                                                      // For each such attribute
                                                                                      // definition

        while (st.hasMoreTokens()) {
            // Separate the attribute name and its data type
            StringTokenizer t = new StringTokenizer(st.nextToken(), this.attrNameTypeSeparator);
            // Insert attribute name and associated data type to the hash map
            attrDefs.put(t.nextToken(), t.nextToken());
        }
    }

    // Returns number of attributes defined the list

    int getAttrDefCount() {
        return attrDefs.size();
    }

    // Check whether an attribute name is defined or not

    Boolean existsAttr(String attrName) {
        return attrDefs.containsKey(attrName);
    }

    // Returns the data type of an attribute if defined

    String getAttrDataType(String attrName) {
        if (attrDefs.containsKey(attrName))
            return attrDefs.get(attrName);
        else
            return null;
    }

    // Returns list of attribute names (without their data types) as an arraylist

    ArrayList<String> getAttrList() {
        ArrayList<String> al = new ArrayList<>();

        for (String key : attrDefs.keySet())
            al.add(key);

        return al;

    }

    // Converts the attribute definition list to a form suitable for use in an SQL
    // query

    String convertADL4SQL() {
        String list = "";
        for (String key : attrDefs.keySet()) {
            list = list + "\"" + key + "\" ";
            switch (attrDefs.get(key)) {
            case "string":
                list = list + "TEXT" + ",";
                break;
            case "int":
                list = list + "INT" + ",";
                break;
            case "time":
                list = list + "TIME" + ",";
                break;
            }
        }
        if (list.length() > 0)
            list = list.substring(0, list.length() - 1);

        return list;
    }

    /*
     * public static void main(String args[]){ //NVQLAttrDefList adl = new
     * NVQLAttrDefList("name :         string,  ipAddr        : string            , mount : int"
     * ); NVQLAttrDefList adl = new NVQLAttrDefList("");
     * 
     * System.out.println(adl.getAttrDefCount());
     * System.out.println(adl.existsAttr("name"));
     * System.out.println(adl.existsAttr("nam3e"));
     * System.out.println(adl.getAttrDataType("name"));
     * System.out.println(adl.getAttrDataType("ipAddr"));
     * 
     * for (int i =0; i< adl.getAttrList().size();i++){
     * System.out.println(adl.getAttrList().get(i)+":"+adl.getAttrDataType(adl.
     * getAttrList().get(i))); }
     * 
     * System.out.println(adl.convertADL4SQL()); }
     * 
     */
}