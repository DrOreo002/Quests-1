package com.leonardobishop.quests.blocktype;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

public class Block {

    @Getter
    @Setter
    private Material material;
    @Getter
    @Setter
    private short data;

    public Block(Material material, short data) {
        this.material = material;
        this.data = data;
    }

    public Block(Material material) {
        this.material = material;
    }
}
