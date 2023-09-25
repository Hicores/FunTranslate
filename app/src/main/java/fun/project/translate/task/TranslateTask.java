package fun.project.translate.task;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;

import fun.project.translate.main.PluginManager;

public class TranslateTask {
    public interface TranslateTaskCallback{
        void onTranslateResult(String hash,String result);
    }
    private static class TranslateTaskInfo{
        public String text;
        public ArrayList<TranslateTaskCallback> callbacks = new ArrayList<>();
    }
    private static final HashMap<String,TranslateTaskInfo> translateInfo = new HashMap<>();
    public static void submitTranslateTask(String hash,String text,TranslateTaskCallback callback){
        synchronized (translateInfo){
            TranslateTaskInfo info = translateInfo.getOrDefault(hash,new TranslateTaskInfo());
            info.text = text;
            info.callbacks.add(callback);
            translateInfo.put(hash,info);
        }

    }
    static {
        new Thread(TranslateTask::workerThread,"Translate_Thread").start();
    }
    public static void workerThread(){
        while (true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { break; }
            TranslateTaskInfo info = null;
            String sKey = null;
            for (String key : translateInfo.keySet()){
                info = translateInfo.get(key);
                sKey = key;
                break;
            }
            if (info != null){
                try {
                    String result = PluginManager.doTranslate(info.text);
                    if (result != null){
                        synchronized (translateInfo){
                            translateInfo.remove(sKey);
                        }
                        TranslateTaskInfo finalInfo = info;
                        String finalSKey = sKey;
                        new Handler(Looper.getMainLooper()).post(()->{
                            for (TranslateTaskCallback callback : finalInfo.callbacks){
                                callback.onTranslateResult(finalSKey,result);
                            }
                        });

                    }
                }catch (Throwable ignored){

                }
            }

        }
    }
}
