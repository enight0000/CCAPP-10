package ccad.com.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import ccad.com.camera.model.CODE;

public class MainActivity extends ActionBarActivity {

    private TextView textView1;
    private Button btn_set;
    private ImageButton btn1,btnovmi,btnjx;
    private ImageView layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
       // @drawable/bg
        layout = (ImageView)findViewById(R.id.imageView2);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.bg, layout, options);
        Log.e("wocao","wocao!!");

        btn_set = (Button) findViewById(R.id.btn_set);

        textView1 = (TextView)findViewById(R.id.textView);

        if(CODE.MODE==0){
            textView1.setText("当前状态：单机版");
        }else{
            textView1.setText("当前状态：联机版");
        }

        btn1 = (ImageButton) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(PhotoActivity.DEAL_TYPE, CODE.DEAL_TYPE.SAFETHREAD.ordinal() );
                intent.putExtra(PhotoActivity.PRODUCT_TYPE,CODE.PRODUCT_TYE.PRODUCT_9607T.ordinal() );
                intent.setClass(MainActivity.this, PhotoActivity.class);
                startActivity(intent);
            }
        });
        btn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.btn_on, btn1, options);
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.btn, btn1, options);
                    return false;
                }
                return false;
            }

        });
        btnovmi = (ImageButton) findViewById(R.id.btnovmi);
        btnovmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(PhotoActivity.DEAL_TYPE, CODE.DEAL_TYPE.OVMI.ordinal());
                intent.putExtra(PhotoActivity.PRODUCT_TYPE, CODE.PRODUCT_TYE.PRODUCT_9607T.ordinal());
                intent.setClass(MainActivity.this, PhotoActivity.class);
                startActivity(intent);
            }
        });
        btnovmi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.btn_ovmi_on, btnovmi, options);
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.btn_ovmi, btnovmi, options);
                    return false;
                }
                return false;
            }

        });
        btnjx = (ImageButton) findViewById(R.id.btnjx);
        btnjx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(PhotoActivity.DEAL_TYPE, CODE.DEAL_TYPE.JIEXIAN.ordinal());
                intent.putExtra(PhotoActivity.PRODUCT_TYPE, CODE.PRODUCT_TYE.PRODUCT_9607T.ordinal());
                intent.setClass(MainActivity.this, PhotoActivity.class);
                startActivity(intent);
            }
        });
        btnjx.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.btn_jx_on, btnjx, options);
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    ImageLoader.getInstance().displayImage("drawable://" + R.drawable.btn_jx, btnjx, options);
                    return false;
                }
                return false;
            }

        });
        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CODE.MODE == 0) {
                    CODE.MODE = 1;
                    textView1.setText("当前状态：联机版");
                } else {
                    CODE.MODE = 0;
                    textView1.setText("当前状态：单机版");
                }
            }
        });
    }

    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
            .displayer(new RoundedBitmapDisplayer(0))
            .build();//构建完成

}
