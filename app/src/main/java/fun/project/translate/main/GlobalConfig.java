package fun.project.translate.main;

import fun.project.translate.R;
import fun.project.translate.utils.FunConf;
import fun.xloader.anno.AutoRun;
import fun.xloader.api.XUtils.XEnv;

public class GlobalConfig {
    public static String show_format;
    public static boolean global_switch;

    @AutoRun
    public static void initAllConfig(){
        global_switch = FunConf.getBoolean("FunTranslate","global_switch",false);
        show_format = FunConf.getString("FunTranslate","show_format", XEnv.getAppContext().getString(R.string.def_format));
        PluginInfo.currentPluginInfo.limit = FunConf.getInt("FunTranslate","api_limit",0);
        PluginInfo.currentPluginInfo.isLimit = FunConf.getBoolean("FunTranslate","api_limit_open",false);
        PluginInfo.currentPluginInfo.name = FunConf.getString("FunTranslate","name","default");
        PluginInfo.currentPluginInfo.pluginContent = FunConf.getString("FunTranslate","content",XEnv.getAppContext().getString(R.string.def_plugin));
    }
}
