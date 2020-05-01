package com.example.mango;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class Countdownview extends View {
    private Context context;
    public Countdownview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    Paint paint;
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

//        实例化画笔对象
        paint = new Paint();
//        给画笔设置颜色
        paint.setColor(Color.BLUE);
//        设置画笔属性
        paint.setStyle(Paint.Style.FILL);//画笔属性是实心圆
//        paint.setStyle(Paint.Style.STROKE);//画笔属性是空心圆
        paint.setStrokeWidth(1);//设置画笔粗细

        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.colorMain));
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(1);
        canvas.drawCircle(getWidth() / 2, getHeight(), dip2px(context,125), paint);

        if(time >= 0 && time <= max) {
            Paint p1 = new Paint();
            p1.setColor(Color.rgb(255, 97, 0));
            p1.setStyle(Paint.Style.FILL);
            p1.setStrokeWidth(1);

            canvas.drawArc(new RectF(0,0,dip2px(context,250),dip2px(context,250)),0, 360 - time * cout,true, p1);
        }

        canvas.drawCircle(getWidth() / 2, getHeight(), dip2px(context,75), p);
    }

    int   time  = 0;
    float cout  = 0;
    float max = 0;
    public void setCout(int cout) {
        max = cout;
        this.cout = 180.0f / cout;
    }
    public void setTime(int time) {
        this.time = time;
        if (time > 0) {
            try {
                post(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
