package com.leonardobishop.quests.quests.tasktypes;

import lombok.Getter;

/**
 * This is for the quest creator and is purely cosmetic.
 */
public final class ConfigValue {

    @Getter
    private String key;
    @Getter
    private boolean required;
    @Getter
    private String description;

    public ConfigValue(String key, boolean required, String description) {
        this.key = key;
        this.required = required;
        this.description = description;
    }
}
