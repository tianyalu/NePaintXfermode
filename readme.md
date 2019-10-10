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
![image](https://github.com/tianyalu/NePaintXfermode/blob/master/show/xfermode.png)
##### 刮刮卡效果示例  
![image](https://github.com/tianyalu/NePaintXfermode/blob/master/show/eraser.gif)

