package fun.project.translate.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fun.project.translate.main.PluginInfo;
import fun.project.translate.main.PluginManager;
import fun.project.translate.simpleCache.HashCache;
import fun.xloader.api.XUtils.XLog;

public class UITask {
    public interface UICallback{
        void onUIResult(String text);
    }
    public static void submitTask(String text,UICallback callback){
        String commonHash = text.hashCode()+""+(text+"|hash").hashCode();
        String result = HashCache.find(commonHash);
        if (result != null){
            callback.onUIResult(result);
        }else {
            TranslateTask.submitTranslateTask(commonHash, text, (hash, result1) -> {
                if (!PluginInfo.currentPluginInfo.skipCacheWhenError || !Pattern_Matches(result1,PluginInfo.currentPluginInfo.errorRegex)){
                    HashCache.set(hash, result1);
                }

                callback.onUIResult(result1);
            });
        }
    }


    public static boolean Pattern_Matches(String raw, String Regex) {
        try {
            Pattern pt = Pattern.compile(Regex);
            Matcher matcher = pt.matcher(raw);
            return matcher.find();
        } catch (Exception e) {
            return false;
        }

    }
}
