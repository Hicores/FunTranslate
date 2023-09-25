package fun.project.translate.hook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;

import fun.project.translate.QQVer;
import fun.project.translate.R;
import fun.project.translate.Target;
import fun.project.translate.main.MainSetRoot;
import fun.project.translate.utils.LayoutUtils;
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
            XMethod.obj(formItem).name("setLeftText").param(CharSequence.class).invoke("[FunTranslate]翻译脚本设置");
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
                        Array.set(newArr,0,createNTSetItem("[FunTranslate]翻译脚本设置",v->{
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
        Object left = XClass.newInstance(XClass.load("com.tencent.mobileqq.widget.listitem.u$b$b"),title, R.drawable.translate);
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


    @SuppressLint("ResourceType")
    @AutoRun(pkg = Target.WX,period = "activity")
    public static void doHookActivityResume() throws NoSuchMethodException {
        XPBridge.hookAfter(XMethod.clz("com.tencent.mm.ui.vas.VASCommonFragment").name("onCreateView").ignoreParam().get(), param -> {
            View v = (View) param.getResult();
            new Handler(Looper.getMainLooper()).post(()->{
                StringHolder newHolder = new StringHolder("朋友权限","个人信息与权限");
                checkActivityContent((ViewGroup) v,newHolder);
                if (newHolder.getResult()){
                    ListView listView = (ListView) findViewWithClassName((ViewGroup) v,"com.tencent.mm.ui.widget.listview.PullDownListView");
                    if (listView != null && listView.findViewById(123456) == null){
                        XInject.injectContext(listView.getContext());

                        View menuView = getMenuView(listView.getContext());
                        menuView.setId(123456);
                        listView.addHeaderView(menuView);
                        menuView.setOnClickListener(mView ->{
                            Context context = mView.getContext();
                            Intent intent = new Intent(context,MainSetRoot.class);
                            context.startActivity(intent);
                        });

                    }

                }
            });
        });
    }
    private static View getMenuView(Context context){
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(context.getColor(R.color.global_background_color));
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tv = new TextView(context);
        tv.setText("[FunTranslate]翻译脚本设置");
        tv.setTextColor(context.getResources().getColor(R.color.global_font_color));
        tv.setTextSize(16);

        LinearLayout.LayoutParams tv_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_param.leftMargin = LayoutUtils.dip2px(context,16);
        tv_param.rightMargin = LayoutUtils.dip2px(context,16);
        tv_param.topMargin = LayoutUtils.dip2px(context,16);
        tv_param.bottomMargin = LayoutUtils.dip2px(context,16);

        linearLayout.addView(tv,tv_param);
        return linearLayout;
    }
    private static void checkActivityContent(ViewGroup act, StringHolder holder){
        for (int i = 0; i < act.getChildCount(); i++) {
            Object child = act.getChildAt(i);
            if (child instanceof ViewGroup){
                checkActivityContent((ViewGroup) child,holder);
            }else if (child instanceof TextView){
                holder.doCheck(((TextView) child).getText().toString().trim());
            }
        }
    }
    private static View findViewWithClassName(ViewGroup group,String className){
        for (int i = 0; i < group.getChildCount(); i++) {
            Object child = group.getChildAt(i);
            if (child.getClass().getName().equals(className)){
                return (View) child;
            }
            if (child instanceof ViewGroup){
                View v = findViewWithClassName((ViewGroup) child,className);
                if (v != null){
                    return v;
                }
            }

        }
        return null;
    }
    private static class StringHolder{
        public HashSet<String> str;
        public StringHolder(String... str){
            this.str = new HashSet<>();
            this.str.addAll(Arrays.asList(str));
        }
        public void doCheck(String str){
            this.str.remove(str);
        }
        public boolean getResult(){
            return this.str.isEmpty();
        }
    }
}
