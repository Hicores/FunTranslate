package fun.project.translate.abstractApi;

import java.util.HashMap;

public interface ITranslateApi {
    void submitTranslate(String hash,String text, ITranslateResult result);
    String isAvailable();
    void submitConfig(HashMap<String,String> config);
    interface ITranslateResult{
        void onTranslateResult(String hash,String result);
    }
}
