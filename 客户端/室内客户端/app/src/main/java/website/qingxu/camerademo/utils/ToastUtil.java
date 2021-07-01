package website.qingxu.camerademo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 在一个类中使用Toast，信息显示在另一个Activity上
 * 实现方法：B中使用Toast显示在A上，简单来说就是在B中定义一个Context变量，在A中通过调用B的构造方法将A自身传进去
 */
public class ToastUtil {
    public static Toast toast;
    public static void showMessage(Context context, String message) {
        if(toast==null) {
            toast= Toast.makeText(context,message,Toast.LENGTH_LONG);
        }else {
            toast.setText(message);
        }
        toast.show();
    }
}
