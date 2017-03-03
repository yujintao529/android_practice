package com.example.mypractice.db;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mypractice.Logger;
import com.example.mypractice.R;

/**
 * <p>Copyright: Copyright (c) 2016</p>
 * <p/>
 * <p>Company: 浙江齐聚科技有限公司<a href="www.guagua.cn">www.guagua.cn</a></p>
 *
 * @author yujintao
 * @version 1.0.0
 * @description
 * @modify
 */
public class SQLiteAct extends Activity implements View.OnClickListener {
    Button mButton;
    DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        mButton= (Button) findViewById(R.id.create);
        mButton.setOnClickListener(this);
        dbHelper=new DbHelper(this,"Student");


    }

    @Override
    public void onClick(View view) {
//      dbHelper.createTable();
        dbHelper=new DbHelper(this,"teacher");
//        long id=dbHelper.insert();
//        Logger.d("insert data "+id);
    }
}
