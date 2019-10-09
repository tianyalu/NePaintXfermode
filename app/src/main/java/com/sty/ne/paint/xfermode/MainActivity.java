package com.sty.ne.paint.xfermode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        setContentView(new XfermodeView(this));
        setContentView(new XfermodeEraserView(this)); //刮刮卡效果
    }
}

