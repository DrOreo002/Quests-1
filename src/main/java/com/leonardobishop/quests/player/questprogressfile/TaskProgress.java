package com.leonardobishop.quests.player.questprogressfile;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class TaskProgress {

    @Getter
    @Setter
    private String taskid;
    @Getter
    @Setter
    private Object progress;
    @Getter
    @Setter
    private UUID player;
    @Getter
    @Setter
    private boolean completed;

    public TaskProgress(String taskid, Object progress, UUID player, boolean completed) {
        this.taskid = taskid;
        this.progress = progress;
        this.completed = completed;
        this.player = player;
    }
}
