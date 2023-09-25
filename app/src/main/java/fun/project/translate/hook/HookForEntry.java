package fun.project.translate.hook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import fun.project.translate.QQVer;
import fun.project.translate.R;
import fun.project.translate.Target;
import fun.project.translate.main.MainSetRoot;
import fun.xloader.anno.AutoRun;
import fun.xloader.api.XPBridge.XPBridge;
import fun.xloader.api.XReflex.XClass;
import fun.xloader.api.XReflex.XField;
import fun.xloader.api.XReflex.XMethod;
import fun.xloader.api.XSearch.XSearch;
import fun.xloader.api.XUtils.XInject;


public class HookForEntry {
    @AutoRun(pkg = Target.QQ,maxVer = QQVer.QQ_8_9_70)
    public static void hook_before() throws NoSuchMethodException {
        Method m = XMethod.clz("com.tencent.mobileqq.activity.AboutActivity").name("doOnCreate").param(Bundle.class).get();
        XPBridge.hookAfter(m, param -> {

            View f = XField.obj(param.obj)
                    .type(XClass.load("com.tencent.mobileqq.widget.FormSimpleItem"))
                    .filter(((field, value) -> value != null))
                    .get();

            LinearLayout parent = (LinearLayout) f.getParent();

            View formItem = XClass.newInstance(XClass.load("com.tencent.mobileqq.widget.FormSimpleItem"),f.getContext());
            XMethod.obj(formItem).name("setLeftText").param(CharSequence.class).invoke("翻译源设置");
            XMethod.obj(formItem).name("setBgType").param(int.class).invoke(1);
            parent.addView(formItem,2);

            formItem.setOnClickListener(v->{
                Activity activity = (Activity) v.getContext();
                Intent intent = new Intent(activity, MainSetRoot.class);
                activity.startActivity(intent);
            });
        });
    }
    @AutoRun(pkg = Target.QQ,minVer = QQVer.QQ_8_9_70)
    public static void hook_nt_setting_create() {
        Member m = XSearch.findStr("null cannot be cast to non-null type com.tencent.mobileqq.widget.listitem.QUISettingsRecyclerView")
                .filter(result -> result.getDeclaringClass().getName().contains("AboutFragment")).get();
        XPBridge.hookBefore(m,param -> {
            Context mContext = XMethod.obj(param.obj).name("getContext").invoke();
            XInject.injectContext(mContext);
            Constructor<?>[] constructors = XClass.load("com.tencent.mobileqq.widget.listitem.Group").getDeclaredConstructors();
            for (Constructor<?> cons : constructors){
                if (cons.getParameterCount()==3){
                    XPBridge.hookBefore(cons,param1 -> {
                        param1.unhook();
                        Object paramArr = param1.args[2];
                        int length = Array.getLength(paramArr);
                        Object newArr = Array.newInstance(XClass.load("com.tencent.mobileqq.widget.listitem.u"),length+1);
                        for (int i = 0; i < length; i++) {
                            Array.set(newArr,i+1,Array.get(paramArr,i));
                        }
                        Array.set(newArr,0,createNTSetItem("翻译源设置",v->{
                                    Context context = v.getContext();
                                    Intent intent = new Intent(context,MainSetRoot.class);
                                    context.startActivity(intent);},
                                v-> true));

                        param1.args[2] = newArr;
                    });
                }
            }
        });
    }
    private static Object createNTSetItem(String title, View.OnClickListener click, View.OnLongClickListener longClick) throws Exception {
        Member m = XSearch.findStr("group").filter(result -> result.getDeclaringClass().getName().contains("com.tencent.mobileqq.widget.listitem.QUIListItemAdapter")).get();
        Object left = XClass.newInstance(XClass.load("com.tencent.mobileqq.widget.listitem.u$b$b"),title, R.drawable.icon);
        Object right = XClass.newInstance(XClass.load("com.tencent.mobileqq.widget.listitem.u$c$f"),
                new Class[]{ CharSequence.class,
                        boolean.class,
                        boolean.class,
                        int.class,
                        XClass.load("kotlin.jvm.internal.DefaultConstructorMarker")
                },
                "",true,false,4,null);
        Object item = XClass.newInstance(XClass.load("com.tencent.mobileqq.widget.listitem.u"),left,right);
        XMethod.obj(item).param(View.OnClickListener.class).invoke(click);
        Method setOnClickMethod = XMethod.obj(item).name("a").ignoreParam().ret(void.class).get();
        XPBridge.hookAfter(setOnClickMethod,param -> {
            param.unhook();
            WeakReference<View> viewHolder = XField.obj(param.obj).type(WeakReference.class).get();
            View view = viewHolder.get();
            if (view != null) {
                view.setOnLongClickListener(longClick);
            }
        });
        return item;
    }
}
