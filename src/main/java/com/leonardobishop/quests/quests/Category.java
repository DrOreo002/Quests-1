package com.leonardobishop.quests.quests;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Category {

    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private ItemStack displayItem;
    @Getter
    @Setter
    private boolean permissionRequired;
    @Getter
    private List<String> registeredQuestIds = new ArrayList<>();

    public Category(String id, ItemStack displayItem, boolean permissionRequired) {
        this.id = id;
        this.displayItem = displayItem;
        this.permissionRequired = permissionRequired;
    }

    public void registerQuestId(String questid) {
        registeredQuestIds.add(questid);
    }
}
