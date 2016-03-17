

package ccad.com.camera;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import ccad.com.camera.callback.PhotoCallBack;
import ccad.com.camera.callback.PhotoSurfaceCallback;
import ccad.com.camera.model.CODE;
import ccad.com.camera.model.CameraModel;
import ccad.com.camera.util.Lg;
import ccad.com.camera.util.ResultDialog;
import ccad.com.camera.util.Utils;

import static ccad.com.camera.model.CODE.DEAL_TYPE.JIEXIAN;

/**
 * 作者：解至煊
 * 时间： 2015.9
 * 邮箱：xiezhixuan@cbpm-kexin.com
 */
public class PhotoActivity extends ActionBarActivity {


    private CODE.DEAL_TYPE dt = CODE.DEAL_TYPE.JIEXIAN;
    private CODE.PRODUCT_TYE pt = CODE.PRODUCT_TYE.PRODUCT_9607A;

    private CameraModel model;
    private PhotoSurfaceCallback photoSurfaceCallback;

    private ImageView guideView;
    public ImageView modelView;
    public ImageView gifView;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private LinearLayout l;
    public static Camera camera;
    public static Button btnSave;
    private DisplayMetrics metric;

    public static final String DEAL_TYPE = "DEAL_TYPE";
    public static final String PRODUCT_TYPE = "PRODUCT_TYPE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_photo);

