package com.demon.yu.avatar.interact;

import android.graphics.PointF;
import android.view.animation.Interpolator;

public class CubicBezierInterpolator implements Interpolator {
    protected PointF start;
    protected PointF end;
    protected PointF a = new PointF();
    protected PointF b = new PointF();
    protected PointF c = new PointF();


    public CubicBezierInterpolator(PointF start, PointF end) throws IllegalArgumentException {
        if (start.x < 0 || start.x > 1)
            throw new IllegalArgumentException("startX value must be in the range [0, 1]");
        if (end.x < 0 || end.x > 1)
            throw new IllegalArgumentException("endX value must be in the range [0, 1]");
        this.start = start;
        this.end = end;
    }

    public CubicBezierInterpolator(float startX, float startY, float endX, float endY) {
        this(new PointF(startX, startY), new PointF(endX, endY));
    }

    public CubicBezierInterpolator(int curve) {
        this(getCurveParameters(curve)[0], getCurveParameters(curve)[1], getCurveParameters(curve)[2], getCurveParameters(curve)[3]);
    }

    @Override
    public float getInterpolation(float time) {
        return getBezierCoordinateY(getXForTime(time));
    }

    protected float getBezierCoordinateY(float time) {
        c.y = 3 * start.y;
        b.y = 3 * (end.y - start.y) - c.y;
        a.y = 1 - c.y - b.y;
        return time * (c.y + time * (b.y + time * a.y));
    }

    protected float getXForTime(float time) {
        float x = time;
        float z;
        for (int i = 1; i < 14; i++) {
            z = getBezierCoordinateX(x) - time;
            if (Math.abs(z) < 1e-3) break;
            x -= z / getXDerivate(x);
        }
        return x;
    }

    protected static float[] getCurveParameters(int curve) {
        switch (curve) {
            case LINEAR:
                return new float[]{0f, 0f, 1f, 1f};

            case SINEEASEIN:
                return new float[]{0.47f, 0f, 0.745f, 0.715f};
            case SINEEASEOUT:
                return new float[]{0.39f, 0.575f, 0.565f, 1f};
            case SINEEASEINOUT:
                return new float[]{0.445f, 0.05f, 0.55f, 0.95f};

            case QUADEASEIN:
                return new float[]{0.26f, 0f, 0.6f, 0.2f};
            case QUADEASEOUT:
                return new float[]{0.4f, 0.8f, 0.74f, 1f};
            case QUADEASEINOUT:
                return new float[]{0.48f, 0.04f, 0.52f, 0.96f};

            case CUBICEASEIN:
                return new float[]{0.4f, 0f, 0.68f, 0.06f};
            case CUBICEASEOUT:
                return new float[]{0.32f, 0.94f, 0.6f, 1f};
            case CUBICEASEINOUT:
                return new float[]{0.66f, 0f, 0.34f, 1f};

            case QUARTEASEIN:
                return new float[]{0.52f, 0f, 0.74f, 0f};
            case QUARTEASEOUT:
                return new float[]{0.26f, 1f, 0.48f, 1f};
            case QUARTEASEINOUT:
                return new float[]{0.76f, 0f, 0.24f, 1f};

            case QUINTEASEIN:
                return new float[]{0.64f, 0f, 0.78f, 0f};
            case QUINTEASEOUT:
                return new float[]{0.22f, 1f, 0.36f, 1f};
            case QUINTEASEINOUT:
                return new float[]{0.84f, 0f, 0.16f, 1f};

            case EXPOEASEIN:
                return new float[]{0.66f, 0f, 0.86f, 0f};
            case EXPOEASEOUT:
                return new float[]{0.14f, 1f, 0.34f, 1f};
            case EXPOEASEINOUT:
                return new float[]{0.9f, 0f, 0.1f, 1f};

            case EASE:
                return new float[]{0.25f, 0.01f, 0.25f, 1f};

            case EASINGIN:
                return new float[]{0.33f, 0f, 0.67f, 1f};

            default:
            case STANDARDINOUT:
                return new float[]{0.15f, 0.12f, 0f, 1f};

        }
    }

    private float getXDerivate(float t) {
        return c.x + t * (2 * b.x + 3 * a.x * t);
    }

    private float getBezierCoordinateX(float time) {
        c.x = 3 * start.x;
        b.x = 3 * (end.x - start.x) - c.x;
        a.x = 1 - c.x - b.x;
        return time * (c.x + time * (b.x + time * a.x));
    }

    public static final int LINEAR = 0;

    public static final int SINEEASEIN = 1;
    public static final int SINEEASEOUT = 2;
    public static final int SINEEASEINOUT = 3;

    public static final int QUADEASEIN = 4;
    public static final int QUADEASEOUT = 5;
    public static final int QUADEASEINOUT = 6;

    public static final int CUBICEASEIN = 7;
    public static final int CUBICEASEOUT = 8;
    public static final int CUBICEASEINOUT = 9;

    public static final int QUARTEASEIN = 10;
    public static final int QUARTEASEOUT = 11;
    public static final int QUARTEASEINOUT = 12;

    public static final int QUINTEASEIN = 13;
    public static final int QUINTEASEOUT = 14;
    public static final int QUINTEASEINOUT = 15;

    public static final int EXPOEASEIN = 16;
    public static final int EXPOEASEOUT = 17;
    public static final int EXPOEASEINOUT = 18;

    public static final int STANDARDINOUT = 19;
    public static final int EASE = 20;
    public static final int EASINGIN = 21;
}