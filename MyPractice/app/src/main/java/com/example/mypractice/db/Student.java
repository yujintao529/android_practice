package com.example.mypractice.db;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created by yujintao on 2017/3/3.
 */
@Table(value = "student")
public class Student {
    public static final String TAG = Student.class.getSimpleName();
    @Column("id")
    @PrimaryKey(PrimaryKey.AssignType.AUTO_INCREMENT)
    private int id;
    @Column("name")
    private String name;
}
