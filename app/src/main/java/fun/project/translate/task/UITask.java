package fun.project.translate.task;

import fun.project.translate.simpleCache.HashCache;

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
                HashCache.set(hash, result1);
                callback.onUIResult(result1);
            });
        }
    }
}
