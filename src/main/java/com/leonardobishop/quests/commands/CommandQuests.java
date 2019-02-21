package com.leonardobishop.quests.commands;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.obj.Messages;
import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import static me.droreo002.oreocore.utils.strings.StringUtil.*;

public class CommandQuests implements CommandExecutor {

    private final Quests plugin;

    public CommandQuests(Quests plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (plugin.isBrokenConfig()) {
            sender.sendMessage(ChatColor.RED + "You have a YAML error in your config and Quests cannot load. If this is your first time using Quests, please " +
                    "delete the Quests folder and RESTART (not reload!) the server. If you have modified the config, check for errors in a YAML parser.");
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            showHelp(sender);
            return true;
        }

        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            qPlayer.openQuests();
            return true;
        } else if (args.length >= 1) {
            if ((args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("admin")) && sender.hasPermission("quests.admin")) {
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        showAdminHelp(sender, "opengui");
                        return true;
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        showAdminHelp(sender, "moddata");
                        return true;
                    } else if (args[1].equalsIgnoreCase("reload")) {
                        plugin.reloadConfig();
                        plugin.reloadQuests();
                        sender.sendMessage(ChatColor.GRAY + "Quests was reloaded.");
                        return true;
                    } else if (args[1].equalsIgnoreCase("types")) {
                        sender.sendMessage(ChatColor.GRAY + "Registered task types:");
                        for (TaskType taskType : plugin.getTaskTypeManager().getTaskTypes()) {
                            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + taskType.getType());
                        }
                        sender.sendMessage(ChatColor.DARK_GRAY + "View info using /q a types [type].");
                        return true;
                    } else if (args[1].equalsIgnoreCase("update")) {
                        sender.sendMessage(ChatColor.GRAY + "Checking for updates...");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                plugin.getUpdater().check();
                                if (plugin.getUpdater().isUpdateReady()) {
                                    sender.sendMessage(plugin.getUpdater().getMessage());
                                } else {
                                    sender.sendMessage(ChatColor.GRAY + "No updates were found.");
                                }
                            }
                        }.runTaskAsynchronously(plugin);
                        return true;
                    }
                } else if (args.length == 3) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        showAdminHelp(sender, "opengui");
                        return true;
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        showAdminHelp(sender, "moddata");
                        return true;
                    } else if (args[1].equalsIgnoreCase("types")) {
                        TaskType taskType = null;
                        for (TaskType task : plugin.getTaskTypeManager().getTaskTypes()) {
                            if (task.getType().equals(args[2])) {
                                taskType = task;
                            }
                        }
                        if (taskType == null) {
                            sender.sendMessage(Messages.COMMAND_TASKVIEW_ADMIN_FAIL.getMessage().replace("{task}", args[2]));
                        } else {
                            sender.sendMessage(ChatColor.RED + "Task type: " + ChatColor.GRAY + taskType.getType());
                            sender.sendMessage(ChatColor.RED + "Author: " + ChatColor.GRAY + taskType.getAuthor());
                            sender.sendMessage(ChatColor.RED + "Description: " + ChatColor.GRAY + taskType.getDescription());
                        }
                        return true;
                    }
                } else if (args.length == 4) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        if (args[2].equalsIgnoreCase("q") || args[2].equalsIgnoreCase("quests")) {
                            Player player = Bukkit.getPlayer(args[3]);
                            if (player != null) {
                                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                                if (qPlayer != null) {
                                    qPlayer.openQuests();
                                    sender.sendMessage(Messages.COMMAND_QUEST_OPENQUESTS_ADMIN_SUCCESS.getMessage().replace("{player}", player.getName()));
                                    return true;
                                }
                            }
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.getMessage().replace("{player}", args[3]));
                            return true;
                        }
                        showAdminHelp(sender, "opengui");
                        return true;
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        Player player;
                        OfflinePlayer ofp;
                        UUID uuid;
                        String name;
                        if ((player = Bukkit.getPlayer(args[3])) != null) {
                            uuid = player.getUniqueId();
                            name = player.getName();
                        } else if ((ofp = Bukkit.getOfflinePlayer(args[3])) != null) {
                            uuid = ofp.getUniqueId();
                            name = ofp.getName();
                        } else {
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.getMessage().replace("{player}", args[3]));
                            return true;
                        }
                        if (args[2].equalsIgnoreCase("fullreset")) {
                            if (plugin.getPlayerManager().getPlayer(uuid) == null) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_LOADDATA.getMessage().replace("{player}", name));
                                plugin.getPlayerManager().loadPlayer(uuid, true);
                            }
                            if (plugin.getPlayerManager().getPlayer(uuid) == null) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_NODATA.getMessage().replace("{player}", name));
                                return true;
                            }
                            QuestProgressFile questProgressFile = plugin.getPlayerManager().getPlayer(uuid).getQuestProgressFile();
                            questProgressFile.clear();
                            questProgressFile.saveToDisk();
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_FULLRESET.getMessage().replace("{player}", name));
                            return true;
                        }
                        if (plugin.getPlayerManager().getPlayer(uuid).isOnlyDataLoaded()) {
                            plugin.getPlayerManager().removePlayer(uuid);
                        }
                        showAdminHelp(sender, "moddata");
                        return true;
                    }
                } else if (args.length == 5) {
                    if (args[1].equalsIgnoreCase("opengui")) {
                        if (args[2].equalsIgnoreCase("c") || args[2].equalsIgnoreCase("category")) {
                            if (!Options.CATEGORIES_ENABLED.getBooleanValue()) {
                                sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DISABLED.getMessage());
                                return true;
                            }
                            Category category = plugin.getQuestManager().getCategoryById(args[4]);
                            if (category == null) {
                                sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DOESNTEXIST.getMessage().replace("{category}", args[4]));
                                return true;
                            }
                            Player player = Bukkit.getPlayer(args[3]);
                            if (player != null) {
                                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                                if (qPlayer != null) {
                                    if (qPlayer.openCategory(category, null) == 0) {
                                        sender.sendMessage(Messages.COMMAND_QUEST_OPENCATEGORY_ADMIN_SUCCESS.getMessage().replace("{player}", player.getName())
                                                .replace("{category}", category.getId()));
                                    } else {
                                        sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_CATEGORY_PERMISSION.getMessage().replace("{player}", player.getName())
                                                .replace("{category}", category.getId()));
                                    }
                                    return true;
                                }
                            }
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.getMessage().replace("{player}", args[3]));
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("moddata")) {
                        boolean success = false;
                        Player player;
                        OfflinePlayer ofp;
                        UUID uuid;
                        String name;
                        if ((player = Bukkit.getPlayer(args[3])) != null) {
                            uuid = player.getUniqueId();
                            name = player.getName();
                        } else if ((ofp = Bukkit.getOfflinePlayer(args[3])) != null) {
                            uuid = ofp.getUniqueId();
                            name = ofp.getName();
                        } else {
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_PLAYERNOTFOUND.getMessage().replace("{player}", args[3]));
                            return true;
                        }
                        if (plugin.getPlayerManager().getPlayer(uuid) == null) {
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_LOADDATA.getMessage().replace("{player}", name));
                            plugin.getPlayerManager().loadPlayer(uuid, true);
                        }
                        if (plugin.getPlayerManager().getPlayer(uuid) == null) {
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_NODATA.getMessage().replace("{player}", name));
                            success = true;
                        }
                        QuestProgressFile questProgressFile = plugin.getPlayerManager().getPlayer(uuid).getQuestProgressFile();
                        Quest quest = plugin.getQuestManager().getQuestById(args[4]);
                        if (quest == null) {
                            sender.sendMessage(Messages.COMMAND_QUEST_START_DOESNTEXIST.getMessage().replace("{quest}", args[4]));
                            return true;
                        }
                        if (args[2].equalsIgnoreCase("reset")) {
                            questProgressFile.generateBlankQuestProgress(quest.getId());
                            questProgressFile.saveToDisk();
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_RESET_SUCCESS.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                            success = true;
                        } else if (args[2].equalsIgnoreCase("start")) {
                            int response = questProgressFile.startQuest(quest);
                            if (response == 1) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILLIMIT.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == 2) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILCOMPLETE.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == 3) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILCOOLDOWN.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == 4) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILLOCKED.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == 5) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILSTARTED.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == 6) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILPERMISSION.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            } else if (response == 7) {
                                sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_FAILCATEGORYPERMISSION.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                                return true;
                            }
                            questProgressFile.saveToDisk();
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_START_SUCCESS.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                            success = true;
                        } else if (args[2].equalsIgnoreCase("complete")) {
                            questProgressFile.completeQuest(quest);
                            questProgressFile.saveToDisk();
                            sender.sendMessage(Messages.COMMAND_QUEST_ADMIN_COMPLETE_SUCCESS.getMessage().replace("{player}", name).replace("{quest}", quest.getId()));
                            success = true;
                        }
                        if (plugin.getPlayerManager().getPlayer(uuid).isOnlyDataLoaded()) {
                            plugin.getPlayerManager().removePlayer(uuid);
                        }
                        if (!success) {
                            showAdminHelp(sender, "moddata");
                        }
                        return true;
                    }
                }
                showAdminHelp(sender, null);
                return true;
            }
            if (sender instanceof Player && (args[0].equalsIgnoreCase("q") || args[0].equalsIgnoreCase("quests"))) {
                Player player = (Player) sender;
                if (args.length >= 2) {
                    Quest quest = plugin.getQuestManager().getQuestById(args[1]);
                    if (quest == null) {
                        sender.sendMessage(Messages.COMMAND_QUEST_START_DOESNTEXIST.getMessage().replace("{quest}", args[1]));
                    } else {
                        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                        if (qPlayer == null) {
                            // shit + fan
                            sender.sendMessage(ChatColor.RED + "An error occurred finding your player.");
                        } else {
                            qPlayer.getQuestProgressFile().startQuest(quest);
                        }
                    }
                    return true;
                }
            } else if (sender instanceof Player && (args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("category"))) {
                if (!Options.CATEGORIES_ENABLED.getBooleanValue()) {
                    sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DISABLED.getMessage());
                    return true;
                }
                Player player = (Player) sender;
                if (args.length >= 2) {
                    Category category = plugin.getQuestManager().getCategoryById(args[1]);
                    if (category == null) {
                        sender.sendMessage(Messages.COMMAND_CATEGORY_OPEN_DOESNTEXIST.getMessage().replace("{category}", args[1]));
                    } else {
                        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                        qPlayer.openCategory(category, null);
                        return true;
                    }
                    return true;
                }
            }
            showHelp(sender);
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Only admin commands are available to non-player senders.");
        }
        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(color("&8&m------------------ &7[ &bQuest &7] &8&m------------------"));
        sender.sendMessage(" ");
        sender.sendMessage(color("&b            Code edited by &eDrOreo002"));
        sender.sendMessage(" ");
        sender.sendMessage("&7(&b✿&7) &8» &e/quest &f: Main command");
        sender.sendMessage("&7(&b✿&7) &8» &e/quest <c|category> <id> &f: Open category by ID");
        sender.sendMessage("&7(&b✿&7) &8» &e/quest <q|quest> <id> &f: Start quest by ID");
        sender.sendMessage("&7(&b✿&7) &8» &e/quest <a|admin> &f: Open admin help command");
        sender.sendMessage(" ");
        sender.sendMessage(color("&8&m------------------ &7[ &bQuest &7] &8&m------------------"));
    }

    private void showAdminHelp(CommandSender sender, String command) {
        sender.sendMessage(color("&8&m------------------ &7[ &bQuest &7] &8&m------------------"));
        sender.sendMessage(" ");
        if (command != null && command.equalsIgnoreCase("opengui")) {
            sender.sendMessage("&7(&b✿&7) &8» &e/quest a opengui <q|quest> <player> &f: Show quest for player");
            sender.sendMessage("&7(&b✿&7) &8» &e/quest a opengui <c|category> <player> <category> &f: Show quest category to player");
        } else if (command != null && command.equalsIgnoreCase("moddata")) {
            sender.sendMessage("&7(&b✿&7) &8» &e/quest a moddata fullreset <player> &f: Clear player's data file");
            sender.sendMessage("&7(&b✿&7) &8» &e/quest a moddata reset <player> <questID> &f: Clear player data for x quest");
            sender.sendMessage("&7(&b✿&7) &8» &e/quest a mmoddata start <player> <questID> &f: Start a quest for that player");
            sender.sendMessage("&7(&b✿&7) &8» &e/quest a moddata complete <player> <questID> &f: Complete the player's quest");
        } else {
            sender.sendMessage("&7(&b✿&7) &8» &e/quest a opengui &f: View help for opengui command");
            sender.sendMessage("&7(&b✿&7) &8» &e/quest a moddata &f: View help for moddata command");
            sender.sendMessage("&7(&b✿&7) &8» &e/quest a types <type> &f: View registered task types");
            sender.sendMessage("&7(&b✿&7) &8» &e/quest a reload &f: Reload the config and other things");
            sender.sendMessage("&7(&b✿&7) &8» &e/quest a update &f: Check for an update");;
        }
        sender.sendMessage(" ");
        sender.sendMessage(color("&8&m------------------ &7[ &bQuest &7] &8&m------------------"));
    }
}
