package cz.tvojeserver.customnpc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class NPCCommand implements CommandExecutor {

    private final CustomNPC plugin;

    public NPCCommand(CustomNPC plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Tento prikaz muze pouzit jen hrac.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Pouziti: /npc create <id> <jmeno> [typ_entity]");
                    return true;
                }
                String id = args[1].toLowerCase();
                if (plugin.getConfig().contains("npcs." + id)) {
                    player.sendMessage(ChatColor.RED + "NPC s timto ID uz existuje!");
                    return true;
                }

                EntityType type = EntityType.VILLAGER;
                String name;

                try {
                    EntityType parsed = EntityType.valueOf(args[args.length - 1].toUpperCase());
                    if (args.length >= 4) {
                        type = parsed;
                        name = String.join(" ", Arrays.copyOfRange(args, 2, args.length - 1));
                    } else {
                        name = args[2];
                    }
                } catch (IllegalArgumentException e) {
                    name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                }

                plugin.createNPC(id, name, player.getLocation(), type);
                player.sendMessage(ChatColor.GREEN + "NPC '" + id + "' bylo vytvoreno!");
            }

            case "remove" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Pouziti: /npc remove <id>");
                    return true;
                }
                if (plugin.removeNPC(args[1].toLowerCase())) {
                    player.sendMessage(ChatColor.GREEN + "NPC bylo odstraneno.");
                } else {
                    player.sendMessage(ChatColor.RED + "NPC s timto ID neexistuje.");
                }
            }

            case "cmd" -> {
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Pouziti: /npc cmd <id> <prikaz_bez_lomitka>");
                    return true;
                }
                String id = args[1].toLowerCase();
                if (!plugin.getConfig().contains("npcs." + id)) {
                    player.sendMessage(ChatColor.RED + "NPC s timto ID neexistuje.");
                    return true;
                }
                String cmd = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                plugin.getConfig().set("npcs." + id + ".command", cmd);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Prikaz pro NPC '" + id + "' nastaven na: /" + cmd);
            }

            case "list" -> {
                if (!plugin.getConfig().contains("npcs")) {
                    player.sendMessage(ChatColor.YELLOW + "Zadna NPC nejsou vytvorena.");
                    return true;
                }
                player.sendMessage(ChatColor.GOLD + "Seznam NPC: " + ChatColor.WHITE +
                        String.join(", ", plugin.getConfig().getConfigurationSection("npcs").getKeys(false)));
            }

            default -> sendHelp(player);
        }
        return true;
    }

    private void sendHelp(Player p) {
        p.sendMessage(ChatColor.GOLD + "=== CustomNPC Napoveda ===");
        p.sendMessage(ChatColor.YELLOW + "/npc create <id> <jmeno> [mob] " + ChatColor.GRAY + "- Vytvori NPC");
        p.sendMessage(ChatColor.YELLOW + "/npc cmd <id> <prikaz> " + ChatColor.GRAY + "- Nastavi prikaz po kliknuti (%player% = jmeno hrace)");
        p.sendMessage(ChatColor.YELLOW + "/npc remove <id> " + ChatColor.GRAY + "- Smaze NPC");
        p.sendMessage(ChatColor.YELLOW + "/npc list " + ChatColor.GRAY + "- Vypise vsechna NPC");
    }
}
