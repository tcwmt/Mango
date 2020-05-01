package com.example.mango;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Uimanagement {
    private AppCompatActivity activity;
    public View view_start_show;
    public ConstraintLayout view_clmainview;

    public View tev_m1_time;

    public Countdownview countdownview;
    public Menuview menuview;

    public Uimanagement(AppCompatActivity appCompatActivity) {
        activity = appCompatActivity;

        view_clmainview = activity.findViewById(R.id.ll_main);
        view_start_show = activity.getLayoutInflater().inflate(
                R.layout.layout_start_show,
                view_clmainview,
                false
        );

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    view_clmainview.addView(view_start_show);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view_start_show,"alpha",1f,0f);
                            objectAnimator.setDuration(5000);
                            objectAnimator.start();
                            objectAnimator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    view_clmainview.removeView(view_start_show);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
