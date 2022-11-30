import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

public class NVQLAttrValList {

    private LinkedHashMap<String, String> attrVals = new LinkedHashMap<>();

    private String attrValSeparator = "#";
    private String attrNameValSeparator = "$";

    NVQLAttrValList(String attrValList) {

        attrValList = attrValList.replace(" ", "").trim();
        attrValList = attrValList.replace("#,$", "#"); // #,$ is mapped to # in intermediate steps
        attrValList = attrValList.replace("$:#", "$"); // $:# is mapped to $ in intermediate steps
        attrValList = attrValList.substring(1, attrValList.length() - 1); // starting $ and ending # is eliminated

        StringTokenizer st = new StringTokenizer(attrValList, this.attrValSeparator);
        while (st.hasMoreTokens()) {
            StringTokenizer t = new StringTokenizer(st.nextToken(), this.attrNameValSeparator);
            attrVals.put(t.nextToken(), t.nextToken());
        }
    }

    int getAttrValCount() {
        return attrVals.size();
    }

    Boolean existsAttr(String attrName) {
        return attrVals.containsKey(attrName);
    }

    String getAttrVal(String attrName) {
        return attrVals.get(attrName);
    }

    ArrayList<String> getAttrList() {
        ArrayList<String> al = new ArrayList<>();

        for (String key : attrVals.keySet())
            al.add(key);

        return al;
    }

    String getAttrList4SQL() {
        String attrList = "";

        for (String attr : attrVals.keySet()) {
            attrList = attrList + "\"" + attr + "\"" + ",";
        }

        if (attrList.length() > 0)
            attrList = attrList.substring(0, attrList.length() - 1);

        return attrList;
    }

    Boolean checkForString(String attrVal) {
        if (attrVal.charAt(0) == '"' && attrVal.charAt(attrVal.length() - 1) == '"')
            return true;
        else
            return false;
    }

    Boolean checkForInt(String attrVal) {
        try {
            Integer.parseInt(attrVal);
            return true;
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    Boolean checkForTime(String attrVal) {
        if (attrVal.charAt(0) == '"' && attrVal.charAt(attrVal.length() - 1) == '"')
            return true;
        else
            return false;
    }

    Boolean VerifyTime(String attrVal) {
        if (attrVal.matches("\"([0,1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]\"")) {
            return true;
        } else {
            return false;
        }
    }

    String getValueList4SQL(NVQLAttrDefList aDefList) {

        String list = "";

        for (String attr : attrVals.keySet()) {

            switch (aDefList.getAttrDataType(attr)) {
            // Check for whether the value of an attribute matches with it's defined type or
            // not is not done
            case "string":
                if (checkForString(attrVals.get(attr))) {
                    list = list + attrVals.get(attr).toString().replace('"', '\'') + ",";
                    break;
                } else {
                    System.out.println(
                            "ERROR.NVQLAttrValList: Value for attribute " + attr + " does not match it's type");
                    System.exit(0);
                    return null;
                }

            case "int":
                if (checkForInt(attrVals.get(attr))) {
                    list = list + attrVals.get(attr) + ",";
                    break;
                } else {
                    System.out.println(
                            "ERROR.NVQLAttrValList: Value for attribute " + attr + " does not match it's type");
                    System.exit(0);
                    return null;
                }

            case "time":
                if (!VerifyTime(attrVals.get(attr))) {
                    System.out.println("ERROR.NVQLAttrValList: Invalid Time cannot be inserted");
                    System.exit(0);
                    return null;
                } else if (checkForTime(attrVals.get(attr))) {
                    list = list + attrVals.get(attr).toString().replace('"', '\'') + ",";
                    break;
                } else {
                    System.out.println(
                            "ERROR.NVQLAttrValList: Value for attribute " + attr + " does not match it's type");
                    System.exit(0);
                    return null;
                }
            }
            if (attrVals.get(attr).equals(null))
                return null;
        }
        if (list.length() > 0)
            list = list.substring(0, list.length() - 1);

        return list;
    }
    /*
     * public static void main(String args[]){
     * 
     * NVQLAttrValList avl = new
     * NVQLAttrValList("$name$:#\"h1\"#,$ipAddr$:#\"192.168.128.1, 192.168.128.2\"#,$macAddr$:#\"xx:xx:xx:xx:xx:xx\"#"
     * );
     * 
     * System.out.println(avl.getAttrValCount());
     * System.out.println(avl.existsAttr("name"));
     * System.out.println(avl.existsAttr("nam3e"));
     * System.out.println(avl.getAttrVal("name"));
     * System.out.println(avl.getAttrVal("ipAddr"));
     * System.out.println(avl.getAttrVal("macAddr"));
     * 
     * for (int i =0; i< avl.getAttrList().size();i++){
     * System.out.println(avl.getAttrList().get(i)+":"+avl.getAttrVal(avl.
     * getAttrList().get(i))); }
     * 
     * }
     */

}