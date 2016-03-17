package ccad.com.camera.model;

/**
 * 作者：Leon Xie
 * 时间： 2015/10/16 0016
 * 邮箱：xiezhixuan@cbpm-kexin.com
 */
public class CODE {

    public static int MODE = 1;//0本地模式 1：网络模式

    public enum DEAL_TYPE {
        BASIC,WHITEMARK,OVMI,JIEXIAN, SAFETHREAD,TYPE_COUNT
    }

    public enum PRODUCT_TYE {
        PRODUCT_9607A,PRODUCT_9607T,PRODUCT_COUNT
    }

    /*单机问题*/
    //拍照问题
    public static final int CAMERANULL = 500;//未检测到相机
    public static final int CAMERAERROR = 501;//相机不合规则
    public static final int MEMORYLOW = 1000;//内存不足
    public static final int FILEERROR = 1100;//文件损坏
    public static final int INITERROR = 1200;//内部错误，传参错误，版本冲突

    /*通讯问题*/
    //成功并返回信息 0-100为可信度，200为成功
    public static final int OK = 200;
    //网络问题
    public static final int NETNOTOPEN = 2000;//网络未启用
    public static final int NETFAIL = 2100;//传输失败
    //服务器问题
    public static final int SERVERCLOSE = 3000;//服务器关闭

    //未知异常
    public static final int UNKNOWSERVERISSUE = 4000;//服务器未知异常

    /*图片问题*/
    public static final int PHOTONULL = 600;//图片中无特征点
    public static final int PHOTOERROR = 601;//图片不合规则
    public static final int NOMODELOPEN = 3100;//模板不匹配表单不符
    public static final int INVALID = 3200;//模版未开放检测
}
