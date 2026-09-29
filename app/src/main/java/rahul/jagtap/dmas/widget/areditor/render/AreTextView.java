package rahul.jagtap.dmas.widget.areditor.render;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import rahul.jagtap.dmas.R;
import rahul.jagtap.dmas.widget.areditor.Constants;
import rahul.jagtap.dmas.widget.areditor.Util;
import rahul.jagtap.dmas.widget.areditor.events.AREMovementMethod;
import rahul.jagtap.dmas.widget.areditor.inner.Html;
import rahul.jagtap.dmas.widget.areditor.strategies.AreClickStrategy;
import rahul.jagtap.dmas.widget.areditor.strategies.defaults.DefaultClickStrategy;

import java.util.HashMap;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/5/20
 * @discription null
 * @usage null
 */
public class AreTextView extends AppCompatTextView {

    private static HashMap<String, Spanned> spannedHashMap = new HashMap<>();

    private AreClickStrategy mClickStrategy;

    Context mContext;

//    private static final int TRIM_MODE_LINES = 0;
//    private static final int TRIM_MODE_LENGTH = 1;
//    private static final int DEFAULT_TRIM_LENGTH = 240;
//    private static final int DEFAULT_TRIM_LINES = 2;
//    private static final int INVALID_END_INDEX = -1;
//    private static final boolean DEFAULT_SHOW_TRIM_EXPANDED_TEXT = true;
//    private static final String ELLIPSIZE = "... ";
//
//    private CharSequence text;
//    private BufferType bufferType;
//    private boolean readMore = true;
//    private int trimLength;
//    private CharSequence trimCollapsedText;
//    private CharSequence trimExpandedText;
//    private ReadMoreClickableSpan viewMoreSpan;
//    private int colorClickableText;
//    private boolean showTrimExpandedText;
//
//    private int trimMode;
//    private int lineEndIndex;
//    private int trimLines;

    public AreTextView(Context context) {
        this(context, null);
    }

    public AreTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AreTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.DEFAULT_FONT_SIZE);
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ReadMoreTextView);
//        this.trimLength = typedArray.getInt(R.styleable.ReadMoreTextView_trimLength, DEFAULT_TRIM_LENGTH);
//        int resourceIdTrimCollapsedText =
//                typedArray.getResourceId(R.styleable.ReadMoreTextView_trimCollapsedText, R.string.read_more);
//        int resourceIdTrimExpandedText =
//                typedArray.getResourceId(R.styleable.ReadMoreTextView_trimExpandedText, R.string.read_less);
//        this.trimCollapsedText = getResources().getString(resourceIdTrimCollapsedText);
//        this.trimExpandedText = getResources().getString(resourceIdTrimExpandedText);
//        this.trimLines = typedArray.getInt(R.styleable.ReadMoreTextView_trimLines, DEFAULT_TRIM_LINES);
//        this.colorClickableText = typedArray.getColor(R.styleable.ReadMoreTextView_colorClickableText,
//                ContextCompat.getColor(context, R.color.red));
//        this.showTrimExpandedText =
//                typedArray.getBoolean(R.styleable.ReadMoreTextView_showTrimExpandedText, DEFAULT_SHOW_TRIM_EXPANDED_TEXT);
//        this.trimMode = typedArray.getInt(R.styleable.ReadMoreTextView_trimMode, TRIM_MODE_LINES);
//        typedArray.recycle();
//        viewMoreSpan = new ReadMoreClickableSpan();
//        onGlobalLayoutLineEndIndex();
//        setText();
        initGlobalValues();
        initMovementMethod();
    }

    private void initGlobalValues() {
        int[] wh = Util.getScreenWidthAndHeight(mContext);
        Constants.SCREEN_WIDTH = wh[0];
        Constants.SCREEN_HEIGHT = wh[1];
    }

    private void initMovementMethod() {
        if (this.mClickStrategy == null) {
            this.mClickStrategy = new DefaultClickStrategy();
        }
        this.setMovementMethod(new AREMovementMethod(this.mClickStrategy));
    }

    public void fromHtml(String html) {
        Spanned spanned = getSpanned(html);
        setText(spanned);
    }

    private Spanned getSpanned(String html) {
        Html.sContext = mContext;
        Html.ImageGetter imageGetter = new AreImageGetter(mContext, this);
        Html.TagHandler tagHandler = new AreTagHandler();
        return Html.fromHtml(html, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH, imageGetter, tagHandler);
    }

    /**
     * Use cache will take more RAM, you need to call clear cache when you think it is safe to do that.
     * You may need cache when working with {@link android.widget.ListView} or RecyclerView
     *
     * @param html
     */
    public void fromHtmlWithCache(String html) {
        Spanned spanned = null;
        if (spannedHashMap.containsKey(html)) {
            spanned = spannedHashMap.get(html);
        }
        if (spanned == null) {
            spanned = getSpanned(html);
            spannedHashMap.put(html, spanned);
        }
        if (spanned != null) {
            setText(spanned);
        }
    }

    public static void clearCache() {
        spannedHashMap.clear();
    }

    public void setClickStrategy(AreClickStrategy clickStrategy) {
        this.mClickStrategy = clickStrategy;
        this.setMovementMethod(new AREMovementMethod(this.mClickStrategy));
    }


