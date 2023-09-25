package fun.project.translate.main;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import fun.project.translate.utils.DataUtils;

public class PluginInfo {
    public static PluginInfo currentPluginInfo = new PluginInfo();




    public String name;
    public boolean isLimit;
    public int limit;

    public String pluginContent;

    public String toJsonText(){
        try {
            JSONObject newJson = new JSONObject();
            newJson.put("isLimit",isLimit);
            newJson.put("name",name);
            newJson.put("limit",limit);
            newJson.put("plugin", DataUtils.byteArrayToHex(pluginContent.getBytes(StandardCharsets.UTF_8)));
            return newJson.toString();
        } catch (JSONException e) {
            return "error";
        }
    }
    public void fromJson(String json){
        try {
            JSONObject newJson = new JSONObject(json);
            isLimit = newJson.optBoolean("isLimit");
            limit = newJson.optInt("limit");
            name = newJson.optString("name");
            pluginContent = new String(DataUtils.hexToByteArray(newJson.optString("plugin")));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
