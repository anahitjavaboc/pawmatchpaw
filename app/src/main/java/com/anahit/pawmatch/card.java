package com.anahit.pawmatch;

import android.graphics.drawable.Drawable;

public class card {
    String content;
    Drawable image;

    public card(String content, Drawable image) {
        this.content = content;
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public Drawable getImage() {
        return image;
    }
}
