package unc.tools.checkstyle;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import unc.tools.checkstyle.commits.ProjectHistoryChecker;

public class JSONTest {
  static Map<String, Set> map = new HashMap<>();
  static Set<String> set1 = new HashSet();
  static Set<String> set2 = new HashSet();
  
  public static void main (String[] args) {
    Date aDate = new Date(System.currentTimeMillis());
    JSONObject aDateJSON = new JSONObject(aDate);
    String aJSONDateString = aDateJSON.toString();
    JSONObject aParsedJSON = new JSONObject(aDateJSON);
    Date aReadDate = new Date();
    ProjectHistoryChecker.toBean(aParsedJSON, aReadDate);
    
   set1.add("element1");
   set1.add("element2");
   set2.add("element3");
   set2.add("element4");
   map.put("key1", set1);
   map.put("key2", set2);
   JSONArray aSet1JSON = new JSONArray(set1);
   String aSet1String = aSet1JSON.toString();
   JSONArray aSet2JSON = new JSONArray(set2);
   String aSet2String = aSet2JSON.toString();
   JSONObject aMapJSON = new JSONObject(map);
   String aMapString = aMapJSON.toString();
   JSONArray aDeserializedJSONArray1 = new JSONArray(aSet1String);
   JSONArray aDeserializedJSONArray2 = new JSONArray(aSet2String);
   Set aDeserializedSet1 = new HashSet (aDeserializedJSONArray1.toList());
   JSONObject adeserializedJSONMap = new JSONObject(aMapString);
   Iterator<String> akeys = adeserializedJSONMap.keys();
   while (akeys.hasNext()) {
     Object aValue = adeserializedJSONMap.get(akeys.next());
     System.out.println(aValue);

   }
   
   System.out.println(adeserializedJSONMap);

   


    
  }

}
