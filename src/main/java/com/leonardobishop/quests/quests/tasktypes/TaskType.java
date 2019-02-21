package com.leonardobishop.quests.quests.tasktypes;

import com.leonardobishop.quests.quests.Quest;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TaskType implements Listener {

    protected final List<Quest> quests = new ArrayList<>();

    @Getter
    protected String type;
    @Getter
    protected String author;
    @Getter
    protected String description;

    public TaskType(String type, String author, String description) {
        this.type = type;
        this.author = author;
        this.description = description;
    }

    public TaskType(String type) {
        this.type = type;
    }

    public final void registerQuest(Quest quest) {
        if (!quests.contains(quest)) {
            quests.add(quest);
        }
    }

    public final List<Quest> getRegisteredQuests() {
        return quests;
    }

    public abstract List<ConfigValue> getCreatorConfigValues();
}
