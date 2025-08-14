package com.ap.stardew.views.dialogs;

import com.ap.stardew.app.ClientApp;
import com.ap.stardew.models.enums.SkillType;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.view.GameAssetManager;
import com.ap.stardew.views.widgets.InGameDialog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class ScoreBoardDialog {
    //TODO: make it look better
    private final float FONT_SCALE = 0.3f;
    private final Skin skin = GameAssetManager.getInstance().getCustomSkin();

    private InGameDialog dialog;
    private Stage stage;
    private Table rootTable;       // container for whole dialog
    private Table headerTable;     // headers (clickable)
    private Table rowsTable;       // rows placed inside scrollpane
    private ScrollPane scrollPane;

    // Local copy of players to avoid concurrent modification
    private final ArrayList<Player> playersCopy = new ArrayList<>();

    private SortType currentSort = SortType.MONEY;
    private boolean ascending = false; // default: descending (best first)

    public enum SortType {
        MONEY,
        FARMING_SKILL,
        FORAGING_SKILL,
        MINING_SKILL,
        FISHING_SKILL,
        QUESTS,
    }

    public ScoreBoardDialog(Stage stage) {
        this.stage = stage;
        rootTable = new Table();
        headerTable = new Table();
        rowsTable = new Table();
        rowsTable.defaults().pad(6).expandX().fillX();

        buildUI();
        // initial load
        refreshFromGame();
    }

    private void buildUI() {
        // header row - clickable labels
        headerTable.clear();
        headerTable.defaults().pad(6);

        Label indexH = new Label("#", skin);
        Label nameH = new Label("Player", skin);
        Label moneyH = makeHeader("Money", SortType.MONEY);
        Label farmingH = makeHeader("Farming", SortType.FARMING_SKILL);
        Label foragingH = makeHeader("Foraging", SortType.FORAGING_SKILL);
        Label miningH = makeHeader("Mining", SortType.MINING_SKILL);
        Label fishingH = makeHeader("Fishing", SortType.FISHING_SKILL);
        Label questsH = makeHeader("Quests", SortType.QUESTS);

        indexH.setFontScale(FONT_SCALE);
        nameH.setFontScale(FONT_SCALE);
        moneyH.setFontScale(FONT_SCALE);
        farmingH.setFontScale(FONT_SCALE);
        foragingH.setFontScale(FONT_SCALE);
        miningH.setFontScale(FONT_SCALE);
        fishingH.setFontScale(FONT_SCALE);
        questsH.setFontScale(FONT_SCALE);

        // Layout widths
        headerTable.add(indexH).width(10).align(Align.left);
        headerTable.add(nameH).expandX();
        headerTable.add(moneyH).width(25);
        headerTable.add(farmingH).width(25);
        headerTable.add(foragingH).width(25);
        headerTable.add(miningH).width(25);
        headerTable.add(fishingH).width(25);
        headerTable.add(questsH).width(25);

        // scroll pane for rows
        scrollPane = new ScrollPane(rowsTable, skin);
        scrollPane.setFlickScroll(true);
        scrollPane.setFadeScrollBars(false);

        // root table assembly
        rootTable.clear();
        rootTable.defaults().pad(8);
        rootTable.add(headerTable).colspan(8).expandX().fillX().row();
        rootTable.add(scrollPane).colspan(8).expand().fill().minWidth(500).minHeight(200);

        // Add rootTable into your InGameDialog when showing
    }

    private Label makeHeader(final String text, final SortType type) {
        final Label header = new Label(text + getSortIndicator(type), skin);
        header.setAlignment(Align.right);
        header.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onHeaderClicked(type);
            }
        });
        return header;
    }

    private String getSortIndicator(SortType type) {
        if (type != currentSort) return "";
        return ascending ? " ▲" : " ▼";
    }

    private void onHeaderClicked(SortType type) {
        // toggle ascending if same column clicked twice
        if (currentSort == type) {
            ascending = !ascending;
        } else {
            currentSort = type;
            ascending = false; // default to descending (best first)
        }
        // rebuild headers (to update indicator) and rows
        buildUI();               // rebuild header labels (could be optimized)
        refreshFromGame();
    }

    /**
     * Call this to show the dialog.
     */
    public void show() {
        dialog = new InGameDialog(stage);
        dialog.add(rootTable).expand().fill();
        dialog.setBackground((Drawable) null);
        dialog.show();
    }

    public void hide() {
        if (dialog != null) dialog.hide();
    }

    /**
     * Refresh the data from the game model (safe to call from any thread).
     * This grabs a snapshot of ClientApp.getActiveGame().getPlayers() and updates UI on render thread.
     */
    public void refreshFromGame() {
        // fetch snapshot off-render-thread just in case network thread calls us
        final ArrayList<Player> snapshot = new ArrayList<>();
        try {
            ArrayList<Player> source = ClientApp.getActiveGame().getPlayers();
            synchronized (source) {
                snapshot.addAll(source);
            }
        } catch (Exception e) {
            // fallback: try again on render thread
            Gdx.app.postRunnable(this::refreshFromGame);
            return;
        }

        // post UI update
        Gdx.app.postRunnable(() -> {
            playersCopy.clear();
            playersCopy.addAll(snapshot);
            sortPlayers();
            rebuildRows();
        });
    }

    /**
     * Comparator & sorting based on currentSort + ascending flag.
     */
    private void sortPlayers() {
        Comparator<Player> comparator = (a, b) -> {
            switch (currentSort) {
                case MONEY:
                    // compare money (double)
                    double ma = a.getWallet().getBalance();
                    double mb = b.getWallet().getBalance();
                    return Double.compare(ma, mb);
                case FARMING_SKILL:
                    return Integer.compare(a.getSkill(SkillType.FARMING).getExperience(),
                        b.getSkill(SkillType.FARMING).getExperience());
                case FORAGING_SKILL:
                    return Integer.compare(a.getSkill(SkillType.FORAGING).getExperience(),
                        b.getSkill(SkillType.FORAGING).getExperience());
                case MINING_SKILL:
                    return Integer.compare(a.getSkill(SkillType.MINING).getExperience(),
                        b.getSkill(SkillType.MINING).getExperience());
                case FISHING_SKILL:
                    return Integer.compare(a.getSkill(SkillType.FISHING).getExperience(),
                        b.getSkill(SkillType.FISHING).getExperience());
                case QUESTS:
                    return Integer.compare(a.getNumberOfQuestsDone(), b.getNumberOfQuestsDone());
                default:
                    return 0;
            }
        };

        // apply ascending/descending
        if (ascending) {
            Collections.sort(playersCopy, comparator);
        } else {
            Collections.sort(playersCopy, comparator.reversed());
        }
    }

    /**
     * Rebuild table rows from playersCopy.
     * Keeps it simple: clears and re-adds all rows. That's fine for tens-hundreds of players.
     */
    private void rebuildRows() {
        rowsTable.clear();
        rowsTable.defaults().pad(6);

        int i = 1;
        for (Player p : playersCopy) {
            Label index = new Label(String.valueOf(i), skin);
            Label name = new Label(p.getUsername(), skin);
            Label money = new Label(String.format(Locale.US, "%.2f", p.getWallet().getBalance()), skin);

            Label farming = new Label(String.valueOf(p.getSkill(SkillType.FARMING).getExperience()), skin);
            Label foraging = new Label(String.valueOf(p.getSkill(SkillType.FORAGING).getExperience()), skin);
            Label mining = new Label(String.valueOf(p.getSkill(SkillType.MINING).getExperience()), skin);
            Label fishing = new Label(String.valueOf(p.getSkill(SkillType.FISHING).getExperience()), skin);
            Label quests = new Label(String.valueOf(p.getNumberOfQuestsDone()), skin);

            farming.setFontScale(FONT_SCALE);
            foraging.setFontScale(FONT_SCALE);
            mining.setFontScale(FONT_SCALE);
            fishing.setFontScale(FONT_SCALE);
            quests.setFontScale(FONT_SCALE);

            rowsTable.add(index).width(10);
            rowsTable.add(name).expandX();
            rowsTable.add(money).width(25);
            rowsTable.add(farming).width(25);
            rowsTable.add(foraging).width(25);
            rowsTable.add(mining).width(25);
            rowsTable.add(fishing).width(25);
            rowsTable.add(quests).width(25);
            rowsTable.row();

            i++;
        }

        // scroll to top after rebuild (optional)
        scrollPane.setScrollY(0);
    }

}
