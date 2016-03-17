package ccad.com.camera.callback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import ccad.com.camera.CBPMApplication;
import ccad.com.camera.PhotoActivity;
import ccad.com.camera.model.CODE;
import ccad.com.camera.model.CameraModel;
import ccad.com.camera.util.DealUtils;
import ccad.com.camera.util.Lg;
import ccad.com.camera.util.LoadingDialog;

/**
 * 作者：Leon Xie
 * 时间： 2015.9
 * 邮箱：xiezhixuan@cbpm-kexin.com
 */

public class PhotoCallBack implements android.hardware.Camera.PictureCallback {
    private PhotoActivity activity;
    private CameraModel model;

    private final String URL = "http://rmb-app.com:8881/disk/";// "http://tlok666.zicp.net:37319/disk/";
    private final int JPEGQuality = 90;

    public PhotoCallBack(PhotoActivity activity,CameraModel model){
        this.activity = activity;
        this.model = model;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
       Lg.d("PhotoCallBack onPictureTaken start JPEG data length=" + data.length);
        Bitmap cut = null;
        try {
            cut = onCut(data, camera);
        }catch (OutOfMemoryError oor){
            Lg.e("get memory:" + DealUtils.getmem_UNUSED(activity));
            cut = null;
        }
        if(cut==null){
            activity.result(CODE.MEMORYLOW, "");
            return;
        }
       Lg.d("PhotoCallBack onPictureTaken cut done length=" + cut.getRowBytes());
       if(model.getDt().ordinal()==CODE.DEAL_TYPE.OVMI.ordinal()){
           OVMI(cut);
       }else if(model.getDt().ordinal()==CODE.DEAL_TYPE.SAFETHREAD.ordinal()){
           Safethread(cut);
       }else if(model.getDt().ordinal()==CODE.DEAL_TYPE.JIEXIAN.ordinal()){
           JIEXIAN(cut);
       }else{
           activity.result(CODE.INVALID, "");
           return;
       }

    }

    private void JIEXIAN(Bitmap bitmap){
       // bitmap =
        if(CODE.MODE==0) {
            String fname = DateFormat.format("yyyyMMddhhmmss", new Date()).toString() + ".jpg";
            String path = ((CBPMApplication) activity.getApplication()).getCBPMPath() + "Camera" + File.separator + "JIEXIAN" + File.separator;
            saveToSDCard(bitmap, path, fname);
            Toast.makeText(activity,
                    "已完成1/1张,即将开始新的拍摄", Toast.LENGTH_SHORT).show();
            activity.restart();
            //test(bitmap);
        }else {
            onUpload(bitmap);
        }
    }

