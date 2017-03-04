package com.example.mypractice.db;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mypractice.Logger;
import com.example.mypractice.R;
import com.example.mypractice.YUApplication;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.impl.SQLiteHelper;

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

    @BindView(R.id.create_table)
    Button b1;

    @BindView(R.id.update_table)
    Button b2;
    static LiteOrm liteOrm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        ButterKnife.bind(this);
        SQLiteOpenHelper dbHelper=DbHelper.getInstance(this);
        if(liteOrm==null) {
            liteOrm = LiteOrm.newSingleInstance(getApplicationContext(), dbHelper.getDatabaseName());
        }
    }

    @Override
    @OnClick(R.id.create)
    public void onClick(View view) {
        liteOrm.insert(new Student());
    }

    @OnClick(R.id.create_table)
    public void createTable(){
        SQLiteOpenHelper dbHelper=DbHelper.getInstance(this);
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        String creataTable="create table if not exists teacher (id integer primary key autoincrement)";
        SQLiteStatement sqLiteStatement=sqLiteDatabase.compileStatement(creataTable);
        sqLiteStatement.execute();
        sqLiteStatement.close();
    }

    @OnClick(R.id.update_table)
    public void updateTable(){
        SQLiteOpenHelper dbHelper=DbHelper.getInstance(this);
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        String updateTable="alter table teacher add column name text";
        SQLiteStatement sqLiteStatement=sqLiteDatabase.compileStatement(updateTable);
        sqLiteStatement.execute();
        sqLiteStatement.close();
    }
}
