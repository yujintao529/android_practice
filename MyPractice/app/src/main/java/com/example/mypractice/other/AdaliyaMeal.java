package com.example.mypractice.other;

import com.example.Factory;

/**
 * Created by yujintao on 16/9/28.
 */

@Factory(id = "adaliya",type = Meal.class)
public class AdaliyaMeal implements Meal {
    private String price="22";
    @Override
    public String getPrice() {
        return price;
    }
}
