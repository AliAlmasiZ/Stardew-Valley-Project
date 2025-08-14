package com.ap.stardew.views.widgets.paralaxBackground;

import com.ap.stardew.view.GameAssetManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

public class GradientActor extends Actor {
    private ShaderProgram shader;
    private List<ColorStop> stops = new ArrayList<>();
    private Texture dummy;

    private static class ColorStop {
        Color color;
        float position; // 0-1
        ColorStop(Color c, float pos) { color = c; position = pos; }
    }

    public GradientActor(String cssGradient) {
        parseCssGradient(cssGradient);

        shader = GameAssetManager.getInstance().gradientShader;

        dummy = new Texture(1, 1, Pixmap.Format.RGBA8888);

        for (ColorStop stop : stops) {
            System.out.printf("%s, %f%n",stop.color.toString(), stop.position);
        }
    }

    private void parseCssGradient(String css) {
        // Remove "linear-gradient(...)" if it's in the string
        css = css.trim();
        if (css.startsWith("linear-gradient")) {
            int start = css.indexOf("(") + 1;
            int end = css.lastIndexOf(")");
            css = css.substring(start, end);
        }

        // Split stops manually by finding commas outside parentheses
        List<String> stopStrings = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        for (char c : css.toCharArray()) {
            if (c == '(') depth++;
            if (c == ')') depth--;
            if (c == ',' && depth == 0) {
                stopStrings.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) stopStrings.add(current.toString().trim());

        // Parse each stop
        for (String stop : stopStrings) {
            // Example: "rgba(239, 251, 255, 1) 0%"
            String[] parts = stop.trim().split("\\)");
            if (parts.length < 2) continue;

            String rgbaStr = parts[0].replace("rgba(", "").trim();
            String[] rgbaVals = rgbaStr.split(",");
            float r = Integer.parseInt(rgbaVals[0].trim()) / 255f;
            float g = Integer.parseInt(rgbaVals[1].trim()) / 255f;
            float b = Integer.parseInt(rgbaVals[2].trim()) / 255f;
            float a = Float.parseFloat(rgbaVals[3].trim());

            String posStr = parts[1].trim().replace("%", "");
            float pos = Float.parseFloat(posStr) / 100f;

            stops.add(new ColorStop(new Color(r, g, b, a), pos));
        }
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();

        batch.setShader(shader);
        shader.bind();
        shader.setUniformi("u_count", stops.size());
        float[] colorArray = new float[stops.size() * 4];
        float[] posArray = new float[stops.size()];
        for (int i = 0; i < stops.size(); i++) {
            colorArray[i*4]   = stops.get(i).color.r;
            colorArray[i*4+1] = stops.get(i).color.g;
            colorArray[i*4+2] = stops.get(i).color.b;
            colorArray[i*4+3] = stops.get(i).color.a;
            posArray[i]       = stops.get(i).position;
        }
        shader.setUniform4fv("u_colors", colorArray, 0, colorArray.length);
        shader.setUniform1fv("u_positions", posArray, 0, posArray.length);
        batch.begin();
        batch.draw(dummy, getX(), getY(), getWidth(), getHeight());
        batch.end();
        batch.setShader(null);
        batch.begin();
    }
}
