package com.ap.stardew.views;

import com.ap.stardew.app.ClientApp;
import com.ap.stardew.app.GameController;
import com.ap.stardew.models.player.Message;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.widgets.InGameDialog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChatDialog {
    private static final String PUBLIC_KEY = "public";
    private static final int CHAT_MAX_CHARS_PER_LINE = 75;
    private static final float CHAT_HEIGHT = 200f;

    // visual tuning
    private static final float CHAT_PANEL_WIDTH = 400; // panel width used in setMainChatTable()
    private static final float CHAT_FONT_SCALE_TEXT = 0.2f;
    private static final float CHAT_FONT_SCALE_META = 0.15f;
    private static final float CHAT_MESSAGE_PADDING = 6f;

    private final Player currentPlayer = ClientApp.getActiveGame().getCurrentPlayer();
    private final Skin skin = GameAssetManager.getInstance().getCustomSkin();

    private Stage stage;
    private InGameDialog dialog;
    private Table chooseChatTable;
    private Table mainChatTable;
    private ScrollPane scrollPane;
    private final Map<String, Table> chatTables = new LinkedHashMap<>(); // preserves order
    private String chosenChat = PUBLIC_KEY;
    private Table activeChatTable;
    public boolean isOpen = false;

    // UI elements we need to update
    private Label chatWithLabel;
    private TextField messageField;

    public ChatDialog(ArrayList<Player> players, Stage stage) {
        this.stage = stage;

        // create select-chat area
        buildChooseChatTable(players);

        // build main chat area
        setMainChatTable();


        // load messages
        initMessages();
    }

    private void buildChooseChatTable(ArrayList<Player> players) {
        chooseChatTable = new Table(skin);
        chooseChatTable.defaults().pad(4);

        Label title = new Label("Chat with:", skin);
        chooseChatTable.add(title).left().row();

        HorizontalGroup buttons = new HorizontalGroup();
        buttons.space(6);

        // public chat
        TextButton publicChatButton = createChatTabButton("Public");
        publicChatButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                switchToChat(PUBLIC_KEY);
            }

            @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                publicChatButton.setScale(1.05f);
            }

            @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                publicChatButton.setScale(1f);
            }
        });
        buttons.addActor(publicChatButton);

        // create a table for public chat first
        Table publicTable = createEmptyChatTable();
        chatTables.put(PUBLIC_KEY, publicTable);

        // private chats
        for (Player p : players) {
            if (p.getUsername().equals(currentPlayer.getUsername())) continue;
            final String username = p.getUsername();
            // ensure a table exists for each player
            chatTables.put(username, createEmptyChatTable());

            TextButton nameButton = createChatTabButton(username);
            nameButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    switchToChat(username);
                }

                @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    nameButton.setScale(1.05f);
                }

                @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    nameButton.setScale(1f);
                }
            });
            buttons.addActor(nameButton);
        }

        chooseChatTable.add(buttons).left().row();

        // set initial active table
        activeChatTable = chatTables.get(PUBLIC_KEY);
    }

    private TextButton createChatTabButton(String text) {
        TextButton b = new TextButton(text, skin);
        b.getLabel().setEllipsis(true);
        b.getLabel().setWrap(false);
        return b;
    }

    private Table createEmptyChatTable() {
        Table t = new Table(skin);
        t.top();
        return t;
    }

    private void setMainChatTable() {
        mainChatTable = new Table(skin);
        mainChatTable.pad(6);

        Label title = new Label("Chat with:", skin);
        title.setColor(Color.BLACK);
        chatWithLabel = new Label(chosenChat, skin);
        chatWithLabel.setColor(Color.WHITE);

        mainChatTable.add(title).right().padRight(6);
        mainChatTable.add(chatWithLabel).left().row();

        scrollPane = new ScrollPane(activeChatTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setForceScroll(false, true);

        mainChatTable.add(scrollPane)
            .width(CHAT_PANEL_WIDTH)
            .height(CHAT_HEIGHT)
            .colspan(2)
            .pad(8)
            .row();

        messageField = new TextField("", skin);
        messageField.setMessageText("message...");
        messageField.setFocusTraversal(false); // nicer UX
        // send on Enter
        messageField.setTextFieldListener((field, c) -> {
            if (c == '\n' || c == '\r') {
                sendCurrentMessage();
            }
        });

        TextButton sendButton = new TextButton("Send", skin);
        sendButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                sendCurrentMessage();
            }
        });

        mainChatTable.add(messageField).growX().colspan(1).pad(4);
        mainChatTable.add(sendButton).width(90).pad(4).row();
    }

    private void sendCurrentMessage() {
        String text = messageField.getText().trim();
        if (text.isEmpty()) return;

        if (PUBLIC_KEY.equals(chosenChat)) {
            GameController.sendPublicMessage(text);
        } else {
            GameController.sendPrivateMessage(chosenChat, text);
        }

        // clear input
        messageField.setText("");
        messageField.setCursorPosition(0);
    }

    private void switchToChat(String key) {
        if (!chatTables.containsKey(key)) {
            // create on demand
            chatTables.put(key, createEmptyChatTable());
        }
        chosenChat = key;
        activeChatTable = chatTables.get(key);
        chatWithLabel.setText(key);
        scrollPane.setActor(activeChatTable);
        scrollToBottom();
    }

    public void updateMessage(String playerUsername, Message message) {
        String key = (playerUsername == null) ? PUBLIC_KEY : playerUsername;
        Table table = chatTables.get(key);
        if (table == null) {
            table = createEmptyChatTable();
            chatTables.put(key, table);
        }

        Table messageRow = new Table(skin);
        messageRow.pad(4);
        messageRow.defaults().pad(2);

        // time
        String time = message.getDate().toString(); // keep original formatting, change as needed

        Label dateLabel = new Label(time, skin);
        dateLabel.setFontScale(CHAT_FONT_SCALE_META);

        Label senderLabel = new Label(message.getSender(), skin);
        senderLabel.setFontScale(CHAT_FONT_SCALE_META);

        // Use Label's wrapping and set a max width so text becomes smaller visually and wraps
        Label textLabel = new Label(wrapByWords(message.getMessage(), CHAT_MAX_CHARS_PER_LINE), skin);
        textLabel.setWrap(true);
        textLabel.setAlignment(Align.left);
        textLabel.setFontScale(CHAT_FONT_SCALE_TEXT);

        boolean isMe = message.getSender().equals(currentPlayer.getUsername());

        // calculate available width for a message (leave some margin for padding and scrollbar)
        float availableWidth = CHAT_PANEL_WIDTH - 48f; // tune this number to taste

        // colouring and alignment
        if (isMe) {
            senderLabel.setColor(Color.CYAN);
            textLabel.setColor(Color.WHITE);
            dateLabel.setColor(Color.BLACK);

            messageRow.add(senderLabel).right().row();
            // apply width to label so wrapping & smaller look works
            messageRow.add(textLabel).right().growX().width(availableWidth).pad(CHAT_MESSAGE_PADDING).row();
            messageRow.add(dateLabel).right().row();
            table.add(messageRow).expandX().right().pad(4).row();
        } else {
            senderLabel.setColor(Color.RED);
            textLabel.setColor(Color.WHITE);
            dateLabel.setColor(Color.BLACK);

            messageRow.add(senderLabel).left().row();
            messageRow.add(textLabel).left().growX().width(availableWidth).pad(CHAT_MESSAGE_PADDING).row();
            messageRow.add(dateLabel).left().row();
            table.add(messageRow).expandX().left().pad(4).row();
        }

        // if this chat is currently visible, ensure scroll goes to bottom
        if (chosenChat.equals(key)) {
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        if (scrollPane == null) return;
        scrollPane.layout();
        // set scroll percent to bottom
        scrollPane.setScrollPercentY(1f);
        // extra layout to ensure renderer picks it up
        scrollPane.layout();
    }

    public void initMessages() {
        // clear any existing content (in case of re-init)
        for (Table t : chatTables.values()) t.clear();

        // public messages
        for (Message message : ClientApp.getActiveGame().getPublicChat()) {
            updateMessage(null, message);
        }

        // private messages from current player's log (receiver == partner username)
        for (Message message : ClientApp.getActiveGame().getCurrentPlayer().getMessageLog()) {
            updateMessage(message.getReceiver(), message);
        }

        // ensure UI shows correct actor
        switchToChat(chosenChat);
    }

    public void show() {
        isOpen = true;
        dialog = new InGameDialog(stage);
        dialog.setBackground((Drawable) null);
        dialog.add(chooseChatTable).growX().row();
        dialog.add().height(8).growX().row();
        dialog.add(mainChatTable).grow().row();
        dialog.show();
    }

    public void hide() {
        isOpen = false;
        dialog.hide();
    }

    public boolean isPublic() {
        return PUBLIC_KEY.equals(chosenChat);
    }

    private String wrapByWords(String text, int maxChars) {
        // keep original but in case skin's label wrapping isn't desired this is a fallback
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        StringBuilder out = new StringBuilder();

        for (String w : words) {
            if (line.length() + w.length() + (line.length() > 0 ? 1 : 0) > maxChars) {
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
