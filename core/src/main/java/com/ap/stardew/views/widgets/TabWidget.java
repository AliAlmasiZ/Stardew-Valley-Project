package com.ap.stardew.views.widgets;

import com.ap.stardew.controllers.GameAssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class TabWidget extends Table {
    private static class TabDetails{
        private final Table tab;
        private final Image button;

        public TabDetails(Table tab, Image button) {
            this.tab = tab;
            this.button = button;
        }
    }

    private final Table tabHeaderTable;
    private final Stack contentStack;
    private final ArrayList<TabDetails> tabs = new ArrayList<>();
    private final Skin skin;
    private TabDetails currentTab = null;

    public TabWidget() {
        this.skin = GameAssetManager.getInstance().getCustomSkin();

        tabHeaderTable = new Table();
        tabHeaderTable.pad(0);
        contentStack = new Stack();

        Table wrapperTable = new Table(skin);
        wrapperTable.setBackground("frameNinePatch2");
        wrapperTable.add(contentStack).grow();

        tabHeaderTable.left();

        this.top();
        this.add(tabHeaderTable).growX().padLeft(5).row();
        this.add(wrapperTable).grow().row();
    }

    public void addTab(final Table content, Drawable tabIcon) {
        Image tabButton = new Image(tabIcon);
        TabDetails tabDetails = new TabDetails(content, tabButton);
        tabs.add(tabDetails);
        contentStack.add(content);

        content.setFillParent(true);
        content.setVisible(false);

        tabButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                selectTab(tabDetails);
            }
        });
        tabHeaderTable.add(tabButton).bottom();

        selectTab(tabDetails);
    }

    private void selectTab(TabDetails tabDetails) {
        if(currentTab != null){
            if(currentTab == tabDetails) return;
            currentTab.button.moveBy(0, 1);
            currentTab.tab.setVisible(false);
        }
        currentTab = tabDetails;
        tabDetails.tab.setVisible(true);
        currentTab.button.moveBy(0, -1);
    }
}
