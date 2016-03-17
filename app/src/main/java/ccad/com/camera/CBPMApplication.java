package ccad.com.camera;

import android.app.Application;
import android.os.Environment;

import com.lidroid.xutils.DbUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

/**
 * 作者：Leon Xie
 * 时间： 2015.9
 * 邮箱：xiezhixuan@cbpm-kexin.com
 */

public class CBPMApplication extends Application {

    public static String DIR = Environment.getExternalStorageDirectory().toString() + File.separator + "CCAPP"+File.separator;
    public DbUtils db;
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "900009897", false);
        File cacheFile = StorageUtils.getOwnCacheDirectory(getApplicationContext(), DIR + "Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCache(new UnlimitedDiskCache(cacheFile)) // default
                .diskCacheFileCount(100)
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);


    }

    public String getCBPMPath() {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
           return DIR;
        }
        return null;
    }
}
