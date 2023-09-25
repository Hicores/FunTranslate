package fun.project.translate.hook;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;

import fun.project.translate.Target;
import fun.project.translate.main.GlobalConfig;
import fun.project.translate.task.UITask;
import fun.xloader.anno.AutoRun;
import fun.xloader.api.XPBridge.XPBridge;
import fun.xloader.api.XReflex.XMethod;
import fun.xloader.api.XUtils.XLog;

public class HookForQQShowText {
    @AutoRun(pkg = Target.QQ)
    public static void doHookShow() throws NoSuchMethodException {
        XPBridge.hookBefore(XMethod.clz("com.tencent.qqnt.aio.widget.AIOMsgTextView").name("setText").ignoreParam().get(),param -> {
            Spannable s = (Spannable) param.args[0];
            if (!TextUtils.isEmpty(s) && GlobalConfig.global_switch){
                UITask.submitTask(s.toString(),str -> {
                    try {
                        param.args[0] = GlobalConfig.show_format.replace("$SOURCE_TEXT$",s).replace("$RESULT$",str);
                        XPBridge.invoke(param.method,param.obj,param.args);
                    } catch (Exception ignored) {

                    }
                });
            }
        });
        XPBridge.hookBefore(XMethod.clz("com.tencent.qqnt.aio.widget.AIOMsgTextView").name("setTextDrawable").ignoreParam().get(),param -> {
            param.setResult(null);
        });
    }
    @AutoRun(pkg = Target.WX,period = "activity")
    public static void doHookWXShowText() throws NoSuchMethodException {
        XPBridge.hookBefore(XMethod.clz("com.tencent.neattextview.textview.view.NeatTextView").param(CharSequence.class, TextView.BufferType.class,Boolean.class).get(), param -> {
            if (param.args[0] instanceof SpannableString){
                Spannable s = (Spannable) param.args[0];
                if (!TextUtils.isEmpty(s) && GlobalConfig.global_switch){
                    UITask.submitTask(s.toString(),str -> {
                        try {
                            param.args[0] = GlobalConfig.show_format.replace("$SOURCE_TEXT$",s).replace("$RESULT$",str);
                            XPBridge.invoke(param.method,param.obj,param.args);
                        } catch (Exception ignored) { }
                    });
                }
            }
        });
    }
}
