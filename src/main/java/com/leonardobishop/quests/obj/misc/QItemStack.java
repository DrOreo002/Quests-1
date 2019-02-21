package com.leonardobishop.quests.obj.misc;

import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QItemStack {

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private List<String> loreNormal;
    @Getter
    @Setter
    private List<String> loreStarted;
    @Getter
    @Setter
    private Material type;
    @Getter
    @Setter
    private int data;

    public QItemStack(String name, List<String> loreNormal, List<String> loreStarted, Material type, int data) {
        this.name = name;
        this.loreNormal = loreNormal;
        this.loreStarted = loreStarted;
        this.type = type;
        this.data = data;
    }

    public ItemStack toItemStack(QuestProgress questProgress) {
        ItemStack is = new ItemStack(type, 1, (short) data);
        ItemMeta ism = is.getItemMeta();
        ism.setDisplayName(name);
        List<String> formattedLore = new ArrayList<>();
        List<String> tempLore = new ArrayList<>();
        tempLore.addAll(loreNormal);
        if (questProgress != null && questProgress.isStarted()) {
            tempLore.addAll(loreStarted);
            ism.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            try {
                ism.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                ism.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            } catch (Exception ignored) {

            }
        }
        if (questProgress != null) {
            for (String s : tempLore) {
                Matcher m = Pattern.compile("\\{([^}]+)\\}").matcher(s);
                while (m.find()) {
                    String[] parts = m.group(1).split(":");
                    if (parts.length > 1) {
                        if (questProgress.getTaskProgress(parts[0]) == null) {
                            continue;
                        }
                        if (parts[1].equals("progress")) {
                            String str = String.valueOf(questProgress.getTaskProgress(parts[0]).getProgress());
                            s = s.replace("{" + m.group(1) + "}", (str.equals("null") ? String.valueOf(0) : str));
                        }
                        if (parts[1].equals("complete")) {
                            String str = String.valueOf(questProgress.getTaskProgress(parts[0]).isCompleted());
                            s = s.replace("{" + m.group(1) + "}", str);
                        }
                    }
                }
                formattedLore.add(s);
            }
        }
        ism.setLore(formattedLore);
        is.setItemMeta(ism);
        return is;
    }
}
