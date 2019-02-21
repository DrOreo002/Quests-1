package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

public final class MobkillingTaskType extends TaskType {

    private final List<ConfigValue> creatorConfigValues = new ArrayList<>();
    private final Quests plugin = Quests.getInstance();

    public MobkillingTaskType() {
        super("mobkilling", "lmbishop", "Kill a set amount of entities.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of mobs to be killed."));
        this.creatorConfigValues.add(new ConfigValue("hostile", false, "Only allow hostile or non-hostile mobs (unspecified = any type allowed)."));
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobKill(EntityDeathEvent event) {
        Entity killer = event.getEntity().getKiller();
        Entity mob = event.getEntity();

        if (mob instanceof Player) {
            return;
        }

        if (killer == null) {
            return;
        }

        Player player = event.getEntity().getKiller();

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

        for (Quest quest : super.getRegisteredQuests()) {
            if (questProgressFile.hasStartedQuest(quest)) {
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    boolean hostilitySpecified = false;
                    boolean hostile = false;
                    if (task.getConfigValue("hostile") != null) {
                        hostilitySpecified = true;
                        hostile = (boolean) task.getConfigValue("hostile");
                    }

                    if (hostilitySpecified) {
                        if (!hostile && !(mob instanceof Animals)) {
                            continue;
                        } else if (hostile && !(mob instanceof Monster)) {
                            continue;
                        }
                    }

                    int mobKillsNeeded = (int) task.getConfigValue("amount");

                    int progressKills;
                    if (taskProgress.getProgress() == null) {
                        progressKills = 0;
                    } else {
                        progressKills = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressKills + 1);

                    if (((int) taskProgress.getProgress()) >= mobKillsNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
