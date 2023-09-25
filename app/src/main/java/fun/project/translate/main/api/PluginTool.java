package fun.project.translate.main.api;

public class PluginTool {
    public Object newSimpleHttp(){
        return SimpleHttpApi.newRequest();
    }
    public Object parseJson(String json){
        return new JSONTextParser(json);
    }
}
