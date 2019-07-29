package com.example.mypractice.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import com.example.mypractice.R;

public class GradientDrawableUtils {

    private static int DEFAULT_COLOR = -1;

    private static int[] sGradientDrawableStyleable = new int[]{
            //size
            R.attr.gradientShape,//0
            R.attr.gradientSizeWidth,
            R.attr.gradientSizeHeight,
//            //solid
            R.attr.gradientSolidColor,//solid color
//            //gradient
            R.attr.gradientStartColor,
            R.attr.gradientCenterColor,
            R.attr.gradientEndColor,
            R.attr.gradientAngle,
            R.attr.gradientCenterX,
            R.attr.gradientCenterY,
//            //corner
            R.attr.gradientCornerRadius,
            R.attr.gradientCornerTopLeftRadius,
            R.attr.gradientCornerTopRightRadius,
            R.attr.gradientCornerBottomLeftRadius,
            R.attr.gradientCornerBottomRightRadius,

//            //stroke
            R.attr.gradientStrokeColor,
            R.attr.gradientStrokeWidth
    };

//    /**
//     * NOTE：目前只支持LINEAR_GRADIENT
//     *
//     * @param context
//     * @param attributeSet
//     * @return
//     */
//    public static GradientDrawable getFromAttributeSet(Context context, AttributeSet attributeSet) {
//        GradientDrawable gradientDrawable = new GradientDrawable();
//        Resources resources = context.getResources();
//        gradientDrawable.setDither(true);
//        GradientDrawableProperties gradientDrawableProperties = new GradientDrawableProperties();
//        for (int i = 0, size = attributeSet.getAttributeCount(); i < size; i++) {
//            int result = attributeSet.getAttributeResourceValue(i, -1);
//            Logger.d(attributeSet.getAttributeName(i) + " " + attributeSet.getAttributeValue(i) + " " + result);
//
//
//            switch (attributeSet.getAttributeNameResource(i)) {
//                case R.attr.gradientShape:
//                    gradientDrawableProperties.shape = attributeSet.getAttributeIntValue(i, GradientDrawable.RECTANGLE);
//                    break;
//                case R.attr.gradientSizeWidth:
//                    if(result==-1) {
//                        String value = attributeSet.getAttributeValue(i);
//                    }
//                    break;
//            }
//        }
//        return gradientDrawable;
//    }


    private static class GradientDrawableProperties {
        int shape=0;
        int sizeWidth=0;
        int sizeHeight=0;
        int solidColor=DEFAULT_COLOR;
        int startColor=DEFAULT_COLOR;
        int centerColor=DEFAULT_COLOR;
        int endColor=DEFAULT_COLOR;
        int angle=0;
        float centerX=0f;
        float centerY=0f;
        int radius=0;
        int topLeftRadius=0;
        int topRightRadius=0;
        int bottomLeftRadius=0;
        int bottomRightRadius=0;
        int strokeColor=DEFAULT_COLOR;
        int strokeWidth=0;

    }

    /**
     * NOTE：目前只支持LINEAR_GRADIENT
     * 需要有严格顺序，不利于后面的扩展.可以使用AttributeSet进行读取，但是比较麻烦～
     *
     * @param context
     * @param attributeSet
     * @return GradientDrawable
     */
    public static GradientDrawable createFromAttributeSet(Context context, AttributeSet attributeSet, int[] styleableRes) {
        //size
        GradientDrawableProperties gradientDrawableProperties = new GradientDrawableProperties();
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, styleableRes);
        for (int i = 0, count = typedArray.getIndexCount(); i < count; i++) {
            int id = typedArray.getIndex(i);
            switch (styleableRes[id]) {
                case R.attr.gradientShape:
                    gradientDrawableProperties.shape = typedArray.getInt(id, GradientDrawable.RECTANGLE);
                    break;
                case R.attr.gradientSizeWidth:
                    gradientDrawableProperties.sizeWidth = typedArray.getDimensionPixelSize(id, -1);
                    break;
                case R.attr.gradientSizeHeight:
                    gradientDrawableProperties.sizeHeight = typedArray.getDimensionPixelSize(id, -1);
                    break;
                case R.attr.gradientSolidColor:
                    gradientDrawableProperties.solidColor = typedArray.getColor(id, DEFAULT_COLOR);
                    break;
                case R.attr.gradientStartColor:
                    gradientDrawableProperties.startColor = typedArray.getColor(id, DEFAULT_COLOR);
                    break;
                case R.attr.gradientCenterColor:
                    gradientDrawableProperties.centerColor = typedArray.getColor(id, DEFAULT_COLOR);
                    break;
                case R.attr.gradientEndColor:
                    gradientDrawableProperties.endColor = typedArray.getColor(id, DEFAULT_COLOR);
                    break;
                case R.attr.gradientAngle:
                    gradientDrawableProperties.angle = typedArray.getInt(id, 0);
                    break;
                case R.attr.gradientCenterX:
                    gradientDrawableProperties.centerX = typedArray.getFloat(id, 0);
                    break;
                case R.attr.gradientCenterY:
                    gradientDrawableProperties.centerY = typedArray.getFloat(id, 0);
                    break;
                case R.attr.gradientCornerRadius:
                    gradientDrawableProperties.radius = typedArray.getDimensionPixelSize(id, 0);
                    break;
                case R.attr.gradientCornerTopLeftRadius:
                    gradientDrawableProperties.bottomLeftRadius = typedArray.getDimensionPixelSize(id, 0);
                    break;
                case R.attr.gradientCornerTopRightRadius:
                    gradientDrawableProperties.topRightRadius = typedArray.getDimensionPixelSize(id, 0);
                    break;
                case R.attr.gradientCornerBottomLeftRadius:
                    gradientDrawableProperties.bottomLeftRadius = typedArray.getDimensionPixelSize(id, 0);
                    break;
                case R.attr.gradientCornerBottomRightRadius:
                    gradientDrawableProperties.bottomRightRadius = typedArray.getDimensionPixelSize(id, 0);
                    break;
                case R.attr.gradientStrokeColor:
                    gradientDrawableProperties.strokeColor = typedArray.getColor(id, DEFAULT_COLOR);
                    break;
                case R.attr.gradientStrokeWidth:
                    gradientDrawableProperties.strokeWidth = typedArray.getDimensionPixelSize(id, 0);
                    break;
            }
        }

