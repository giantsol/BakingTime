package com.lee.hansol.bakingtime.helpers;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.WindowManager;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.fragments.RenewableFragment;
import com.lee.hansol.bakingtime.utils.WindowUtils;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class AnimationHelper {
    private final Activity activity;

    private Animator fadeIn;
    private Animator fadeOut;
    private Animator slideLeftEnter;
    private Animator slideLeftExit;
    private Animator slideRightEnter;
    private Animator slideRightExit;

    public AnimationHelper(Activity activity) {
        this.activity = activity;
        fadeIn = AnimatorInflater.loadAnimator(activity, R.animator.fragment_fade_in);
        fadeOut = AnimatorInflater.loadAnimator(activity, R.animator.fragment_fade_out);
        slideLeftEnter = AnimatorInflater.loadAnimator(activity, R.animator.fragment_slide_left_enter);
        slideLeftExit = AnimatorInflater.loadAnimator(activity, R.animator.fragment_slide_left_exit);
        slideRightEnter = AnimatorInflater.loadAnimator(activity, R.animator.fragment_slide_right_enter);
        slideRightExit = AnimatorInflater.loadAnimator(activity, R.animator.fragment_slide_right_exit);
    }

    public void fadeOutRenewFadeIn(final RenewableFragment target) {
        WindowUtils.setUntouchable(activity);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                target.renew();
                animation.removeAllListeners();
                fadeIn(target);
            }
        });
        fadeOut.setTarget(target.getView());
        fadeOut.start();
    }

    private void fadeIn(final RenewableFragment target) {
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                WindowUtils.clearUntouchable(activity);
                animation.removeAllListeners();
            }
        });
        fadeIn.setTarget(target.getView());
        fadeIn.start();
    }

    public void slideLeftExitRenewSlideRightEnter(final RenewableFragment target) {
        WindowUtils.setUntouchable(activity);
        slideLeftExit.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                target.renew();
                animation.removeAllListeners();
                slideRightEnter(target);
            }
        });
        slideLeftExit.setTarget(target.getView());
        slideLeftExit.start();
    }

    private void slideRightEnter(final RenewableFragment target) {
        slideRightEnter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                WindowUtils.clearUntouchable(activity);
                animation.removeAllListeners();
            }
        });
        slideRightEnter.setTarget(target.getView());
        slideRightEnter.start();
    }

    public void slideRightExitRenewSlideRightEnter(final RenewableFragment target) {
        WindowUtils.setUntouchable(activity);
        slideRightExit.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                target.renew();
                animation.removeAllListeners();
                slideRightEnter(target);
            }
        });
        slideRightExit.setTarget(target.getView());
        slideRightExit.start();
    }

    public void slideLeftExitRenewSlideLeftEnter(final RenewableFragment target) {
        WindowUtils.setUntouchable(activity);
        slideLeftExit.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                target.renew();
                animation.removeAllListeners();
                slideLeftEnter(target);
            }
        });
        slideLeftExit.setTarget(target.getView());
        slideLeftExit.start();
    }

    private void slideLeftEnter(final RenewableFragment target) {
        slideLeftEnter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                WindowUtils.clearUntouchable(activity);
                animation.removeAllListeners();
            }
        });
        slideLeftEnter.setTarget(target.getView());
        slideLeftEnter.start();
    }

}
