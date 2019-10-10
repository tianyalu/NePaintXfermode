package com.sty.ne.paint.xfermode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by tian on 2019/10/9.
 */

public class XfermodesView extends View {
    private static int W = 250;
    private static int H = 250;

    private static final int ROW_MAX = 4; //number of samples per row

    private Bitmap mSrcB; //矩形
    private Bitmap mDstB; //圆形
    private Shader mBG; //background checker-board pattern

    //其中Sa全部称为Source alpha，表示源图的Alpha通道；Sc全称为Source color,表示源图的颜色；
    //Da全称为destination alpha,表示目标图的alpha通道；Dc全称为destination color,表示目标图的颜色；
    //[...,...]前半部分计算的是结果图像的Alpha通道值，“，”后半部分计算的是结果图像的颜色值。
    //效果作用于src的原图像区域
    private static final Xfermode[] sModes = {
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
    };

    private static final String[] sLabels = {
            "Clear", "Src", "Dst", "SrcOver", "DstOver", "SrcIn", "DstIn", "SrcOut", "DstOut",
            "SrcAtop", "DstATop", "Xor", "Darken", "Lighten", "Multiply", "Screen", "Add", "Overlay"
    };

    public XfermodesView(Context context) {
        this(context, null);
    }

    public XfermodesView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XfermodesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if(windowManager != null) {
            DisplayMetrics display = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(display);
            W = H = (display.widthPixels - 64) / ROW_MAX; //得到矩形
        }

        //1. API 14后 有些函数不支持硬件加速，需要禁用
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mSrcB = makeSrc(W, H);
        mDstB = makeDst(W, H);

        //根据width和height创建空位图，然后用指定的颜色数组colors来从左到右从上至下一次填充颜色
        // make a checkerboard pattern
        Bitmap bitmap = Bitmap.createBitmap(new int[]{0xFFFFFFFF, 0xFFCCCCCC, 0xFFCCCCCC, 0xFFFFFFFF},
                2, 2, Bitmap.Config.RGB_565);
        //BitmapShader的作用是使用特定的图片来作为纹理来使用
        mBG = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        Matrix matrix = new Matrix();
        // 设置Matrix进行缩放，sx,sy控制X,Y方向上的缩放比例
        matrix.setScale(6, 6);
        mBG.setLocalMatrix(matrix);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        Paint labelP = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelP.setTextAlign(Paint.Align.CENTER);

        Paint paint = new Paint();
        paint.setFilterBitmap(false);

        canvas.translate(15, 65);

        int x = 0;
        int y = 0;
        for (int i = 0; i < sModes.length; i++) {
            //draw the border
            paint.setStyle(Paint.Style.STROKE);
            paint.setShader(null);
            canvas.drawRect(x - 0.5f, y - 0.5f, x + W + 0.5f, y + H + 0.5f, paint);

            //draw the checker-board pattern
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(mBG);
            canvas.drawRect(x, y, x + W, y + H, paint);

            //使用离屏绘制
            int layerId = canvas.saveLayer(x, y, x + W, y + H, null);
            canvas.translate(x, y);
            canvas.drawBitmap(makeDst(2 * W / 3, 2 * H / 3), 0, 0, paint);
            paint.setXfermode(sModes[i]);
            canvas.drawBitmap(makeSrc(2 * W / 3, 2 * H / 3), W / 3, H / 3, paint);
            paint.setXfermode(null);
            canvas.restoreToCount(layerId);

            // draw the label
            labelP.setTextSize(32);
            canvas.drawText(sLabels[i], x + W / 2, y - labelP.getTextSize() / 2, labelP); //因为是居中的，所以这个点表示文字中的baseline中心点

            x += W + 10;

            //wrap around when we've drawn enough for one row
            if((i % ROW_MAX) == ROW_MAX - 1) {
                x = 0;
                y += H + 60;
            }
        }
    }

    // create a bitmap with a circle, used for the "dst" image
    // 画一个完成的圆
    static Bitmap makeDst(int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(0xFFFFCC44);
        canvas.drawOval(new RectF(0, 0, w, h), paint);

        return bitmap;
    }

    // create a bitmap with a rect, used for the "src" image
    // 画一个完成的矩形
    static Bitmap makeSrc(int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(0xFF66AAFF);
        canvas.drawRect(0, 0, w * 19 / 20, h * 19 / 20, paint);

        return bitmap;
    }
}
