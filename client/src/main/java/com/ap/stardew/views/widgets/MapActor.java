package com.ap.stardew.views.widgets;

import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.dto.AccountInfo;
import com.ap.stardew.models.gameMap.MapRegion;
import com.ap.stardew.models.gameMap.WorldMap;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.ColorPalette;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import org.tiledreader.TiledMap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class RegionActor extends Table {
    private final MapRegion mapRegion;
    public final Image image;
    private final Texture texture;
    private Vector2 centerOffset;
    private Vector2 center = new Vector2();
    private boolean hovered = false;
    private final Label nameLabel;
    private final Label usernameLabel;

    public RegionActor(WorldMap worldMap, MapRegion mapRegion) {
        this.mapRegion = mapRegion;
        Pixmap pixmap = getPixmap(worldMap, mapRegion);
        texture = new Texture(pixmap);
//        texture.setFilter(Texture.TextureFilter., Texture.TextureFilter.Linear);

        image = new Image(texture) {
            @Override
            public Actor hit(float x, float y, boolean touchable) {
                if (!isVisible() || getTouchable() != Touchable.enabled) return null;

                if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return null;

                // Convert actor-space coordinates to texture coordinates
                int texX = (int)(x * (pixmap.getWidth() / getWidth()));
                int texY = (int)((getHeight() - y) * (pixmap.getHeight() / getHeight()));

                // Sample a 4x4 area around the cursor
                int halfSize = 10; // Half of 4
                for (int dx = -halfSize; dx < halfSize; dx++) {
                    for (int dy = -halfSize; dy < halfSize; dy++) {
                        int px = texX + dx;
                        int py = texY + dy;

                        if (px >= 0 && px < pixmap.getWidth() && py >= 0 && py < pixmap.getHeight()) {
                            int pixel = pixmap.getPixel(px, py);
                            int alpha = (pixel & 0x000000ff); // assuming pixmap is RGBA8888

                            if (alpha > 10) return this; // hit if any pixel has enough alpha
                        }
                    }
                }

                return null;
            }
        };
        image.getColor().a = 0.6f;
        image.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(pointer != -1) return;
                hovered = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(pointer != -1) return;
                hovered = false;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                image.addAction(
                    Actions.sequence(
                        Actions.alpha(0.7f, 0.06f),
                        Actions.alpha(0.6f, 0.06f)
                    )
                );
                onClick();
            }
        });
        add(image).grow();

        nameLabel = new Label(mapRegion.getName(), GameAssetManager.getInstance().getCustomSkin());
        nameLabel.setFontScale(0.16f);
        nameLabel.setColor(Color.WHITE);
        nameLabel.setAlignment(Align.center);
        nameLabel.setTouchable(Touchable.disabled);
        addActor(nameLabel);
        usernameLabel = new Label("", GameAssetManager.getInstance().getCustomSkin());
        usernameLabel.setFontScale(0.14f);
        usernameLabel.setColor(Color.WHITE);
        usernameLabel.getColor().a = 0.8f;
        usernameLabel.setAlignment(Align.center);
        usernameLabel.setTouchable(Touchable.disabled);
        addActor(usernameLabel);
        usernameLabel.setVisible(false);

        centerOffset = mapRegion.getCenter().scl(1/16f).scl(1f / texture.getWidth(), 1f / texture.getHeight());
    }

    private static Pixmap getPixmap(WorldMap worldMap, MapRegion mapRegion) {
        int height = worldMap.getHeight();
        int width = worldMap.getWidth();

        MapRegion[][] regions = worldMap.getRegionMap();
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MapRegion region = regions[y][x];
                if(region == null || region != mapRegion) {
                    pixmap.setColor(0, 0, 0, 0);
                    pixmap.drawPixel(x, height - y);
                    continue;
                }

                pixmap.setColor(Color.WHITE);
                pixmap.drawPixel(x, height - y);
            }
        }
        return pixmap;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if(hovered){
            batch.end();
            ShaderProgram outlineShader = GameAssetManager.getInstance().outlineShader;
            batch.setShader(outlineShader);
            outlineShader.bind();

            Color color = image.getColor();

            batch.begin();
            outlineShader.setUniformf("u_outlineColor", color.r, color.g, color.b, color.a+0.3f);
            outlineShader.setUniformf("u_thickness", 0.005f * GameAssetManager.getInstance().ppiX);
            outlineShader.setUniformf("u_alphaThreshold", 0.1f);
            outlineShader.setUniformf("u_center", getX() + getWidth() / 2f, getY() + getHeight() / 2f, 0);
            outlineShader.setUniformf("u_scale", 1.2f);
            outlineShader.setUniformf("u_textureSize", getWidth(), getHeight());

            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
            batch.end();
            batch.setShader(null);
            batch.begin();
        }
    }

    private void updateNameLabel(){
        center.set(0, 0).add(centerOffset.cpy().scl(getWidth(), getHeight()));

        float scaleX = nameLabel.getFontScaleX();
        float scaleY = nameLabel.getFontScaleY();

        float scaledWidth = nameLabel.getWidth() * scaleX;
        float scaledHeight = nameLabel.getHeight() * scaleY;

        float width = nameLabel.getWidth();
        float height = nameLabel.getHeight();

        float newX = center.x - width / 2f;
        float newY = center.y - height / 2f;

        float borderX = (width - scaledWidth) / 2f;
        float borderY = (height - scaledHeight) / 2f;


        float minX = -borderX;
        float maxX = getWidth() - scaledWidth - borderX;
        float minY = -borderY;
        float maxY = getHeight() - scaledHeight - borderY;

        newX = Math.max(minX, Math.min(newX, maxX));
        newY = Math.max(minY, Math.min(newY, maxY));

        nameLabel.setPosition(newX, newY);
        usernameLabel.setPosition(nameLabel.getX() + nameLabel.getWidth()/2f - usernameLabel.getWidth()/2f,
            nameLabel.getY() - usernameLabel.getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateNameLabel();
    }

    public void onClick(){

    }

    public MapRegion getMapRegion() {
        return mapRegion;
    }

    public Label getUsernameLabel() {
        return usernameLabel;
    }
}
public class MapActor extends Table {
    private Image map;
    private final ArrayList<RegionActor> regionActors = new ArrayList<>();

