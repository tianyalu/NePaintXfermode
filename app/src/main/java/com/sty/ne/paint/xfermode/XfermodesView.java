package com.sty.ne.paint.xfermode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

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
            new PorterDuffXfermode(PorterDuff.Mode.CLEAR),

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
            //取两图层全部区域，交集部分颜色叠加
            new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),

            //取两图层全部区域，交集部分滤色
            new PorterDuffXfermode(PorterDuff.Mode.SCREEN),
            //取两图层全部区域，交集部分饱和度相加
            new PorterDuffXfermode(PorterDuff.Mode.ADD),
            //取两图层全部区域，交集部分叠加
            new PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
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
    }
}
