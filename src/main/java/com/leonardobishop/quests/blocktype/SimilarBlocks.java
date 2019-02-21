package com.leonardobishop.quests.blocktype;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class SimilarBlocks {

    private static final HashMap<Block, Block> SIMILAR = new HashMap<>();

    static {
        // Redstone Ore
        SIMILAR.put(new Block(Material.REDSTONE_ORE), new Block(Material.GLOWING_REDSTONE_ORE));
        SIMILAR.put(new Block(Material.GLOWING_REDSTONE_ORE), new Block(Material.REDSTONE_ORE));

        // Oak Door
        SIMILAR.put(new Block(Material.WOODEN_DOOR, (short) 0), new Block(Material.WOODEN_DOOR));
        SIMILAR.put(new Block(Material.WOODEN_DOOR, (short) 1), new Block(Material.WOODEN_DOOR));
        SIMILAR.put(new Block(Material.WOODEN_DOOR, (short) 2), new Block(Material.WOODEN_DOOR));
        SIMILAR.put(new Block(Material.WOODEN_DOOR, (short) 3), new Block(Material.WOODEN_DOOR));

        // Dark Oak Door
        SIMILAR.put(new Block(Material.DARK_OAK_DOOR, (short) 0), new Block(Material.DARK_OAK_DOOR));
        SIMILAR.put(new Block(Material.DARK_OAK_DOOR, (short) 1), new Block(Material.DARK_OAK_DOOR));
        SIMILAR.put(new Block(Material.DARK_OAK_DOOR, (short) 2), new Block(Material.DARK_OAK_DOOR));
        SIMILAR.put(new Block(Material.DARK_OAK_DOOR, (short) 3), new Block(Material.DARK_OAK_DOOR));

        // Acacia Door
        SIMILAR.put(new Block(Material.ACACIA_DOOR, (short) 0), new Block(Material.ACACIA_DOOR));
        SIMILAR.put(new Block(Material.ACACIA_DOOR, (short) 1), new Block(Material.ACACIA_DOOR));
        SIMILAR.put(new Block(Material.ACACIA_DOOR, (short) 2), new Block(Material.ACACIA_DOOR));
        SIMILAR.put(new Block(Material.ACACIA_DOOR, (short) 3), new Block(Material.ACACIA_DOOR));

        // Birch Door
        SIMILAR.put(new Block(Material.BIRCH_DOOR, (short) 0), new Block(Material.BIRCH_DOOR));
        SIMILAR.put(new Block(Material.BIRCH_DOOR, (short) 1), new Block(Material.BIRCH_DOOR));
        SIMILAR.put(new Block(Material.BIRCH_DOOR, (short) 2), new Block(Material.BIRCH_DOOR));
        SIMILAR.put(new Block(Material.BIRCH_DOOR, (short) 3), new Block(Material.BIRCH_DOOR));

        // Jungle Door
        SIMILAR.put(new Block(Material.JUNGLE_DOOR, (short) 0), new Block(Material.JUNGLE_DOOR));
        SIMILAR.put(new Block(Material.JUNGLE_DOOR, (short) 1), new Block(Material.JUNGLE_DOOR));
        SIMILAR.put(new Block(Material.JUNGLE_DOOR, (short) 2), new Block(Material.JUNGLE_DOOR));
        SIMILAR.put(new Block(Material.JUNGLE_DOOR, (short) 3), new Block(Material.JUNGLE_DOOR));

        // Spruce Door
        SIMILAR.put(new Block(Material.SPRUCE_DOOR, (short) 0), new Block(Material.SPRUCE_DOOR));
        SIMILAR.put(new Block(Material.SPRUCE_DOOR, (short) 1), new Block(Material.SPRUCE_DOOR));
        SIMILAR.put(new Block(Material.SPRUCE_DOOR, (short) 2), new Block(Material.SPRUCE_DOOR));
        SIMILAR.put(new Block(Material.SPRUCE_DOOR, (short) 3), new Block(Material.SPRUCE_DOOR));

        // Iron Door
        SIMILAR.put(new Block(Material.IRON_DOOR, (short) 0), new Block(Material.IRON_DOOR));
        SIMILAR.put(new Block(Material.IRON_DOOR, (short) 1), new Block(Material.IRON_DOOR));
        SIMILAR.put(new Block(Material.IRON_DOOR, (short) 2), new Block(Material.IRON_DOOR));
        SIMILAR.put(new Block(Material.IRON_DOOR, (short) 3), new Block(Material.IRON_DOOR));

        // Oak Log
        SIMILAR.put(new Block(Material.LOG, (short) 4), new Block(Material.LOG, (short) 0));
        SIMILAR.put(new Block(Material.LOG, (short) 8), new Block(Material.LOG, (short) 0));

        // Spruce Log
        SIMILAR.put(new Block(Material.LOG, (short) 5), new Block(Material.LOG, (short) 1));
        SIMILAR.put(new Block(Material.LOG, (short) 9), new Block(Material.LOG, (short) 1));

        // Birch Log
        SIMILAR.put(new Block(Material.LOG, (short) 6), new Block(Material.LOG, (short) 2));
        SIMILAR.put(new Block(Material.LOG, (short) 10), new Block(Material.LOG, (short) 2));

        // Jungle Log
        SIMILAR.put(new Block(Material.LOG, (short) 7), new Block(Material.LOG, (short) 3));
        SIMILAR.put(new Block(Material.LOG, (short) 11), new Block(Material.LOG, (short) 3));

        // Acacia Log
        SIMILAR.put(new Block(Material.LOG_2, (short) 4), new Block(Material.LOG, (short) 0));
        SIMILAR.put(new Block(Material.LOG_2, (short) 8), new Block(Material.LOG, (short) 0));

        // Dark Oak Log
        SIMILAR.put(new Block(Material.LOG_2, (short) 5), new Block(Material.LOG, (short) 1));
        SIMILAR.put(new Block(Material.LOG_2, (short) 9), new Block(Material.LOG, (short) 1));
    }

    public static Block getSimilarBlock(Block block) {
        for (Map.Entry<Block, Block> entry : SIMILAR.entrySet()) {
            if (entry.getKey().getMaterial() == block.getMaterial() && entry.getKey().getData() == block.getData()) {
                return entry.getValue();
            }
        }
        return null;
    }
}
