package com.ap.stardew.views;

import com.ap.stardew.ClientGame;
import com.ap.stardew.app.ClientApp;
import com.ap.stardew.app.GameController;
import com.ap.stardew.models.player.Message;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.widgets.InGameDialog;
import com.ap.stardew.views.widgets.TabWidget;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatDialog {
    private final Player currentPlayer = ClientApp.getActiveGame().getCurrentPlayer();
    private InGameDialog dialog;
    private Table chooseChatTable;
    private Table mainChatTable;
    private ScrollPane scrollPane;
    private Table publicChatTable;
    private HashMap<String, Table> chatTables = new HashMap<>();
    private Skin skin = GameAssetManager.getInstance().getCustomSkin();

    private String chosenChat = "public";
    private Table activeChatTable;
    public boolean isOpen = false;


    public ChatDialog(ArrayList<Player> players, Stage stage) {
        dialog = new InGameDialog(stage);
        dialog.setBackground((Drawable) null);
        TabWidget tabWidget = new TabWidget();

        chooseChatTable = new Table();
        publicChatTable = new Table();
        activeChatTable = publicChatTable;
        for (Player player : players) {
            if (player.getUsername().equals(currentPlayer.getUsername())) continue;
            chatTables.put(player.getUsername(), new Table(skin));
        }

        Label title = new Label("Chat with:", skin);
        TextButton publicChatButton = new TextButton("Public", skin);
        publicChatButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chosenChat = "public";
                activeChatTable = publicChatTable;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                publicChatButton.setScale(1.2f);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                publicChatButton.setScale(1f);
            }
        });
        chooseChatTable.add(title).center().growX().row();
        chooseChatTable.add(publicChatButton).width(40).pad(5);
        for (String p : chatTables.keySet()) {
            TextButton nameButton = new TextButton(p, skin);
            publicChatButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    chosenChat = p;
                    activeChatTable = chatTables.get(p);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    publicChatButton.setScale(1.2f);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    publicChatButton.setScale(1f);
                }
            });
            chooseChatTable.add(nameButton).width(40).pad(5);
        }

        setMainChatTable();

        dialog.add(chooseChatTable);
        dialog.add().height(10).growX().row();
        dialog.add(mainChatTable);

        initMessages();

    }

    public void setMainChatTable() {
        mainChatTable = new Table(skin);
        Label title = new Label("Chat with:", skin);
        Label chatWithLabel = new Label(chosenChat, skin);
        title.setColor(Color.BLACK);
        chatWithLabel.setColor(Color.WHITE);

        TextField messageField = new TextField("", skin);
        messageField.setMessageText("message...");

        TextButton sendButton = new TextButton("Send", skin);
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (messageField.getText().isEmpty()) return;
                if (chosenChat.equals("public")) {
                    GameController.sendPublicMessage(messageField.getText());
                }else {
                    GameController.sendPrivateMessage(chosenChat, messageField.getText());
                }
            }
        });

        scrollPane = new ScrollPane(activeChatTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setHeight(150);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.layout();
        scrollPane.setScrollY(scrollPane.getMaxY());


        mainChatTable.add(title).right().pad(3);
        mainChatTable.add(chatWithLabel).left().pad(3).row();
        mainChatTable.add(scrollPane).width((float) Gdx.graphics.getWidth() / 2).height(150).colspan(2).pad(8);
        mainChatTable.row();
        mainChatTable.add(messageField).colspan(2).growX().pad(2).row();
        mainChatTable.add(sendButton).colspan(2).growX().pad(2).row();
    }

    public void updateMessage(String playerUsername, Message message) {
        Table table;
        if (playerUsername == null) table = publicChatTable;
        else table = chatTables.get(playerUsername);

        Table messageContainer = new Table(skin);
        Label dateLabel = new Label(message.getDate().toString(), skin);
        dateLabel.setScale(0.5f);
        Label senderLabel = new Label(message.getSender(), skin);
        if (message.getSender().equals(currentPlayer.getUsername())) {
            senderLabel.setColor(Color.CYAN);
        } else {
            senderLabel.setColor(Color.GREEN);
        }
        senderLabel.setScale(0.5f);

        Label text = new Label(wrapByWords(message.getMessage(), 75), skin);
        if (message.getSender().equals(currentPlayer.getUsername())) {
            senderLabel.setColor(Color.CYAN);
        } else {
            senderLabel.setColor(Color.GREEN);
        }

        messageContainer.add(senderLabel).pad(5).right();
        messageContainer.add(senderLabel).pad(5).left();
        messageContainer.row();
        messageContainer.add(dateLabel).growX();



        table.add(messageContainer).width(chooseChatTable.getWidth()).pad(10).row();
        scrollPane.layout();
        scrollPane.setScrollY(scrollPane.getMaxY());

        if (playerUsername == null) publicChatTable = table;
        else chatTables.put(playerUsername, table);
    }

    public void initMessages() {
        for (Message message : ClientApp.getActiveGame().getPublicChat()) {
            updateMessage(null, message);
        }

        for (Message message : ClientApp.getActiveGame().getCurrentPlayer().getMessageLog()) {
            updateMessage(message.getReceiver(), message);
        }
    }

    public void show() {
        isOpen = true;
        dialog.show();
    }

    public void hide() {
        isOpen = false;
        dialog.hide();
    }

    public boolean isPublic() {
        return chosenChat.equals("public");
    }

    private String wrapByWords(String text, int maxChars) {
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        StringBuilder out = new StringBuilder();

        for (String w : words) {
            if (line.length() + w.length() + (line.length()>0?1:0) > maxChars) {
                if (out.length() > 0) out.append('\n');
                out.append(line);
                line.setLength(0);
            }
            if (line.length() > 0) line.append(' ');
            line.append(w);
        }
        if (line.length() > 0) {
            if (out.length() > 0) out.append('\n');
            out.append(line);
        }
        return out.toString();
    }

}
