package com.leonardobishop.quests.player.questprogressfile;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestProgress {

    private final Map<String, TaskProgress> taskProgress = new HashMap<>();
    @Getter
    private String questid;
    @Getter
    @Setter
    private boolean started;
    @Getter
    @Setter
    private boolean completed;
    @Getter
    @Setter
    private boolean completedBefore;
    @Getter
    @Setter
    private long completionDate;
    @Getter
    private UUID player;
    @Getter
    @Setter
    private boolean modified;

    public QuestProgress(String questid, boolean completed, boolean completedBefore, long completionDate, UUID player, boolean started) {
        this.questid = questid;
        this.completed = completed;
        this.completedBefore = completedBefore;
        this.completionDate = completionDate;
        this.player = player;
        this.started = started;
    }

    public QuestProgress(String questid, boolean completed, boolean completedBefore, long completionDate, UUID player, boolean started, boolean modified) {
        this(questid, completed, completedBefore, completionDate, player, started);
        this.modified = modified;
    }

    public void addTaskProgress(TaskProgress taskProgress) {
        this.taskProgress.put(taskProgress.getTaskid(), taskProgress);
    }

    public Collection<TaskProgress> getTaskProgress() {
        return taskProgress.values();
    }

    public TaskProgress getTaskProgress(String taskId) {
        TaskProgress tP = taskProgress.getOrDefault(taskId, null);
        if (tP == null) {
            repairTaskProgress(taskId);
            tP = taskProgress.getOrDefault(taskId, null);
        }
        return tP;
    }

    public void repairTaskProgress(String taskid) {
        TaskProgress taskProgress = new TaskProgress(taskid, null, player, false);
        this.addTaskProgress(taskProgress);
    }
}
