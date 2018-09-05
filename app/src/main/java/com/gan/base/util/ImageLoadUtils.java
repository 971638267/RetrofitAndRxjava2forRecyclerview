package com.gan.base.util;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/7/30
 */
public class ImageLoadUtils {

    @BindingAdapter("imageUrl")
    public static void loadUrl(ImageView iv ,String url){
        if (iv!=null && !TextUtils.isEmpty(url)) {
            ImageLoader.getInstance().displayImage(url, iv);
        }
    }
}
