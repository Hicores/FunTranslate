package fun.project.translate;

import fun.xloader.anno.AutoRun;
import fun.xloader.anno.XposedInit;
import fun.xloader.api.XBridge.XposedInitImpl;
import fun.xloader.api.XSearch.XSearch;
import fun.xloader.api.XUtils.XEnv;

@XposedInit
public class MyXposedInit extends XposedInitImpl {
    @Override
    public String getTag() {
        return "FunTranslate";
    }

    @Override
    public boolean needActivityProxy() {
        return true;
    }

    @AutoRun(priority = 20)
    public static void initXSearch(){
        XSearch.setTargetApk(XEnv.getHostAppPath());
    }
}