    public MapActor(WorldMap worldMap, Texture mapImage) {
        Stack stack = new Stack();

        add(stack).grow();

        map = new Image(mapImage);
        stack.add(map);

        for (MapRegion region : worldMap.getRegions()) {
            if(region.getTilesNum() == 0) continue;
            RegionActor actor = new RegionActor(worldMap, region){
                @Override
                public void onClick() {
                    regionClicked(region);
                }
            };
            regionActors.add(actor);
            stack.add(actor);
        }
    }

    public void regionClicked(MapRegion mapRegion){

    }

    public void updateOwners(List<AccountInfo> accounts){
        for (RegionActor regionActor : regionActors) {
            String region = regionActor.getMapRegion().getName();
            String previousOwner = regionActor.getUsernameLabel().getText().toString();
            String currentOwner = null;

            for (AccountInfo account : accounts) {
                String selectedMapRegion = account.getSelectedMapRegion();

                if(selectedMapRegion != null && selectedMapRegion.equals(region)){
                    currentOwner = account.getUsername();
                    break;
                }
            }

            if(previousOwner.isEmpty() && currentOwner != null){
                Color newCoLor = ColorPalette.red.cpy();
                if(currentOwner.equals(ClientApp.getUsername())){
                    newCoLor = Color.CYAN.cpy();
                }

                newCoLor.a = regionActor.image.getColor().a;

                regionActor.image.addAction(
                    Actions.color(
                        newCoLor, 0.3f
                    )
                );

                regionActor.getUsernameLabel().setText(currentOwner);
                regionActor.getUsernameLabel().setVisible(true);
            } else if((!previousOwner.isEmpty()) && currentOwner == null){
                Color newCoLor = Color.WHITE.cpy();
                newCoLor.a = regionActor.image.getColor().a;

                regionActor.image.addAction(
                    Actions.color(
                        newCoLor, 0.3f
                    )
                );

                regionActor.getUsernameLabel().setText("");
                regionActor.getUsernameLabel().setVisible(false);
            }
        }
    }
}
