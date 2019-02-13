package com.cd.xq.module.util.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by Administrator on 2019/1/12.
 */

/**
 * Glide 的图形变换可参考Glide 示例源码
 * {@link com.bumptech.glide.load.resource.bitmap.CenterCrop}
 * {@link com.bumptech.glide.load.resource.bitmap.BitmapTransformation}
 *
 */

public class GlideCircleBorderTransform extends BitmapTransformation {
    private final String ID = getClass().getName();
    private Paint mBorderPaint;
    private float borderWidth;
    private int borderColor;

    public GlideCircleBorderTransform(Context context,float borderWidth, int borderColor) {
        super(context);
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
        mBorderPaint = new Paint();
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(borderWidth);
        mBorderPaint.setDither(true);
    }

    private Bitmap circleCrop(BitmapPool bitmapPool, Bitmap source) {

        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap square = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap result = bitmapPool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        //画图
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        //设置 Shader
        paint.setShader(new BitmapShader(square, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float radius = size / 2f;
        //绘制一个圆
        canvas.drawCircle(radius, radius, radius, paint);


        /************************描边*********************/
        //注意：避免出现描边被屏幕边缘裁掉
        float borderRadius = radius - (borderWidth / 2);
        //画边框
        canvas.drawCircle(radius, radius, borderRadius, mBorderPaint);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GlideCircleBorderTransform;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    @Override
    public String getId() {
        return ID;
    }
}