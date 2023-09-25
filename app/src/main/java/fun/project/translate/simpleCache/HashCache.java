package fun.project.translate.simpleCache;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;

import fun.project.translate.utils.FileUtils;
import fun.xloader.anno.AutoRun;
import fun.xloader.api.XUtils.XEnv;

public class HashCache {
    @AutoRun
    public static void initPath(){
        targetCacheFilePath = XEnv.getExtraCachePath() + "/FunTranslate_" + new SimpleDateFormat("yyyyMMdd") + ".json";
        try{
            JSONObject newJson = new JSONObject(FileUtils.readFileString(targetCacheFilePath));
            Iterator<String> keysIt = newJson.keys();
            while (keysIt.hasNext()){
                String key = keysIt.next();
                memoryCache.put(key, newJson.getString(key));
            }
        }catch (Exception e){

        }
    }
    static {
        new Thread(HashCache::flushThread).start();
    }
    private static final HashMap<String,String> memoryCache = new HashMap<>();
    public static String targetCacheFilePath;
    public static void cleanCache(){
        synchronized (memoryCache){
            memoryCache.clear();
            FileUtils.deleteFile(new File(targetCacheFilePath));
        }
    }
    public static String find(String key){
        synchronized (memoryCache){
            return memoryCache.get(key);
        }
    }
    public static void set(String key,String value){
        synchronized (memoryCache){
            if (value == null){
                memoryCache.remove(key);
            }else {
                memoryCache.put(key,value);
            }

        }
    }
    public static void flushThread(){
        while (true){
            try {
                Thread.sleep(10 * 1000);
                if (targetCacheFilePath != null){
                    HashMap<String,String> copyMap;
                    synchronized (memoryCache){
                        copyMap = new HashMap<>(memoryCache);
                    }
                    JSONObject saveJson = new JSONObject();
                    for (String key : copyMap.keySet()){
                        saveJson.put(key,copyMap.get(key));
                    }
                    FileUtils.writeToFile(targetCacheFilePath,saveJson.toString());
                }
            } catch (Exception e) { }
        }
    }
}
