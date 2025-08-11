package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.controllers.RadioController;
import com.ap.stardew.models.dto.JSONMessage;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.widgets.InGameDialog;
import com.ap.stardew.views.widgets.PopUpMessage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    private GameScreen gameScreen;


    public RadioDialog(GameScreen gameScreen) {
        super(gameScreen.uiStage);
        this.gameScreen = gameScreen;
        skin = GameAssetManager.getInstance().getCustomSkin();
        this.fileChooser = ClientGame.getInstance().fileChooser;
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
                        System.out.println("Chosen file: " + fileHandle.path());
                        if(musics.contains(fileHandle.name())) {
                            PopUpMessage message = new PopUpMessage(PopUpMessage.PopUpMessageType.ERROR_NOTIFICATION);
                            message.add(new Label("file with this name already exists", gameScreen.customSkin));
                            message.show(gameScreen.uiStage);
                            return;
                        }
                        // Dialog for show Process

                        // TODO : better progress bar maybe
                        Pixmap bgPixmap = new Pixmap(100, 15, Pixmap.Format.RGBA8888);
                        bgPixmap.setColor(Color.BROWN);
                        bgPixmap.fill();
                        Texture progressBarBgTex = new Texture(bgPixmap);
                        bgPixmap.dispose();

                        Pixmap knobPixmap = new Pixmap(1 , 15, Pixmap.Format.RGBA8888);
                        knobPixmap.setColor(Color.GREEN);
                        knobPixmap.fill();
                        Texture progressBarKnobTex = new Texture(knobPixmap);
                        knobPixmap.dispose();

                        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
                        progressBarStyle.background = new TextureRegionDrawable(new TextureRegion(progressBarBgTex));
                        progressBarStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(progressBarKnobTex));

                        InGameDialog progressDialog = new InGameDialog(gameScreen.uiStage);
                        ProgressBar progressBar = new ProgressBar(0f, 1f, 0.01f, false, progressBarStyle);
                        progressBar.setValue(0f);
                        progressBar.setAnimateDuration(0.2f);

                        Label percentLabel = new Label("0%", skin);
                        Table content = new Table();
                        content.add(progressBar).width(300).pad(10).row();
                        content.add(percentLabel).pad(5).row();

                        progressDialog.add(content);
                        progressDialog.show();

                        // Upload with another thread
                        new Thread(() -> {
                            RadioController.uploadFile(fileHandle.path(), fileHandle.name(), progress -> {
                                // progress is between 0 and 1
                                Gdx.app.postRunnable(() -> {
                                    progressBar.setValue(progress);
                                    percentLabel.setText((int)(progress * 100) + "%");
                                });
                            });

                            Gdx.app.postRunnable(() -> {
                                progressDialog.hide();
                                showPlaylistScreen();
                            });
                        }).start();
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
