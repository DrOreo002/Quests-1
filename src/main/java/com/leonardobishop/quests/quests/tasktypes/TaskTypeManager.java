package com.leonardobishop.quests.quests.tasktypes;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TaskTypeManager {

    @Getter
    private final List<TaskType> taskTypes = new ArrayList<>();

    public void resetTaskTypes() {
        for (TaskType taskType : taskTypes) {
            taskType.getRegisteredQuests().clear();
        }
    }

    public void registerTaskType(TaskType taskType) {
        Bukkit.getPluginManager().registerEvents(taskType, Quests.getInstance());
        Quests.getInstance().getLogger().log(Level.INFO, "Task type " + taskType.getType() + " has been registered.");
        taskTypes.add(taskType);
    }

    public void registerQuestTasksWithTaskTypes(Quest quest) {
        for (Task task : quest.getTasks()) {
            for (TaskType taskType : taskTypes) {
                if (taskType.getType().equalsIgnoreCase(task.getType())) {
                    taskType.registerQuest(quest);
                }
            }
        }
    }
}
