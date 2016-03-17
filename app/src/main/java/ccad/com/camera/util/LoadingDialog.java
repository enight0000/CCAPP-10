

package ccad.com.camera.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import ccad.com.camera.R;

/**
 * 作者：Leon Xie
 * 时间： 2015.9
 * 邮箱：xiezhixuan@cbpm-kexin.com
 */
public class LoadingDialog extends Dialog {
    private TextView tv;
    private final static String TAG = "CameraActivity";
    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
    }

    private LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Lg.d("LoadingDialog onCreate");
        setContentView(R.layout.dialog_loading);
        tv = (TextView)this.findViewById(R.id.tv);
        tv.setText("正在上传...");
        LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.LinearLayout);
        linearLayout.getBackground().setAlpha(210);
    }

    public void setText(String arg){
        tv.setText(arg);
    }

}

