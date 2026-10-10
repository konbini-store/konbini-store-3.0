package io.github.konbini.store3.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import io.github.konbini.store3.R;

public class CustomFontTextView extends TextView {
    private static final Map<String, Typeface> sCache = new HashMap<>();

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);

        String font = a.getString(R.styleable.CustomFontTextView_customFontFamily);
        a.recycle();

        if (font == null) font = "droidsans.ttf"; // default
        Typeface tf = sCache.get(font);
        if (tf == null) {
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/" + font);
            sCache.put(font, tf);
        }
        setTypeface(tf);
    }

}