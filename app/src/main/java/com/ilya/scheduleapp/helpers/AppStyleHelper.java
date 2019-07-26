package com.ilya.scheduleapp.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.ColorUtils;

import com.ilya.scheduleapp.R;

public class AppStyleHelper {
    private static final String COLOR_SCHEME = "default_color";
    private static final String DARK_THEME_TYPE = "dark_theme";

    private enum ColorType {
        WHITE,
        BLACK
    }

    public static void initializeStyle(Context context) {
        int color = context.getResources().getColor(R.color.colorPrimary);

        StorageHelper.addToShared(context, COLOR_SCHEME, color);
        StorageHelper.addToShared(context, DARK_THEME_TYPE, "no");
    }

    public static void restoreMainStyle(Activity activity, Toolbar toolbar) {
        int color = StorageHelper.findIntInShared(activity, COLOR_SCHEME);

        if (color != Integer.MIN_VALUE)
            setStyle(activity, color, toolbar);
        else
            setStyle(activity, activity.getResources().getColor(R.color.colorPrimary), toolbar);
    }

    public static int getDefaultTheme(Activity activity) {
        int color = StorageHelper.findIntInShared(activity, COLOR_SCHEME);

        if (generateContrastColorType(color) == ColorType.BLACK)
            return R.style.AppTheme_DarkActionBar;

        return R.style.AppTheme;
    }

    public static void setDefaultBackground(Activity activity, ActionBar actionBar) {
        int color = StorageHelper.findIntInShared(activity, COLOR_SCHEME);

        if (color != Integer.MIN_VALUE && actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(color));
            setStatusBarColor(activity, color);
        }
    }

    public static void setDefaultStyle(Activity activity, ActionBar actionBar) {
        int color = StorageHelper.findIntInShared(activity, COLOR_SCHEME);

        AppStyleHelper.setStyleDynamically(activity, color, actionBar);
    }

    public static void setStyleDynamically(Activity activity, int color, ActionBar actionBar) {
        if (actionBar == null) return;

        setStatusBarColor(activity, color);
        actionBar.setBackgroundDrawable(new ColorDrawable(color));

        int titleColor = generateContrastColorId(color);

        Spannable text = new SpannableString(actionBar.getTitle());
        text.setSpan(new ForegroundColorSpan(activity.getResources().getColor(titleColor)),
                0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);

        final Drawable upArrow = activity.getResources().
                getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getBackArrowColor(activity, titleColor), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);
    }

    public static void saveColorScheme(Activity activity, int color) {
        StorageHelper.addToShared(activity, COLOR_SCHEME, color);
    }

    private static void setStyle(Activity activity, int color, Toolbar toolbar) {
        if (toolbar == null) return;

        toolbar.setBackgroundColor(color);
        setStatusBarColor(activity, color);

        if (generateContrastColorType(color) == ColorType.BLACK)
            toolbar.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Light);
        else
            toolbar.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Dark_ActionBar);
    }

    private static ColorType generateContrastColorType(int color) {
        double luminance = ColorUtils.calculateLuminance(color);

        if (luminance > 0.5)
            return ColorType.BLACK;
        else
            return ColorType.WHITE;
    }

    private static int generateContrastColorId(int color) {
        double luminance = ColorUtils.calculateLuminance(color);

        if (luminance > 0.5)
            return android.R.color.black;
        else
            return android.R.color.white;
    }

    private static int generateDarkenColor(int color) {
        float[] hsv = new float[3];

        Color.colorToHSV(color, hsv);
       // hsv[2] *= 0.8f;

        return Color.HSVToColor(hsv);
    }

    private static int getBackArrowColor(Activity activity, int textColor) {
        if(textColor == android.R.color.black)
            return activity.getResources().getColor(R.color.gray);

        return activity.getResources().getColor(textColor);
    }

    private static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}
