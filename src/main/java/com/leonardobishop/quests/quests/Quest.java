package com.leonardobishop.quests.quests;

import com.leonardobishop.quests.obj.misc.QItemStack;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.*;

public class Quest {

    private final Map<String, Task> tasks = new HashMap<>();
    //TODO: maybe store by <tasktypename (string), list<task>> since we never get task by id, but always get tasks by type.

    @Getter
    private String id;
    @Getter
    private QItemStack displayItem;
    @Getter
    private List<String> rewards;
    @Getter
    private List<String> requirements;
    @Getter
    private List<String> rewardString;
    @Getter
    private boolean repeatable;
    @Getter
    private boolean cooldownEnabled;
    @Getter
    private int cooldown;
    @Getter
    private boolean permissionRequired;
    @Getter
    private String categoryid;


    public Quest(String id, QItemStack displayItem, List<String> rewards, List<String> requirements, boolean repeatable, boolean cooldownEnabled, int cooldown, boolean permissionRequired, List<String> rewardString, String categoryid) {
        this(id, displayItem, rewards, requirements, repeatable, cooldownEnabled, cooldown, permissionRequired, rewardString);
        this.categoryid = categoryid;
    }

    public Quest(String id, QItemStack displayItem, List<String> rewards, List<String> requirements, boolean repeatable, boolean cooldownEnabled, int cooldown, boolean permissionRequired, List<String> rewardString) {
        this.id = id;
        this.displayItem = displayItem;
        this.rewards = rewards;
        this.requirements = requirements;
        this.repeatable = repeatable;
        this.cooldownEnabled = cooldownEnabled;
        this.cooldown = cooldown;
        this.permissionRequired = permissionRequired;
        this.rewardString = rewardString;
    }

    public void registerTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public Collection<Task> getTasks() {
        return tasks.values();
    }

    public List<Task> getTasksOfType(String type) {
        List<Task> tasks = new ArrayList<>();
        for (Task task : getTasks()) {
            if (task.getType().equals(type)) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    public String getDisplayNameStripped() {
        return ChatColor.stripColor(this.displayItem.getName());
    }
}
