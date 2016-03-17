

package ccad.com.camera.util;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import ccad.com.camera.PhotoActivity;
import ccad.com.camera.R;

/**
 * 作者：Leon Xie
 * 时间： 2015.9
 * 邮箱：xiezhixuan@cbpm-kexin.com
 */
public class ResultDialog extends Dialog{
    private ImageView imageView;
    private Button cont,back;
    private TextView textView;
    private PhotoActivity activity;
    private int rst;
    private String info;
    public ResultDialog(PhotoActivity context,int rst) {
        super(context, R.style.loadingDialogStyle);
        this.activity = context;
        this.rst = rst;
    }

    private ResultDialog(PhotoActivity context, int theme,int rst) {
        super(context, theme);
        this.activity = context;
        this.rst = rst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Lg.d("LoadingDialog onCreate");
        setContentView(R.layout.dialog_result);
        imageView = (ImageView)findViewById(R.id.imageView4);
        cont = (Button)findViewById(R.id.button2);
        back = (Button)findViewById(R.id.button);
        textView = (TextView)findViewById(R.id.textView2);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout);
        layout.getBackground().setAlpha(210);
       // WindowManager.LayoutParams param = layout.getpar
       // layout.setLayoutParams(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultDialog.this.cancel();
                activity.restart();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultDialog.this.cancel();
                activity.finish();
            }
        });
        if(rst==100){
            //imageView.setBackgroundResource(R.drawable.rst_ok);
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.rst_ok, imageView, options);
        }else if(rst==50){
            //imageView.setBackgroundResource(R.drawable.rst_unknow);
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.rst_unknow, imageView, options);
        }else{
            //imageView.setBackgroundResource(R.drawable.rst_error);
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.rst_error, imageView, options);
        }
        textView.setText(info);
    }

    public void setText(String text){
        this.info = text;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        Lg.e("Dialog capture!-------------------");
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            ResultDialog.this.cancel();
            activity.restart();
            return true;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH){
            return true;
        }
        return false;
    }

    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
            .displayer(new RoundedBitmapDisplayer(0))
            .build();//构建完成
}

