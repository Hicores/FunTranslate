package fun.project.translate.main;

import bsh.BshMethod;
import bsh.Interpreter;
import fun.project.translate.main.api.PluginTool;

public class PluginManager {
    private static Interpreter interpreter;
    public static void stop(){
        if (interpreter != null){
            interpreter.getNameSpace().clear();
            interpreter = null;
        }
    }
    public static void load(String text){
        if (interpreter != null){
            stop();
        }
        try {
            interpreter = new Interpreter();
            interpreter.set("tool",new PluginTool());
            interpreter.eval(text, " FunTranslatePlugin");
            BshMethod initMethod = interpreter.getNameSpace().getMethod("init",new Class[0]);
            initMethod.invoke(new Object[0], interpreter);

        } catch (Exception e) {
            stop();
            throw new RuntimeException(e);
        }

    }
    public static String doTranslate(String text){
        if (interpreter == null){
            load(PluginInfo.currentPluginInfo.pluginContent);
        }
        if (interpreter != null){
            try {
                BshMethod initMethod = interpreter.getNameSpace().getMethod("onViewText",new Class[]{
                        String.class
                });
                return (String) initMethod.invoke(new Object[]{text},interpreter);
            }catch (Exception e){
                return e.getMessage();
            }
        }
        return null;
    }
}
