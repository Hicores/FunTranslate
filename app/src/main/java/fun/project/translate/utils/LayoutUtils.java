package fun.project.translate.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

public class LayoutUtils {
    public static int dip2px(Context context,float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,context.getResources().getDisplayMetrics());
    }

    public static float sp2px(Context context,int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,context.getResources().getDisplayMetrics());
    }
    public static int dip2sp(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density /
                context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    public static boolean isSmallWindowNeedPlay(View v) {
        Rect rect = new Rect();
        boolean visibleRect = v.getGlobalVisibleRect(rect);

        if (visibleRect) {
            Point point = new Point();
            Context baseContext = v.getContext();
            if (!(baseContext instanceof Activity) && (baseContext instanceof ContextWrapper)) {
                baseContext = ((ContextWrapper)baseContext).getBaseContext();
            }


            if (baseContext instanceof Activity) {
                ((Activity) baseContext).getWindowManager().getDefaultDisplay().getSize(point);

                return rect.top >= 0 && (rect.top - 100) <= point.y && rect.left >= 0 && rect.left <= point.x;
            }
        }
        return false;
    }
}
