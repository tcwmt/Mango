package com.example.mango;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class Menuview extends View {
    Context context;
    public Menuview(Context context, @Nullable AttributeSet attrs) {
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

   /*四个参数：
                参数一：圆心的x坐标
                参数二：圆心的y坐标
                参数三：圆的半径
                参数四：定义好的画笔
                */
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, dip2px(context,125), paint);

        Paint pp = new Paint();
        pp.setColor(Color.rgb(255, 97, 0));
        pp.setStyle(Paint.Style.FILL);//画笔属性是实心圆
        pp.setStrokeWidth(1);
        switch (itype) {
            case 1:
                canvas.drawArc(new RectF(0,0,dip2px(context,250),dip2px(context,250)),330.0f,120.0f,true, pp);
                break;
            case 2:
                canvas.drawArc(new RectF(0,0,dip2px(context,250),dip2px(context,250)),90.0f,120.0f,true, pp);
                break;
            case 3:
                canvas.drawArc(new RectF(0,0,dip2px(context,250),dip2px(context,250)),210.0f,120.0f,true, pp);
                break;
        }

        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(3);

        canvas.drawArc(new RectF(0,0,dip2px(context,250),dip2px(context,250)),330.0f,120.0f,true, p);
        canvas.drawArc(new RectF(0,0,dip2px(context,250),dip2px(context,250)),90.0f,120.0f,true, p);
        canvas.drawArc(new RectF(0,0,dip2px(context,250),dip2px(context,250)),210.0f,120.0f,true, p);
    }

    private int itype = 0;
    public void setType(int i) {
        itype = i;
        post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
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
