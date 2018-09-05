package base.gan.photopicker.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/5/24
 */
public class AnimationUtil {
    /**
     * 为第一个页面准备的从左到右的动画
     *
     * @return
     */
    public static Animation  getTranslateAnimationLeft2Right1(Animation.AnimationListener listener) {
        //实例化TranslateAnimation
        //以自身为坐标系和长度单位，从(0,0)移动到(1,1)
        TranslateAnimation animation = new
                TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        //设置动画插值器 被用来修饰动画效果,定义动画的变化率
        animation.setInterpolator(new DecelerateInterpolator());
        //设置动画执行时间
        animation.setDuration(500);
        animation.setAnimationListener(listener);
        return animation;
    }

    /**
     * 为第一个页面准备的从左到右的动画
     *
     * @return
     */
    public static Animation getTranslateAnimationRight2left1(Animation.AnimationListener listener) {
        //实例化TranslateAnimation
        //以自身为坐标系和长度单位，从(0,0)移动到(1,1)
        TranslateAnimation animation = new
                TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        //设置动画插值器 被用来修饰动画效果,定义动画的变化率
        animation.setInterpolator(new DecelerateInterpolator());
        //设置动画执行时间
        animation.setDuration(500);
        animation.setAnimationListener(listener);
        return animation;
    }

    /**
     * 为第二个页面准备的从左到右的动画
     *
     * @return
     */
    public static Animation getTranslateAnimationLeft2Right2() {
        //实例化TranslateAnimation
        //以自身为坐标系和长度单位，从(0,0)移动到(1,1)
        TranslateAnimation animation = new
                TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        //设置动画插值器 被用来修饰动画效果,定义动画的变化率
        animation.setInterpolator(new DecelerateInterpolator());
        //设置动画执行时间
        animation.setDuration(500);
        return animation;
    }

    /**
     * 为第二个页面准备的从左到右的动画
     *
     * @return
     */
    public static Animation getTranslateAnimationRight2left2() {
        //实例化TranslateAnimation
        //以自身为坐标系和长度单位，从(0,0)移动到(1,1)
        TranslateAnimation animation = new
                TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        //设置动画插值器 被用来修饰动画效果,定义动画的变化率
        animation.setInterpolator(new DecelerateInterpolator());
        //设置动画执行时间
        animation.setDuration(500);
        return animation;
    }
}
