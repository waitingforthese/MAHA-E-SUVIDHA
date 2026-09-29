package rahul.jagtap.dmas.widget.areditor.strategies.defaults;

import android.content.Context;
import android.content.Intent;
import android.text.style.URLSpan;

import rahul.jagtap.dmas.widget.areditor.spans.AreAtSpan;
import rahul.jagtap.dmas.widget.areditor.spans.AreImageSpan;
import rahul.jagtap.dmas.widget.areditor.spans.AreVideoSpan;
import rahul.jagtap.dmas.widget.areditor.strategies.AreClickStrategy;

import rahul.jagtap.dmas.widget.areditor.Util;

/**
 * Created by wliu on 30/06/2018.
 */

public class DefaultClickStrategy implements AreClickStrategy {
    @Override
    public boolean onClickAt(Context context, AreAtSpan atSpan) {
        Intent intent = new Intent();
        intent.setClass(context, DefaultProfileActivity.class);
        intent.putExtra("userKey", atSpan.getUserKey());
        intent.putExtra("userName", atSpan.getUserName());
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean onClickImage(Context context, AreImageSpan imageSpan) {
        Intent intent = new Intent();
        AreImageSpan.ImageType imageType = imageSpan.getImageType();
        intent.putExtra("imageType", imageType);
        if (imageType == AreImageSpan.ImageType.URI) {
            intent.putExtra("uri", imageSpan.getUri());
        } else if (imageType == AreImageSpan.ImageType.URL) {
            intent.putExtra("url", imageSpan.getURL());
        } else {
            intent.putExtra("resId", imageSpan.getResId());
        }
        intent.setClass(context, DefaultImagePreviewActivity.class);
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean onClickVideo(Context context, AreVideoSpan videoSpan) {
        Util.toast(context, "Video span");
        return true;
    }

    @Override
    public boolean onClickUrl(Context context, URLSpan urlSpan) {
        // Use default behavior
        return false;
    }
}
