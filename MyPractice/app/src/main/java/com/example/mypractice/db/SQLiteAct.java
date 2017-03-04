package com.example.mypractice.db;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mypractice.Logger;
import com.example.mypractice.R;
import com.example.mypractice.YUApplication;
import com.litesuits.orm.LiteOrm;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.create)
    Button mButton;
    DbHelper dbHelper;
    static LiteOrm liteOrm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        ButterKnife.bind(this);
        if(liteOrm==null) {
            liteOrm = LiteOrm.newSingleInstance(getApplicationContext(), "yu_database");
        }
    }

    @Override
    @OnClick(R.id.create)
    public void onClick(View view) {
        liteOrm.insert(new Student());
    }
}