////////// Read More TextView Code Starts here.......
//    private void setText() {
//        super.setText(getDisplayableText(), bufferType);
//        setMovementMethod(LinkMovementMethod.getInstance());
//        setHighlightColor(Color.TRANSPARENT);
//    }
//
//    private CharSequence getDisplayableText() {
//        return getTrimmedText(text);
//    }
//
//    @Override
//    public void setText(CharSequence text, BufferType type) {
//        this.text = text;
//        bufferType = type;
//        setText();
//    }
//
//    private CharSequence getTrimmedText(CharSequence text) {
//        if (trimMode == TRIM_MODE_LENGTH) {
//            if (text != null && text.length() > trimLength) {
//                if (readMore) {
//                    return updateCollapsedText();
//                } else {
//                    return updateExpandedText();
//                }
//            }
//        }
//        if (trimMode == TRIM_MODE_LINES) {
//            if (text != null && lineEndIndex > 0) {
//                if (readMore) {
//                    if (getLayout().getLineCount() > trimLines) {
//                        return updateCollapsedText();
//                    }
//                } else {
//                    return updateExpandedText();
//                }
//            }
//        }
//        return text;
//    }
//
//    private CharSequence updateCollapsedText() {
//        int trimEndIndex = text.length();
//        switch (trimMode) {
//            case TRIM_MODE_LINES:
//                trimEndIndex = lineEndIndex - (ELLIPSIZE.length() + trimCollapsedText.length() + 1);
//                if (trimEndIndex < 0) {
//                    trimEndIndex = trimLength + 1;
//                }
//                break;
//            case TRIM_MODE_LENGTH:
//                trimEndIndex = trimLength + 1;
//                break;
//        }
//        // Apply custom text size to trimCollapsedText
//        int customTextSizeInPx = 50; // Specify the text size in pixels
//        SpannableString spannableTrimCollapsedText = new SpannableString(trimCollapsedText);
//        spannableTrimCollapsedText.setSpan(new AbsoluteSizeSpan(customTextSizeInPx), 0, trimCollapsedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        SpannableStringBuilder s = new SpannableStringBuilder(text, 0, trimEndIndex)
//                .append(ELLIPSIZE)
//                .append(spannableTrimCollapsedText);
//        return addClickableSpan(s, spannableTrimCollapsedText);
//    }
//
//    private CharSequence updateExpandedText() {
//        if (showTrimExpandedText) {
//            // Apply custom text size to trimCollapsedText
//            int customTextSizeInPx = 50; // Specify the text size in pixels
//            SpannableString spannableTrimExpandedText = new SpannableString(trimExpandedText);
//            spannableTrimExpandedText.setSpan(new AbsoluteSizeSpan(customTextSizeInPx), 0, trimExpandedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            SpannableStringBuilder s = new SpannableStringBuilder(text, 0, text.length()).append(spannableTrimExpandedText);
//            return addClickableSpan(s, spannableTrimExpandedText);
//        }
//        return text;
//    }
//
//    private CharSequence addClickableSpan(SpannableStringBuilder s, CharSequence trimText) {
//        s.setSpan(viewMoreSpan, s.length() - trimText.length(), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return s;
//    }
//
//    public void setTrimLength(int trimLength) {
//        this.trimLength = trimLength;
//        setText();
//    }
//
//    public void setColorClickableText(int colorClickableText) {
//        this.colorClickableText = colorClickableText;
//    }
//
//    public void setTrimCollapsedText(CharSequence trimCollapsedText) {
//        this.trimCollapsedText = trimCollapsedText;
//    }
//
//    public void setTrimExpandedText(CharSequence trimExpandedText) {
//        this.trimExpandedText = trimExpandedText;
//    }
//
//    public void setTrimMode(int trimMode) {
//        this.trimMode = trimMode;
//    }
//
//    public void setTrimLines(int trimLines) {
//        this.trimLines = trimLines;
//    }
//
//    private class ReadMoreClickableSpan extends ClickableSpan {
//        @Override
//        public void onClick(View widget) {
//            readMore = !readMore;
//            setText();
//        }
//
//        @Override
//        public void updateDrawState(TextPaint ds) {
//            ds.setColor(colorClickableText);
//        }
//    }
//
//    private void onGlobalLayoutLineEndIndex() {
//        if (trimMode == TRIM_MODE_LINES) {
//            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    ViewTreeObserver obs = getViewTreeObserver();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        obs.removeOnGlobalLayoutListener(this);
//                    } else {
//                        obs.removeGlobalOnLayoutListener(this);
//                    }
//                    refreshLineEndIndex();
//                    setText();
//                }
//            });
//        }
//    }
//
//    private void refreshLineEndIndex() {
//        try {
//            if (trimLines == 0) {
//                lineEndIndex = getLayout().getLineEnd(0);
//            } else if (trimLines > 0 && getLineCount() >= trimLines) {
//                lineEndIndex = getLayout().getLineEnd(trimLines - 1);
//            } else {
//                lineEndIndex = INVALID_END_INDEX;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
