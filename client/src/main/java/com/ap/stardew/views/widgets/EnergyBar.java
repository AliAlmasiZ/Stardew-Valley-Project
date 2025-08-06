package com.ap.stardew.views.widgets;

import com.ap.stardew.models.Game;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.models.App;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EnergyBar extends Table {
    private final Cell fill;
    private final Player player;

    Color fullColor = new Color(0, 1, 0, 1);
    Color emptyColor = new Color(1, 0, 0, 1);

    public EnergyBar(Player player) {
        this.player = player;

        setBackground(GameAssetManager.getInstance().getCustomSkin().getDrawable("energyBarNinePatch"));
        getBackground().setMinHeight(70);
        bottom();

        fill = add(new Image(GameAssetManager.getInstance().getCustomSkin().getDrawable("energyBarFill2NinePatch")))
            .width(6).bottom().fill();

    }

    @Override
    public void act(float delta) {
        float percent = ((float) player.getEnergy().getAmount()) /
            ((float) player.getEnergy().getMaxEnergy());

        fill.getActor().setColor(fullColor.cpy().mul(percent).add(emptyColor.cpy().mul(1 - percent)));

        fill.height(percent * (getHeight() - getBackground().getBottomHeight() - getBackground().getTopHeight()));
        layout();
    }
}
