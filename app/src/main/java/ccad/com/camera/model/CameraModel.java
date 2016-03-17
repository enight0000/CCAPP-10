package ccad.com.camera.model;

/**
 * 作者：Leon Xie
 * 时间： 2015/10/16 0016
 * 邮箱：xiezhixuan@cbpm-kexin.com
 */
public class CameraModel {
    public int NUMBER = 0;
    public String prePicPath;
    private int id;
    private CODE.DEAL_TYPE dt;
    private CODE.PRODUCT_TYE pt;
    public CameraModel(CODE.PRODUCT_TYE pt,CODE.DEAL_TYPE dt){
        this.pt = pt;
        this.dt = dt;
        setId();
    }

    private String[] guidePic = {
            "",
            "",
            ""
    };
    private String[] modelPic = {
            "assets://camera/model1.png",//接线
            "assets://camera/model1.png",//OVMI-1
            "assets://camera/model2.png",//OVMI-2
            "assets://camera/model6.png"//10元
    };
    private String[] gifPic = {
            "",
            "",
            "",
            ""
    };
    private final float[] infoLocPercent = {//x y w h
            400f/1280f,220f/960f,640f/1280f,730f/960f,
            470f/1280f,220f/960f,500f/1280f,280f/960f,
            510f/1280f,260f/960f,400f/1280f,160f/960f,
            600f/1280f,0,680f/1280f,1
    };

    private void setId(){
        if(dt.ordinal()== CODE.DEAL_TYPE.JIEXIAN.ordinal() && pt.ordinal()==CODE.PRODUCT_TYE.PRODUCT_9607T.ordinal()){
            id = 0;
            NUMBER = 0;
        }else if(dt.ordinal()==CODE.DEAL_TYPE.OVMI.ordinal() && pt.ordinal()==CODE.PRODUCT_TYE.PRODUCT_9607T.ordinal()){
            id = 1;
            NUMBER = 1;
        }else if(dt.ordinal()==CODE.DEAL_TYPE.SAFETHREAD.ordinal() && pt.ordinal()==CODE.PRODUCT_TYE.PRODUCT_9607T.ordinal()){
            id = 3;
            NUMBER = 0;
        }
    }

    public void NUMBERP(){
        if(NUMBER>0) {
            NUMBER--;
            id++;
        }
    }


    //相机preview在显示屏上的拉伸比例
    private float cameraToScreenRatio;
    //蒙版在显示屏上的坐标、长、宽
    private float onScreenX = 20,onScreenY = 30,onScreenW,onScreenH;
    //蒙版在显示屏上的百分比坐标、长、宽
    private float onScreenXPercent,onScreenYPercent,onScreenWPercent,onScreenHPercent;
    //蒙版真实长、宽、比例
    private float ratio = 4f/3f;

    public float[] getCutPos(){
        float[] r = new float[4];
        r[0] = infoLocPercent[id*4];
        r[1] = infoLocPercent[id*4+1];
        r[2] = infoLocPercent[id*4+2];
        r[3] = infoLocPercent[id*4+3];
        return r;
    }



    public String getModelSrc(){
        return modelPic[id];
    }
    public String getGifSrc(){
        return gifPic[id];
    }
    public String getGuideSrc(){
        return guidePic[id];
    }

    public int getId() {
        return id;
    }

    public String[] getGuidePic() {
        return guidePic;
    }

    public void setGuidePic(String[] guidePic) {
        this.guidePic = guidePic;
    }

    public String[] getModelPic() {
        return modelPic;
    }

    public void setModelPic(String[] modelPic) {
        this.modelPic = modelPic;
    }

    public float getCameraToScreenRatio() {
        return cameraToScreenRatio;
    }

    public void setCameraToScreenRatio(float cameraToScreenRatio) {
        this.cameraToScreenRatio = cameraToScreenRatio;
    }

    public float getOnScreenX() {
        return onScreenX;
    }

    public void setOnScreenX(float onScreenX) {
        this.onScreenX = onScreenX;
    }

    public float getOnScreenY() {
        return onScreenY;
    }

    public void setOnScreenY(float onScreenY) {
        this.onScreenY = onScreenY;
    }

    public float getOnScreenW() {
        return onScreenW;
    }

    public void setOnScreenW(float onScreenW) {
        this.onScreenW = onScreenW;
    }

    public float getOnScreenH() {
        return onScreenH;
    }

    public void setOnScreenH(float onScreenH) {
        this.onScreenH = onScreenH;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public float getOnScreenXPercent() {
        return onScreenXPercent;
    }

    public void setOnScreenXPercent(float onScreenXPercent) {
        this.onScreenXPercent = onScreenXPercent;
    }

    public float getOnScreenYPercent() {
        return onScreenYPercent;
    }

    public void setOnScreenYPercent(float onScreenYPercent) {
        this.onScreenYPercent = onScreenYPercent;
    }

    public float getOnScreenWPercent() {
        return onScreenWPercent;
    }

    public void setOnScreenWPercent(float onScreenWPercent) {
        this.onScreenWPercent = onScreenWPercent;
    }

    public float getOnScreenHPercent() {
        return onScreenHPercent;
    }

    public void setOnScreenHPercent(float onScreenHPercent) {
        this.onScreenHPercent = onScreenHPercent;
    }

    public CODE.DEAL_TYPE getDt() {
        return dt;
    }

    public CODE.PRODUCT_TYE getPt() {
        return pt;
    }
}
