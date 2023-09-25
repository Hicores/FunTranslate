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
    private Object parse(String text,JSONObject json){
        try {
            int index = text.indexOf(".");
            String headText = index == -1 ? text : text.substring(0,index);
            int arrSplit = headText.indexOf("[");
            int arrIndex = -1;
            if (arrSplit != -1){
                int arrSplitLast = headText.lastIndexOf("]");
                if (arrSplitLast == -1) return null;
                arrIndex = Integer.parseInt(headText.substring(arrSplit+1,arrSplitLast));
                headText = headText.substring(0,arrSplit);
            }
            Object o = json.get(headText);
            if (arrIndex == -1){
                if (index == -1){
                    return o;
                }else {
                    return parse(text.substring(index+1), (JSONObject) o);
                }
            }else {
                JSONArray mArr = (JSONArray) o;
                if (index == -1){
                    return mArr.get(arrIndex);
                }else {
                    return parse(text.substring(index+1), mArr.getJSONObject(arrIndex));
                }
            }
        }catch (Exception e){
            return null;
        }

    }
}
