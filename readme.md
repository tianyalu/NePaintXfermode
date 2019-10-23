### NePaintXfermode-刮刮卡效果

参考：[https://github.com/THEONE10211024/ApiDemos/blob/master/app/src/main/java/com/example/android/apis/graphics/Xfermodes.java](https://github.com/THEONE10211024/ApiDemos/blob/master/app/src/main/java/com/example/android/apis/graphics/Xfermodes.java)  

#### 离屏绘制
##### 通过使用离屏缓冲，把要绘制的内容单独绘制在缓冲层，保证Xfermode的使用不会出现错误的结果
* `Canvas.saveLayer()` //可以做可以做短时的离屏缓冲，在绘制之前保存，绘制之后恢复
```android
    int layerId = canvas.saveLayer(0, 0, width, height, mPaint, Canvas.ALL_SAVE_FLAG);
    
    canvas.drawBitmap(rectBitmap, 0, 0, mPaint); //画方
    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN)); //设置混合模式
    canvas.drawBitmap(circleBitmap, 0, 0, mPaint); //画圆
    mPaint.setXfermode(null);  //用完及时清除混合模式

    canvas.restoreToCount(layerId);
```
* `View.setLayerType()` //直接把整个View都绘制在离屏缓冲中
```android
    setLayerType(LAYER_TYPE_HARDWARE); //使用GPU来缓冲
    setLayerType(LAYER_TYPE_SOFTWARE); //使用一个Bitmap来缓冲
```
##### 离屏绘制模式示意图 
Src:矩形(上层)     Dst:圆形(下层)
![image](https://github.com/tianyalu/NePaintXfermode/blob/master/show/xfermode.png)  
解释：  
```android
    //所绘制不会提交到画布上
    new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
    //显示上层绘制的图像（矩形）
    new PorterDuffXfermode(PorterDuff.Mode.SRC),
    //显示下层绘制的图像（圆形）
    new PorterDuffXfermode(PorterDuff.Mode.DST),

    //正常绘制显示，上下层绘制叠盖
    new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),
    //上下层都显示，下层居上显示
    new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
    //取两层绘制交集，显示上层
    new PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
    //取两层绘制交集，显示下层
    new PorterDuffXfermode(PorterDuff.Mode.DST_IN),

    //取上层绘制非交集部分，交集部分变成透明
    new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),
    //取下层绘制非交集部分，交集部分变成透明
    new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
    //取上层交集部分与下层非交集部分
    new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
    //取下层交集部分与上层非交集部分
    new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),

    //去除两图层交集部分
    new PorterDuffXfermode(PorterDuff.Mode.XOR),
    //取两图层全部区域，交集部分颜色加深
    new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
    //取两图层全部区域，交集部分颜色点亮
    new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
    //取两图层交集部分，交集部分颜色叠加
    new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),

    //取两图层全部区域，交集部分滤色
    new PorterDuffXfermode(PorterDuff.Mode.SCREEN),
    //取两图层全部区域，交集部分饱和度相加
    new PorterDuffXfermode(PorterDuff.Mode.ADD),
    //取两图层全部区域，交集部分叠加
    new PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
```
##### 刮刮卡效果示例  
![image](https://github.com/tianyalu/NePaintXfermode/blob/master/show/eraser.gif)

