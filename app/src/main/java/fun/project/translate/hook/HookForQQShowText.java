package fun.project.translate.hook;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;

import fun.project.translate.Target;
import fun.project.translate.task.UITask;
import fun.xloader.anno.AutoRun;
import fun.xloader.api.XPBridge.XPBridge;
import fun.xloader.api.XReflex.XMethod;
import fun.xloader.api.XUtils.XLog;

public class HookForQQShowText {
    @AutoRun(pkg = Target.QQ)
    public static void doHookShow() throws NoSuchMethodException {
        XPBridge.hookBefore(XMethod.clz("com.tencent.qqnt.aio.widget.AIOMsgTextView").name("setText").ignoreParam().get(),param -> {
            String s = param.args[0].toString().trim();
            if (!TextUtils.isEmpty(s)){
                UITask.submitTask(s,str -> {
                    try {
                        param.args[0] = str;
                        XPBridge.invoke(param.method,param.obj,param.args);
                    } catch (Exception e) {

                    }
                });
            }
        });
        XPBridge.hookBefore(XMethod.clz("com.tencent.qqnt.aio.widget.AIOMsgTextView").name("setTextDrawable").ignoreParam().get(),param -> {
            param.setResult(null);
        });

//        XPBridge.hookBefore(XMethod.clz("com.tencent.qqnt.kernel.nativeinterface.IKernelMsgService$CppProxy").name("native_getMsgs").ignoreParam().get(),param -> {
//            XLog.d("native_getMsgs",param.args[1]);
//        });
//        XPBridge.hookBefore(XMethod.clz("com.tencent.qqnt.kernel.nativeinterface.IKernelMsgService$CppProxy").name("native_getAioFirstViewLatestMsgs").ignoreParam().get(),param -> {
//            XLog.d("native_getAioFirstViewLatestMsgs",param.args[1]);
//        });
//        XPBridge.hookBefore(XMethod.clz("com.tencent.qqnt.kernel.nativeinterface.IKernelMsgService$CppProxy").name("native_getMsgsBySeqRange").ignoreParam().get(),param -> {
//            XLog.d("native_getMsgsBySeqRange",param.args[1]);
//        });
    }
}
