package com.sty.ne.paint.xfermode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        setContentView(new XfermodeView(this));  //离屏绘制演示示例
        setContentView(new XfermodesView(this));  //离屏绘制Mode demo
//        setContentView(new XfermodeEraserView(this)); //刮刮卡效果
    }
}

