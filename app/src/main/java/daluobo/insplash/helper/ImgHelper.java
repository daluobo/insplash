package daluobo.insplash.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import daluobo.insplash.common.GlideApp;

/**
 * Created by daluobo on 2017/11/12.
 */

public class ImgHelper {

    public static void loadImg(@NonNull Context context, @NonNull ImageView imageView, @NonNull String url) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

    public static void loadImg(@NonNull Context context, @NonNull ImageView imageView, Drawable placeholder, @NonNull String url) {
        GlideApp.with(context)
                .load(url)
                .placeholder(placeholder)
                .into(imageView);
    }

}
