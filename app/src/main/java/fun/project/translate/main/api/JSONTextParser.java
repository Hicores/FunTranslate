package fun.project.translate.main.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONTextParser {
    private final JSONObject json;
    public JSONTextParser(String text){
        JSONObject json1;
        try {
            json1 = new JSONObject(text);
        } catch (JSONException e) {
            json1 = new JSONObject();
        }
        json = json1;
    }
    public String getString(String parserText,String def){
        Object result = parse(parserText,json);
        if (result instanceof String){
            return result.toString();
        }else {
            return def;
        }
    }
    public Object get(String parserText){
        return parse(parserText,json);
    }
    private Object parse(String text,JSONObject json){
        try {
            int objPointIndex = text.indexOf(".");
            String objNodeText;
            if (objPointIndex == -1){
                objNodeText = text;
            }else {
                objNodeText = text.substring(0,objPointIndex);
            }

            int arrSplitIndex = objNodeText.indexOf("[");

            Object nextNode;
            if (arrSplitIndex == -1){
                nextNode = json.get(objNodeText);
            }else {
                nextNode = json.getJSONArray(objNodeText.substring(0,arrSplitIndex));
                while (arrSplitIndex != -1){
                    int arrSplitIndexClose = objNodeText.indexOf("]",arrSplitIndex);
                    int arrIndex = Integer.parseInt(objNodeText.substring(arrSplitIndex+1,arrSplitIndexClose));
                    nextNode = ((JSONArray)nextNode).get(arrIndex);
                    arrSplitIndex = objNodeText.indexOf("[",arrSplitIndexClose+1);
                }
            }

            if (objPointIndex == -1){
                return nextNode;
            }else {
                return parse(text.substring(objPointIndex+1), (JSONObject) nextNode);
            }
        }catch (Exception e){
            return null;
        }
    }
}