        Lg.d("PhotoActivity onCreate");
        Intent intent = getIntent(); //接受参数
        try {
            dt = CODE.DEAL_TYPE.values()[intent.getIntExtra(DEAL_TYPE, JIEXIAN.ordinal())];
            pt = CODE.PRODUCT_TYE.values()[intent.getIntExtra(PRODUCT_TYPE, CODE.PRODUCT_TYE.PRODUCT_9607T.ordinal())];
        } catch (ArrayIndexOutOfBoundsException ex) {
            Lg.e("PhotoActivity ArrayIndexOutOfBoundsException");
            Toast.makeText(this,"此模板暂未开放检测！",Toast.LENGTH_SHORT);
            this.finish();
        }
        model = new CameraModel(pt, dt);
        Lg.d("PhotoActivity onCreateView start");
        //获取屏幕宽高度
        metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        photoSurfaceCallback = new PhotoSurfaceCallback(this, metric);
        surfaceView = (SurfaceView) findViewById(R.id.camera_preview); // Camera interface to instantiate components
        surfaceHolder = surfaceView.getHolder(); // Camera interface to instantiate components
        surfaceHolder.addCallback(photoSurfaceCallback); // Add a callback for the SurfaceHolder
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //加载组件
        guideView = (ImageView) findViewById(R.id.cameraGuideImageView);
        btnSave = (Button) findViewById(R.id.save_pic);
        l = (LinearLayout) findViewById(R.id.cameraRightLinearLayout);
        modelView = (ImageView) findViewById(R.id.cameraImageView);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lg.d("PhotoActivity onCreateView click");
                takePic();
                btnSave.setClickable(false);
            }
        });

        //拍照前提示图片
        /*   guideView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageLoader.getInstance().displayImage(referencePic[mparam], guideView, options);
        guideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideView.setVisibility(ImageView.INVISIBLE);
                btnSave.setEnabled(true);
                onLoadModel(rootView);
            }
        });*/
        //拍照前不提示图片
        guideView.setVisibility(ImageView.INVISIBLE);
        network();
        onLoadModel();
    }

    private void network() {
        if (CODE.MODE == 0) {
            btnSave.setEnabled(true);
        }
        if (Utils.isNetworkConnected(this)) {
            int type = Utils.getNetworkType(this);
            if (type == Utils.NETTYPE_WIFI) {
                Lg.d("Jump to PhotoActivity ");
                btnSave.setEnabled(true);
            } else {
                Lg.d("ShowDialog");
                showMultiDia();
            }
        } else {
            Lg.d("Net null");
            Toast.makeText(this, "无法找到网络！", Toast.LENGTH_SHORT);
            this.finish();
        }
    }

    private void showMultiDia() {
        AlertDialog.Builder multiDia = new AlertDialog.Builder(PhotoActivity.this);
        multiDia.setTitle("警告");
        multiDia.setMessage("您正处于2G/3G/4G网络下，联网识别会使用部分流量,是否继续？");

        multiDia.setPositiveButton("继续", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnSave.setEnabled(true);
            }
        });
        multiDia.setNeutralButton("返回", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                PhotoActivity.this.finish();
            }
        });
        multiDia.create().show();
    }

    public void onLoadModel() {
        Lg.d("screen W:" + metric.widthPixels + "   H:" + metric.heightPixels);
        //计算边框信息

        int linearLayoutWidth = l.getWidth();
        if (linearLayoutWidth == 0) {
            linearLayoutWidth = (int) (74.0f * metric.widthPixels / 768);
        }
        Lg.d("PhotoActivity linearLayoutWidth=" + linearLayoutWidth);

        //计算模板在屏幕上的坐标和宽高度
        float w = metric.widthPixels - linearLayoutWidth - model.getOnScreenX() * 2;
        float h = metric.heightPixels - model.getOnScreenY() * 2;
        if (h * model.getRatio() > w) {
            model.setOnScreenH(w / model.getRatio());
            model.setOnScreenW(w);
        } else {
            model.setOnScreenH(h);
            model.setOnScreenW(h * model.getRatio());
        }
        float marginT = (metric.heightPixels - model.getOnScreenH()) / 2;
        float marginL = (metric.widthPixels - linearLayoutWidth - model.getOnScreenW()) / 2;
        model.setOnScreenX(marginL);
        model.setOnScreenY(marginT);
        model.setOnScreenHPercent(model.getOnScreenH() / metric.heightPixels);
        model.setOnScreenWPercent(model.getOnScreenW() / metric.widthPixels);
        model.setOnScreenXPercent(model.getOnScreenX() / metric.widthPixels);
        model.setOnScreenYPercent(model.getOnScreenY() / metric.heightPixels);
        Lg.d("model on screen h=" + model.getOnScreenH() + " " + model.getOnScreenHPercent() + "%"
                + " w=" + model.getOnScreenW() + " " + model.getOnScreenWPercent() + "%"
                + " x=" + model.getOnScreenX() + " " + model.getOnScreenXPercent() + "%"
                + " y=" + model.getOnScreenY() + " " + model.getOnScreenYPercent() + "%");
        //显示蒙版

        ViewGroup.LayoutParams params = modelView.getLayoutParams();
        params.width = (int) model.getOnScreenW();
        params.height = (int) model.getOnScreenH();
        modelView.setLayoutParams(params);
        setMargins(modelView, (int) model.getOnScreenX(), (int) model.getOnScreenY(), 0, 0);
        modelView.setVisibility(ImageView.VISIBLE);
        ImageLoader.getInstance().displayImage(model.getModelSrc(), modelView, options);

        btnSave.setClickable(true);
    }

    private void takePic() {
        Lg.d("PhotoActivity takePic start camera=" + camera);
        shootSound();
        try {
            camera.takePicture(null, null, null, new PhotoCallBack(this, model));
        } catch (RuntimeException e) {
           this.result(CODE.CAMERAERROR, null);
        }
        Lg.d("PhotoActivity takePic down");
    }

    private void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private void shootSound() {
        AudioManager meng = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        if (volume != 0) {
            MediaPlayer shootMP = MediaPlayer.create(this, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (shootMP != null) {
                shootMP.start();
            }
        }
    }

    public void restart(){
        if(camera!=null) {
            camera.stopPreview();
            camera.startPreview();
            model = new CameraModel(pt, dt);
            onLoadModel();
        }else{
            this.finish();
        }
    }

    public void result(int code, String info) {
        ResultDialog dialog = null;
        switch (code) {
            case CODE.OK:
                Integer rst = 0;
                try {
                    rst = Integer.parseInt(info);
                } catch (NumberFormatException e) {
                    dialog = new ResultDialog(this,0);
                    dialog.setText("版本过低！");
                    return;
                }
                if (rst >= 90 && rst <= 100) {
                    dialog = new ResultDialog(this,100);
                } else if (rst >= 0 && rst <= 30) {
                    dialog = new ResultDialog(this,50);
                } else if (rst > 30 && rst < 90) {
                    dialog = new ResultDialog(this,0);
                } else if (rst == 200) {
                    dialog = new ResultDialog(this,0);
                    dialog.setText("暂无数据进行匹配");
                } else if (rst == 600) {
                    dialog = new ResultDialog(this,50);
                    dialog.setText("未检测到有效识别区");
                    //content.setText("请参考照相模板重新拍摄");
                    break;
                } else if (rst == 700 || rst == 701) {
                    dialog = new ResultDialog(this,50);
                    dialog.setText("图像未达到要求");
                    //content.setText("手机后置摄像头像素需高于500万像素");
                    break;
                } else if (rst == 1100) {
                    dialog = new ResultDialog(this,50);
                    dialog.setText("图像意外损坏请重试");
                    break;
                } else if (rst == 3100) {
                    dialog = new ResultDialog(this,0);
                    dialog.setText("模板不匹配");

                    break;
                } else if (rst == 3200) {
                    dialog = new ResultDialog(this,50);
                    dialog.setText("此型号未开放检测");

                    //content.setText("华为P8系列由于板卡问题手机暂不支持");

                    break;
                } else if (rst == 4000) {
                    dialog = new ResultDialog(this,50);
                    dialog.setText("服务器出错请重新尝试");
                    break;
                }
                break;
            case CODE.NETFAIL:
                dialog = new ResultDialog(this,50);
                dialog.setText("传输失败");
                break;
            case 500:
                dialog = new ResultDialog(this,50);
                dialog.setText("相机加载失败");
                break;
            case 1000:
                dialog = new ResultDialog(this,50);
                dialog.setText("手机内存不足");
                break;
            case 2000:
                dialog = new ResultDialog(this,50);
                dialog.setText("网络异常");
                break;
            case 2001:
                dialog = new ResultDialog(this,50);
                dialog.setText("网络未连接");
                break;
        }
        if(dialog!=null) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }else{
            Lg.e("dialog == null");
            this.finish();
        }
    }


    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
            .displayer(new RoundedBitmapDisplayer(0))
            .build();//构建完成
}