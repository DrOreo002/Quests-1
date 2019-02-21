package com.leonardobishop.quests.player;

import com.leonardobishop.quests.obj.misc.QMenu;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.events.EventInventory;
import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.obj.misc.QMenuCategory;
import com.leonardobishop.quests.obj.misc.QMenuQuest;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QPlayer {

    private final Quests plugin = Quests.getInstance();

    @Getter
    private final UUID uuid;
    @Getter
    @Setter
    private QuestProgressFile questProgressFile;
    @Getter
    private boolean onlyDataLoaded;

    public QPlayer(UUID uuid, QuestProgressFile questProgressFile) {
        this(uuid, questProgressFile, false);
    }

    public QPlayer(UUID uuid, QuestProgressFile questProgressFile, boolean onlyDataLoaded) {
        this.uuid = uuid;
        this.questProgressFile = questProgressFile;
        this.onlyDataLoaded = onlyDataLoaded;
    }

    /**
     * Open the category
     *
     * @param category : The category that will be opened
     * @param superMenu : The super menu for that category
     * @param backButton : Should we add backButton?
     * @return 0 if success, 1 if no permission, 2 is only data loaded, 3 if player not found
     */
    public int openCategory(Category category, QMenuCategory superMenu, boolean backButton) {
        if (onlyDataLoaded) {
            return 2;
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return 3;
        }

        QMenuQuest qMenuQuest = new QMenuQuest(plugin.getPlayerManager().getPlayer(player.getUniqueId()), category.getId(), superMenu);
        List<Quest> quests = new ArrayList<>();
        for (String questid : category.getRegisteredQuestIds()) {
            Quest quest = plugin.getQuestManager().getQuestById(questid);
            if (quest != null) {
                quests.add(quest);
            }
        }
        qMenuQuest.populate(quests);
        qMenuQuest.setBackButtonEnabled(backButton);
        return openCategory(category, qMenuQuest);
    }

    /**
     * Open a category (Straight open it)
     *
     * @param category : The category
     * @param qMenuQuest : The menu quest
     * @return 0 if success, 1 if no permission, 2 is only data loaded, 3 if player not found
     */
    public int openCategory(Category category, QMenuQuest qMenuQuest) {
        if (onlyDataLoaded) {
            return 2;
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return 3;
        }

        if (category.isPermissionRequired() && !player.hasPermission("quests.category." + category.getId())) {
            return 1;
        }

        player.openInventory(qMenuQuest.toInventory(1));
        EventInventory.track(player.getUniqueId(), qMenuQuest);
        return 0;
    }

    public void openQuests() {
        if (onlyDataLoaded) {
            return;
        }

        if (uuid == null) {
            return;
        }
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        if (Options.CATEGORIES_ENABLED.getBooleanValue()) {
            QMenuCategory qMenuCategory = new QMenuCategory(plugin.getPlayerManager().getPlayer(player.getUniqueId()));
            List<QMenuQuest> questMenus = new ArrayList<>();
            for (Category category : plugin.getQuestManager().getCategories()) {
                QMenuQuest qMenuQuest = new QMenuQuest(plugin.getPlayerManager().getPlayer(player.getUniqueId()), category.getId(), qMenuCategory);
                List<Quest> quests = new ArrayList<>();
                for (String questid : category.getRegisteredQuestIds()) {
                    Quest quest = plugin.getQuestManager().getQuestById(questid);
                    if (quest != null) {
                        quests.add(quest);
                    }
                }
                qMenuQuest.populate(quests);
                questMenus.add(qMenuQuest);
            }
            qMenuCategory.populate(questMenus);

            player.openInventory(qMenuCategory.toInventory(1));
            EventInventory.track(player.getUniqueId(), qMenuCategory);
        } else {
            QMenuQuest qMenuQuest = new QMenuQuest(plugin.getPlayerManager().getPlayer(player.getUniqueId()), "", null);
            List<Quest> quests = new ArrayList<>();
            for (Map.Entry<String, Quest> entry : plugin.getQuestManager().getQuests().entrySet()) {
                quests.add(entry.getValue());
            }
            qMenuQuest.populate(quests);
            qMenuQuest.setBackButtonEnabled(false);

            player.openInventory(qMenuQuest.toInventory(1));
            EventInventory.track(player.getUniqueId(), qMenuQuest);
        }
    }
}
