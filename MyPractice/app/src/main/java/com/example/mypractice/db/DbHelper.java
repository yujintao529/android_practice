package com.example.mypractice.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
public class DbHelper extends SQLiteOpenHelper {
    private String table;
    public DbHelper(Context context,String table) {
        super(context,"my_database",null,1);
        this.table=table;
//        createTable();
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public void createTable(){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.execSQL("create table "+table+"(id integer)");
        sqLiteDatabase.endTransaction();

    }
    public long insert(){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        ContentValues cv=new ContentValues();
        cv.put("id", 1);
        return sqLiteDatabase.insert("msg1",null,cv);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+table+"(id integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
