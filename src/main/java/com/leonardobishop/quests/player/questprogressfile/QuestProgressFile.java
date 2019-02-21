package com.leonardobishop.quests.player.questprogressfile;

import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.obj.Messages;
import com.leonardobishop.quests.obj.Options;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class QuestProgressFile {

    private final Map<String, QuestProgress> questProgress = new HashMap<>();
    private final UUID player;
    private final Quests plugin = Quests.getInstance();

    public QuestProgressFile(UUID player) {
        this.player = player;
    }

    /**
     * Complete the Quest
     * @param quest : Quest object
     */
    public void completeQuest(Quest quest) {
        QuestProgress questProgress = getQuestProgress(quest);
        questProgress.setStarted(false);
        questProgress.setCompleted(true);
        questProgress.setCompletedBefore(true);
        questProgress.setCompletionDate(System.currentTimeMillis());
        if (Bukkit.getPlayer(player) != null) {
            Player player = Bukkit.getPlayer(this.player);
            Bukkit.getServer().getScheduler().runTask(Quests.getInstance(), () -> {
                for (String s : quest.getRewards()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replace("{player}", player.getName()));
                }
            });
            player.sendMessage(Messages.QUEST_COMPLETE.getMessage().replace("{quest}", quest.getDisplayNameStripped()));
            if (Options.TITLES_ENABLED.getBooleanValue()) {
                plugin.getTitle().sendTitle(player, Messages.TITLE_QUEST_COMPLETE_TITLE.getMessage().replace("{quest}", quest
                        .getDisplayNameStripped()), Messages.TITLE_QUEST_COMPLETE_SUBTITLE.getMessage().replace("{quest}", quest
                        .getDisplayNameStripped()));
            }
            for (String s : quest.getRewardString()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
    }

    /**
     * Start a quest for the player.
     *
     * @param quest the quest to check
     * @return 0 if successful, 1 if limit reached, 2 if quest is already completed, 3 if quest has cooldown, 4 if still locked, 5 if already started, 6 if
     * no permission, 7 if no permission for category
     */
    public int startQuest(Quest quest) {
        Player p = Bukkit.getPlayer(player);
        if (getStartedQuests().size() >= Options.QUESTS_START_LIMIT.getIntValue()) {
            if (player != null) {
                p.sendMessage(Messages.QUEST_START_LIMIT.getMessage().replace("{limit}", String.valueOf(Options.QUESTS_START_LIMIT.getIntValue())));
            }
            return 1;
        }
        QuestProgress questProgress = getQuestProgress(quest);
        if (!quest.isRepeatable() && questProgress.isCompletedBefore()) {
            if (player != null) {
                p.sendMessage(Messages.QUEST_START_DISABLED.getMessage());
            }
            return 2;
        }
        long cooldown = getCooldownFor(quest);
        if (cooldown > 0) {
            if (player != null) {
                p.sendMessage(Messages.QUEST_START_COOLDOWN.getMessage().replace("{time}", String.valueOf(Quests.convertToFormat(TimeUnit.MINUTES.convert
                        (cooldown, TimeUnit.MILLISECONDS)))));
            }
            return 3;
        }
        if (!hasMetRequirements(quest)) {
            if (player != null) {
                p.sendMessage(Messages.QUEST_START_LOCKED.getMessage());
            }
            return 4;
        }
        if (questProgress.isStarted()) {
            if (player != null) {
                p.sendMessage(Messages.QUEST_START_STARTED.getMessage());
            }
            return 5;
        }
        if (quest.isPermissionRequired()) {
            if (player != null) {
                if (!p.hasPermission("quests.quest." + quest.getId())) {
                    p.sendMessage(Messages.QUEST_START_PERMISSION.getMessage());
                    return 6;
                }
            } else {
                return 6;
            }
        }
        if (quest.getCategoryid() != null && plugin.getQuestManager().getCategoryById(quest.getCategoryid()) != null && plugin.getQuestManager().getCategoryById(quest.getCategoryid()).isPermissionRequired()) {
            if (player != null) {
                if (!p.hasPermission("quests.category." + quest.getCategoryid())) {
                    p.sendMessage(Messages.QUEST_CATEGORY_QUEST_PERMISSION.getMessage());
                    return 7;
                }
            } else {
                return 7;
            }
        }

        questProgress.setStarted(true);
        for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
            taskProgress.setCompleted(false);
            taskProgress.setProgress(null);
        }
        questProgress.setCompleted(false);
        if (Bukkit.getPlayer(player) != null) {
            Player player = Bukkit.getPlayer(getPlayer());
            player.sendMessage(Messages.QUEST_START.getMessage().replace("{quest}", quest.getDisplayNameStripped()));
            if (Options.TITLES_ENABLED.getBooleanValue()) {
                plugin.getTitle().sendTitle(player, Messages.TITLE_QUEST_START_TITLE.getMessage().replace("{quest}", quest
                        .getDisplayNameStripped()), Messages.TITLE_QUEST_START_SUBTITLE.getMessage().replace("{quest}", quest
                        .getDisplayNameStripped()));
            }
        }
        return 0;
    }

    /**
     * Cancel the quest.
     *
     * @param quest : Quest object
     * @return true if succeeded, false otherwise
     */
    public boolean cancelQuest(Quest quest) {
        QuestProgress questProgress = getQuestProgress(quest);
        if (!questProgress.isStarted()) {
            if (Bukkit.getPlayer(player) != null) {
                Bukkit.getPlayer(getPlayer()).sendMessage(Messages.QUEST_CANCEL_NOTSTARTED.getMessage());
            }
            return false;
        }
        questProgress.setStarted(false);
        for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
            taskProgress.setProgress(null);
        }
        if (Bukkit.getPlayer(player) != null) {
            Bukkit.getPlayer(getPlayer()).sendMessage(Messages.QUEST_CANCEL.getMessage().replace("{quest}", quest.getDisplayNameStripped()));
        }
        return true;
    }

    /**
     * Add that quest progress into the cache
     *
     * @param questProgress : The questProgressA.K.A the data
     */
    public void addQuestProgress(QuestProgress questProgress) {
        this.questProgress.put(questProgress.getQuestid(), questProgress);
    }

    public List<Quest> getStartedQuests() {
        List<Quest> startedQuests = new ArrayList<>();
        for (QuestProgress questProgress : questProgress.values()) {
            if (questProgress.isStarted()) {
                startedQuests.add(plugin.getQuestManager().getQuestById(questProgress.getQuestid()));
            }
        }
        return startedQuests;
    }

    public boolean hasQuestProgress(Quest quest) {
        return questProgress.containsKey(quest.getId());
    }

    public boolean hasStartedQuest(Quest quest) {
        //TODO always return true if the need for starting quests is disabled & requirements are met
        if (hasQuestProgress(quest)) {
            return getQuestProgress(quest).isStarted();
        }
        return false;
    }

    public long getCooldownFor(Quest quest) {
        QuestProgress questProgress = getQuestProgress(quest);
        if (quest.isCooldownEnabled() && questProgress.isCompleted()) {
            if (questProgress.getCompletionDate() > 0) {
                long date = questProgress.getCompletionDate();
                return (date + TimeUnit.MILLISECONDS.convert(quest.getCooldown(), TimeUnit.MINUTES)) - System.currentTimeMillis();
            }
        }
        return 0;
    }

    public boolean hasMetRequirements(Quest quest) {
        for (String id : quest.getRequirements()) {
            Quest q = plugin.getQuestManager().getQuestById(id);
            if (q == null) {
                continue;
            }
            if (hasQuestProgress(q) && !getQuestProgress(q).isCompletedBefore()) {
                return false;
            } else if (!hasQuestProgress(q)) {
                return false;
            }
        }
        return true;
    }

    public UUID getPlayer() {
        return player;
    }

    public QuestProgress getQuestProgress(Quest quest) {
        if (questProgress.containsKey(quest.getId())) {
            return questProgress.get(quest.getId());
        } else if (generateBlankQuestProgress(quest.getId())) {
            return getQuestProgress(quest);
        }
        return null;
    }

    public boolean generateBlankQuestProgress(String questid) {
        if (plugin.getQuestManager().getQuestById(questid) != null) {
            Quest quest = plugin.getQuestManager().getQuestById(questid);
            QuestProgress questProgress = new QuestProgress(quest.getId(), false, false, 0, player, false, false);
            for (Task task : quest.getTasks()) {
                TaskProgress taskProgress = new TaskProgress(task.getId(), null, player, false);
                questProgress.addTaskProgress(taskProgress);
            }

            addQuestProgress(questProgress);
            return true;
        }
        return false;
    }

    public void saveToDisk() {
        File directory = new File(Quests.getInstance().getDataFolder() + File.separator + "playerdata");
        if (!directory.exists() && !directory.isDirectory()) {
            directory.mkdirs();
        }
        File file = new File(Quests.getInstance().getDataFolder() + File.separator + "playerdata" + File.separator + player.toString() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        data.set("quest-progress", null);
        for (QuestProgress questProgress : questProgress.values()) {
            if (!questProgress.isModified()) {
                continue;
            }
            data.set("quest-progress." + questProgress.getQuestid() + ".started", questProgress.isStarted());
            data.set("quest-progress." + questProgress.getQuestid()+ ".completed", questProgress.isCompleted());
            data.set("quest-progress." + questProgress.getQuestid() + ".completed-before", questProgress.isCompletedBefore());
            data.set("quest-progress." + questProgress.getQuestid() + ".completion-date", questProgress.getCompletionDate());
            for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
                data.set("quest-progress." + questProgress.getQuestid() + ".task-progress." + taskProgress.getTaskid() + ".completed", taskProgress
                        .isCompleted());
                data.set("quest-progress." + questProgress.getQuestid() + ".task-progress." + taskProgress.getTaskid() + ".progress", taskProgress
                        .getProgress());
            }
        }

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        questProgress.clear();
    }
}

