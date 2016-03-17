package ccad.com.camera.callback;

import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ccad.com.camera.PhotoActivity;
import ccad.com.camera.model.CODE;
import ccad.com.camera.util.Lg;

/**
 * 作者：Leon Xie
 * 时间： 2015/10/16 0016
 * 邮箱：xiezhixuan@cbpm-kexin.com
 */
public class PhotoSurfaceCallback implements SurfaceHolder.Callback {
    private DisplayMetrics metric;
    private PhotoActivity activity;
    public PhotoSurfaceCallback(PhotoActivity activity,DisplayMetrics metric) {
        this.activity = activity;
        this.metric = metric;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Lg.d("CameraFragment surfaceCallback====");
        try {
            PhotoActivity.camera = Camera.open(); // Turn on the camera
            PhotoActivity.camera.setPreviewDisplay(holder); // Set Preview
        }catch (NullPointerException e){
            error(e);
            surfaceDestroyed(holder);
        }catch (IOException e) {
            error(e);
            surfaceDestroyed(holder);
        }catch (Exception e){
            error(e);
            surfaceDestroyed(holder);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Lg.d("PhotoActivity ====surfaceChanged");
        if(PhotoActivity.camera==null){
            surfaceDestroyed(holder);
            return;
        }
        Camera.Parameters parameters = PhotoActivity.camera.getParameters(); // Camera parameters to obtain
        try {
        parameters.setPictureFormat(PixelFormat.JPEG);// Setting Picture Format
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//自动连续对焦
        parameters.setJpegQuality(90);

        Camera.Size[] size = getFitSize(parameters.getSupportedPreviewSizes(), parameters.getSupportedPictureSizes(), metric.widthPixels, metric.heightPixels);
        if (size != null) {
            parameters.setPreviewSize(size[0].width, size[0].height);
            parameters.setPictureSize(size[1].width, size[1].height);
            Lg.d("find suitable preview size w=" + size[0].width + " h=" + size[0].height + " r=" + (float) size[0].width / (float) size[0].height);
            Lg.d("find suitable picture size w=" + size[1].width + " h=" + size[1].height + " r=" + (float) size[1].width / (float) size[1].height);
        }

        //PhotoActivity.camera.setDisplayOrientation(0);
        PhotoActivity.camera.setParameters(parameters); // Setting camera parameters
        }catch (RuntimeException e){
            error(e);
            return;
        }
        PhotoActivity.camera.startPreview(); // Start Preview
        PhotoActivity.camera.cancelAutoFocus();// 使用连续的自动对焦

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Lg.d("PhotoActivity ====surfaceDestroyed");
        if(holder!=null)
            holder.removeCallback(this);
        if(PhotoActivity.camera==null)
            return;
        PhotoActivity.camera.setPreviewCallback(null);
        PhotoActivity.camera.stopPreview();// stop preview
        PhotoActivity.camera.lock();
        PhotoActivity.camera.release(); // Release camera resources
        PhotoActivity.camera = null;
    }


    private Rect calculateTapArea(float x, float y, float coefficient, Camera.Size previewSize) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int centerX = (int) (x / previewSize.width * 2000 - 1000);
        int centerY = (int) (y / previewSize.height * 2000 - 1000);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);

        return new Rect(left, top, right, bottom);
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    private Camera.Size[] getFitSize(List<Camera.Size> previewSizes, List<Camera.Size> pictureSizes, int screenW, int screenH) {
        Lg.d("PhotoActivity getSupportedPictureSizes length=" + pictureSizes.size() + " getSupportedPreviewSizes length=" + previewSizes.size());
        Camera.Size[] size = new Camera.Size[2];//0 previewSizes 1 pictureSizes
        float ratio = (float) screenW / (float) screenH;
        Lg.d("screen ratio " + ratio);
        if (previewSizes.size() > 0 && pictureSizes.size() > 0) {
            CameraSizeComparator sizeComparator = new CameraSizeComparator();
            Collections.sort(previewSizes, sizeComparator);
            Collections.sort(pictureSizes, sizeComparator);
            Iterator<Camera.Size> itv = previewSizes.iterator();
            while (itv.hasNext()) {
                size[0] = itv.next();
                float f = (float) size[0].width / (float) size[0].height;
                if (size[0].height<=screenH && Math.abs(f - ratio) < 0.15) {
                    Lg.d("one suitable preview size w=" + size[0].width + " h=" + size[0].height + " r=" + (float) size[0].width / (float) size[0].height);
                    size[1] = getFitPictureSize(pictureSizes, f, 1280);
                    Lg.d("has suitable? picture size ? " + size[1]);
                    if (size[1] != null) {
                        //Lg.d( "suitable picture size w=" + size[1].width + " h=" + size[1].height + " r=" + (float) size[1].width / (float) size[1].height);
                        return size;
                    }
                }
            }
        }
        return null;
    }

    private Camera.Size getFitPictureSize(List<Camera.Size> pictureSizes, float ratio, int minWidth) {
        Camera.Size size = null;
        Iterator<Camera.Size> itp = pictureSizes.iterator();
        while (itp.hasNext()) {
            size = itp.next();

            float r = (float) size.width / (float) size.height;
            Lg.d("suitable picture size w=" + size.width + " h=" + size.height + " r=" + r);
            if (size.width >= minWidth && (Math.abs(r - ratio) < 0.015f)) {
                return size;
            }
        }
        return null;
    }

    private void error(Exception e){
        Lg.e(e.getMessage());
        PhotoActivity.camera.release();// release camera
        PhotoActivity.camera = null;
        activity.result(CODE.CAMERANULL, "");
    }

    class CameraSizeComparator implements Comparator<Camera.Size> {
        //按降序排列
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return -1;
            } else {
                return 1;
            }
        }

    }
}