        GradientDrawable gradientDrawable = convert(gradientDrawableProperties);
        typedArray.recycle();
        return gradientDrawable;

    }


    private static GradientDrawable convert(GradientDrawableProperties gradientDrawableProperties) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setDither(true);
        gradientDrawable.setShape(gradientDrawableProperties.shape);
        gradientDrawable.setSize(gradientDrawableProperties.sizeWidth, gradientDrawableProperties.sizeHeight);
        if (gradientDrawableProperties.solidColor != DEFAULT_COLOR) {
            gradientDrawable.setColor(gradientDrawableProperties.solidColor);
        } else {
            int startColor = gradientDrawableProperties.startColor;
            int centerColor = gradientDrawableProperties.centerColor;
            int endColor = gradientDrawableProperties.endColor;
            float centerX = gradientDrawableProperties.centerX;
            float centerY = gradientDrawableProperties.centerY;
            int[] colors;
            if (centerColor == DEFAULT_COLOR) {
                colors = new int[]{startColor, endColor};
            } else {
                colors = new int[]{startColor, centerColor, endColor};
            }
            gradientDrawable.setOrientation(calOrientation(gradientDrawableProperties.angle));
            gradientDrawable.setColors(colors);
            gradientDrawable.setGradientCenter(centerX, centerY);
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        }
        int radius = gradientDrawableProperties.radius;
        int topLeft = gradientDrawableProperties.topLeftRadius == 0 ? radius : gradientDrawableProperties.topLeftRadius;
        int topRight = gradientDrawableProperties.topRightRadius == 0 ? radius : gradientDrawableProperties.topRightRadius;
        int bottomLeft = gradientDrawableProperties.bottomLeftRadius == 0 ? radius : gradientDrawableProperties.bottomLeftRadius;
        int bottomRight = gradientDrawableProperties.bottomRightRadius == 0 ? radius : gradientDrawableProperties.bottomRightRadius;
        gradientDrawable.setCornerRadius(radius);
        if (radius != topLeft || radius != topRight || radius != bottomLeft || radius != bottomRight) {
            gradientDrawable.setCornerRadii(new float[]{
                    topLeft, topLeft,
                    topRight, topRight,
                    bottomRight, bottomRight,
                    bottomLeft, bottomLeft
            });
        }
        if (gradientDrawableProperties.strokeColor != DEFAULT_COLOR && gradientDrawableProperties.strokeWidth != -1) {
            gradientDrawable.setStroke(gradientDrawableProperties.strokeWidth,gradientDrawableProperties.strokeColor);
        }
        return gradientDrawable;
    }



    private static GradientDrawable.Orientation calOrientation(int angle) {
        angle %= 360;
        if (angle % 45 != 0) {
            throw new IllegalArgumentException("requires 'angle' attribute tobe a multiple of 45");
        }
        GradientDrawable.Orientation orientation = GradientDrawable.Orientation.LEFT_RIGHT;
        switch (angle) {
            case 0:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case 45:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case 90:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case 135:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case 180:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case 225:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            case 270:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            case 315:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
        }
        return orientation;
    }
}
