package com.ap.stardew.views;

import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.widgets.InGameDialog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class RadioDialog extends InGameDialog {

    private final Skin skin;
    private Table mainContentTable;
    private NativeFileChooser fileChooser;


    public RadioDialog(Stage stage, NativeFileChooser fileChooser) {
        super(stage);
        skin = GameAssetManager.getInstance().getCustomSkin();
        this.fileChooser = fileChooser;
        setupUI();
    }

    private void setupUI() {
        mainContentTable = new Table();
        mainContentTable.setBackground(skin.getDrawable("frameNinePatch2"));
        mainContentTable.pad(10);

        // Title
        Label titleLabel = new Label("Radio Station", skin, "big");
        mainContentTable.add(titleLabel).colspan(2).center().padBottom(10).row();

        // Buttons
        TextButton myPlaylistButton = new TextButton("My Playlist", skin);
        TextButton connectToOthersButton = new TextButton("Connect to Others", skin);

        mainContentTable.add(myPlaylistButton).fillX().pad(5);
        mainContentTable.add(connectToOthersButton).fillX().pad(5).row();

        // Listeners
        myPlaylistButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showPlaylistScreen();
            }
        });

        connectToOthersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showConnectToOthersScreen();
            }
        });

        add(mainContentTable);
        pack();
    }


    private void showPlaylistScreen() {
        mainContentTable.clear();
        mainContentTable.pad(10);

        // Title
        Label label = new Label("My Playlist", skin, "big");
        mainContentTable.add(label).colspan(2).padBottom(10).center().row();

        // Users Musics TODO: get files from server and add to scrollPane
        Table playlistContent = new Table(skin);
        playlistContent.add(new Label("Your songs will appear here.", skin)).center().row();
        List<String> musics = new ArrayList<>();

        for (String music : musics) {
            Label musicLabel = new Label(music, skin);
            TextButton textButton = new TextButton("Play", skin);
            // TODO: add listener

            playlistContent.add(musicLabel).left().pad(5);
            playlistContent.add(textButton).right().pad(5).row();
        }

        ScrollPane scrollPane = new ScrollPane(playlistContent, skin);
        scrollPane.setFadeScrollBars(false);

        mainContentTable.add(scrollPane).colspan(2).grow().padBottom(10).row();

        TextButton uploadButton = new TextButton("Upload Song", skin);
        TextButton backButton = new TextButton("Back", skin);

        mainContentTable.add(uploadButton).fillX().pad(5);
        mainContentTable.add(backButton).fillX().pad(5).row();

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainContentTable.clear();
                setupUI();
            }
        });

        uploadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var conf = new NativeFileChooserConfiguration();
                conf.directory = Gdx.files.absolute(System.getProperty("user.home"));
                conf.mimeFilter = "audio/*";
                conf.nameFilter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith("mp3") || name.endsWith("wav");
                    }
                };

                fileChooser.chooseFile(conf, new NativeFileChooserCallback() {
                    @Override
                    public void onFileChosen(FileHandle fileHandle) {
                        //TODO
                        System.out.println("Chosen file: " + fileHandle.path());
                    }

                    @Override
                    public void onCancellation() {
                        System.out.println("File chooser cancelled");
                    }

                    @Override
                    public void onError(Exception e) {
                        System.err.println("Error choosing file: " + e.getMessage());
                    }
                });
            }
        });
    }

    private void showConnectToOthersScreen() {
        mainContentTable.clear();
        mainContentTable.pad(10);

        // Title
        Label titleLabel = new Label("Connect to Others", skin, "big");
        mainContentTable.add(titleLabel).center().padBottom(10).row();

        Table playersTable = new Table(skin);

        for (Player player : ClientApp.getActiveGame().getPlayers()) {
            if(player.equals(ClientApp.getActiveGame().getCurrentPlayer()))
                continue;
            String playerName = player.getUsername();
            Label nameLabel = new Label(playerName, skin);
            TextButton tuneInButton = new TextButton("Tune In", skin);
            // TODO: add listener for this button

            playersTable.add(nameLabel).left().pad(5);
            playersTable.add(tuneInButton).right().pad(5).row();
        }



        ScrollPane scrollPane = new ScrollPane(playersTable, skin);
        scrollPane.setFadeScrollBars(false);

        mainContentTable.add(scrollPane).grow().padBottom(10).row();

        TextButton backButton = new TextButton("Back", skin);
        mainContentTable.add(backButton).fillX().padTop(10).row();

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainContentTable.clear();
                setupUI();
            }
        });

    }



}
