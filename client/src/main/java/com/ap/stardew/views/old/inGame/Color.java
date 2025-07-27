package com.ap.stardew.views.old.inGame;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.io.Serializable;

@JsonDeserialize(using = ColorDeserializer.class)
public class Color implements Serializable {
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(256, 256, 256);


    private int[] bg = {255, 255, 255};
    @JsonProperty("fg")
    private int[] fg = {255, 255, 255};

    public int[] getBg() {
        return bg;
    }

    public void setBg(int[] bg) {
        this.bg = bg;
    }

    public int[] getFg() {
        return fg;
    }

    public void setFg(int[] fg) {
        this.fg = fg;
    }

    public Color(int[] fg) {
        this.fg = fg;
    }

    public Color(int r, int g, int b) {
        this.fg[0] = r;
        this.fg[1] = g;
        this.fg[2] = b;
    }

    public Color(double r, double g, double b) {
        this.fg[0] = (int) (r * 256);
        this.fg[1] = (int) (g * 256);
        this.fg[2] = (int) (b * 256);

    }

    public boolean equals(Color c2) {
        if (c2 == null) return false;
        return (this.fg[0] == c2.fg[0]) && (this.fg[1] == c2.fg[1]) && (this.fg[2] == c2.fg[2]);
    }
}

class
ColorDeserializer extends JsonDeserializer<Color> {
    @Override
    public Color deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        int[] rgb = p.readValueAs(int[].class);
        if (rgb.length != 3) {
            throw new IOException("Expected 3 integers for color [r, g, b], got " + rgb.length);
        }
        return new Color(rgb[0], rgb[1], rgb[2]);
    }
}