    private void Safethread(Bitmap bitmap) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = 1024;
        int newHeight = 1600;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                height, matrix, true);
        if (CODE.MODE == 0) {
            String fname = DateFormat.format("yyyyMMddhhmmss", new Date()).toString() + ".jpg";
            String path = ((CBPMApplication) activity.getApplication()).getCBPMPath() + "Camera" + File.separator + "Safethread" + File.separator;

            saveToSDCard(resizedBitmap, path, fname);
            Toast.makeText(activity,
                    "已完成1/1张,即将开始新的拍摄", Toast.LENGTH_SHORT).show();
           activity.restart();
            //test(bitmap);
        } else {
            onUpload(resizedBitmap);
        }
    }

    private void OVMI(Bitmap bitmap){
        if(model.NUMBER>0){
            String fname = DateFormat.format("yyyyMMddhhmmss", new Date()).toString()+"-first.jpg";
            String path = ((CBPMApplication)activity.getApplication()).getCBPMPath()+"Camera"+ File.separator + "OVMI"+ File.separator;
            saveToSDCard(bitmap, path, fname);
            model.NUMBERP();
            model.prePicPath = path+fname;
            Toast.makeText(activity,
                    "已完成1/2张，请按模板提示倾斜45度拍摄第二张！", Toast.LENGTH_LONG).show();
            activity.onLoadModel();
            PhotoActivity.camera.startPreview();
            PhotoActivity.btnSave.setClickable(true);
        }else{
            Bitmap first = null;
            first = BitmapFactory.decodeFile(model.prePicPath);
            bitmap = add2Bitmap(first, bitmap);

            if (CODE.MODE == 0) {
                String fname = DateFormat.format("yyyyMMddhhmmss", new Date()).toString() + "-combine.jpg";
                String path = ((CBPMApplication)activity.getApplication()).getCBPMPath()+"Camera"+ File.separator + "OVMI"+ File.separator;
                saveToSDCard(bitmap, path, fname);
                Toast.makeText(activity,
                        "已完成2/2张,即将开始新的拍摄", Toast.LENGTH_SHORT).show();
               activity.restart();
            } else {
                Toast.makeText(activity,
                        "已完成2/2张,正在验证中....", Toast.LENGTH_SHORT).show();
                onUpload(bitmap);

            }
            delete(model.prePicPath);
        }

    }

    private Bitmap add2Bitmap(Bitmap first, Bitmap second) {
        int width = (int)((float)second.getWidth()/0.8f);
        width = width+(8-width%8)%8;
        int height = (int)((float)second.getHeight()/0.35f);
        Bitmap result = Bitmap.createBitmap(width, height,
                        Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(second,
                0, height * 0.65f, null);
        return result;
    }

    private boolean delete(String path) {
        if(path==null)
            return true;
        File picture = new File(path);
        if (picture.exists()) {
            return picture.delete();
        }
        return true;
    }

    private void saveToSDCard(byte[] data,String path,String name){
        File picture = makeDir(path, name);
        try {
            FileOutputStream fos = new FileOutputStream(picture); // Get file output stream
            fos.write(data, 0, data.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
           Lg.d(e.getMessage());
        }
       Lg.d("write done " + DealUtils.getAvailableInternalMemorySize() + "/" + DealUtils.getTotalInternalMemorySize());
    }

    private void saveToSDCard(Bitmap bitmap,String path,String name){
        File picture = makeDir(path, name);
        try {
            FileOutputStream fos = new FileOutputStream(picture); // Get file output stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEGQuality, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
           Lg.d(e.getMessage());
        }
       Lg.d("write done "+DealUtils.getAvailableInternalMemorySize()+"/"+DealUtils.getTotalInternalMemorySize());
    }

    private File makeDir(String path, String name){
        File pathFile = new File(path);
        if(!pathFile.exists()){
            pathFile.mkdirs();
        }
        return new File(path+name);
    }

    private Bitmap onCut(byte[] data, Camera camera) {
        Lg.d("PhotoCallBack onCut");
        Bitmap mBitmap = null;
        Lg.v("decodeByteArray pre memory:" + DealUtils.getmem_UNUSED(activity));
        Camera.Size size = camera.getParameters().getPictureSize();
        Lg.d("PhotoCallBack OnCut original height=" + size.height + "  width=" + size.width);
        try {
            Lg.e("data length=" + data.length);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            if(size.height>2350){
                options.inSampleSize = 4;
            }else if(size.height>1750){
                options.inSampleSize = 3;
            }else if(size.height>1250){
                options.inSampleSize = 2;
            }
            //options.
            mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (OutOfMemoryError e) {
            Lg.e("get memory:" + DealUtils.getmem_UNUSED(activity));
            return null;
        }
        Lg.v("decodeByteArray done memory:" + DealUtils.getmem_UNUSED(activity));
        int height = mBitmap.getHeight();
        int width = mBitmap.getWidth();
        Lg.d("PhotoCallBack OnCut inSampleSize height=" + height + "  width=" + width);
        Lg.d("model int pic percent" + model.getOnScreenXPercent() + "  " + model.getOnScreenYPercent() + "  " + model.getOnScreenWPercent() + "  " + model.getOnScreenHPercent() + "  ");
        float modelInPicXYWH[] = {model.getOnScreenXPercent() * width, model.getOnScreenYPercent() * height, model.getOnScreenWPercent() * width, model.getOnScreenHPercent() * height};
        Lg.d("model in pic " + modelInPicXYWH[0] + "  " + modelInPicXYWH[1] + "  " + modelInPicXYWH[2] + "  " + modelInPicXYWH[3] + "  ");
        float[] cutXYWHPercent = model.getCutPos();
        Lg.d("cut in model percent " + cutXYWHPercent[0] + "  " + cutXYWHPercent[1] + "  " + cutXYWHPercent[2] + "  " + cutXYWHPercent[3] + "  ");
        float cutInModelXYWH[] = {modelInPicXYWH[2] * cutXYWHPercent[0], modelInPicXYWH[3] * cutXYWHPercent[1], modelInPicXYWH[2] * cutXYWHPercent[2], modelInPicXYWH[3] * cutXYWHPercent[3]};
        Lg.d("cutInModelXYWH " + cutInModelXYWH[0] + "  " + cutInModelXYWH[1] + "  " + cutInModelXYWH[2] + "  " + cutInModelXYWH[3] + "  ");
        int cutXYWH[] = {(int) (modelInPicXYWH[0] + cutInModelXYWH[0]), (int) (modelInPicXYWH[1] + cutInModelXYWH[1]), (int) cutInModelXYWH[2], (int) cutInModelXYWH[3]};
        Lg.d("cutXYWH " + cutXYWH[0] + "  " + cutXYWH[1] + "  " + cutXYWH[2] + "  " + cutXYWH[3] + "  ");
        cutXYWH[2] = cutXYWH[2] + (8 - cutXYWH[2] % 8) % 8;
        Lg.d("cutXYWH " + cutXYWH[0] + "  " + cutXYWH[1] + "  " + cutXYWH[2] + "  " + cutXYWH[3] + "  ");
        Lg.v("pre cut memory:" + DealUtils.getmem_UNUSED(activity));

        try {
            mBitmap = Bitmap.createBitmap(mBitmap, cutXYWH[0], cutXYWH[1], cutXYWH[2], cutXYWH[3]);
        } catch (OutOfMemoryError ooe) {
            Lg.e("memory:" + DealUtils.getmem_UNUSED(activity));
            return null;
        }
        Lg.v("final memory:" + DealUtils.getmem_UNUSED(activity));
        Lg.d("PhotoCallBack OnCut dealing height=" + mBitmap.getHeight() + "  cut width=" + mBitmap.getWidth());
        return mBitmap;

    }

    private void onUpload(Bitmap bitmap){
       Lg.d("PhotoCallBack onUpload start");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEGQuality, baos);
        byte[] b = baos.toByteArray();
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<1000;i++){
            String hex = Integer.toHexString(b[ i ] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        Lg.e(sb.toString());
        ByteArrayInputStream bais = new ByteArrayInputStream(b);

       Lg.d("PhotoCallBack onUpload Stream change done");
        HttpUtils http = new HttpUtils();

        http.configResponseTextCharset("UTF-8");
        RequestParams params = new RequestParams();

        params.addBodyParameter("headImg", bais, b.length);
        params.addBodyParameter("nProduct", "" + model.getPt().ordinal());
        params.addBodyParameter("nDealType", "" + model.getDt().ordinal());
        params.addBodyParameter("sPhoneType", getPhoneInfo());
       // params.addBodyParameter("sPosition",getLocation());

       Lg.d("PhotoCallBack onUpload Http init done");
        http.send(HttpRequest.HttpMethod.POST,
                URL,
                params,
                new RequestCallBack<String>() {
                    LoadingDialog dialog;

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        if (isUploading) {
                            dialog.setText("已上传" + (int) (current / 1000) + "/" + (int) (total / 1000) + "K");
                            //testTextView.setText("upload: " + current + "/" + total);
                        } else {
                            //testTextView.setText("reply: " + current + "/" + total);
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (dialog != null) {
                            try {
                                dialog.cancel();
                            } catch (IllegalArgumentException e) {
                                CrashReport.postCatchedException(new Throwable("onSuccess dialog.cancel() dismiss"));  // bugly会将这个throwable上报
                            }
                        }
                        Lg.d("PhotoCallBack onUpload success:" + responseInfo.statusCode + "\n" + responseInfo.result);
                       activity.result(CODE.OK, responseInfo.result);
                    }

                    @Override
                    public void onStart() {
                        dialog = new LoadingDialog(activity);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                        Lg.d("PhotoCallBack onUpload error ExceptionCode:" + error.getExceptionCode() + "\nMsg:" + msg);
                       activity.result(CODE.NETFAIL, msg);
                    }
                });
    }

    private String getPhoneInfo() {
        TelephonyManager mTm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuffer sb = new StringBuffer();
        sb.append(android.os.Build.BRAND).append(",").append(android.os.Build.MODEL).append(",").append(mTm.getDeviceId()).append(",").append(mTm.getSubscriberId());
        return sb.toString();
    }

    private String getLocation(){
        //获取地理位置管理器
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = null;
        List<String> providers = null;
        //获取所有可用的位置提供器
        if(locationManager!=null){
            providers = locationManager.getProviders(true);
        }
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }else{
            return "none";
        }
        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if(location!=null){
            return location.getLongitude()+","+location.getLatitude();//经度，维度
        }
        return "none";
    }

}
