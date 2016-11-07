package com.example.mypractice.other;

import com.example.Factory;

/**
 * Created by yujintao on 16/9/28.
 */


@Factory(id = "baxi",type = Meal.class)
public class BaxiMeal implements Meal {
    private String price="11";
    @Override
    public String getPrice() {
        return price;
    }
}
