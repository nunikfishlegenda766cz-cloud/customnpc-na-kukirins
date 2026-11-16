package cz.tvojeserver.customnpc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class CustomNPC extends JavaPlugin {

    public NamespacedKey npcKey;

    @Override
    public void onEnable() {
        this.npcKey = new NamespacedKey(this, "npc_id");
        saveDefaultConfig();

        getCommand("npc").setExecutor(new NPCCommand(this));
        getServer().getPluginManager().registerEvents(new NPCListener(this), this);

        getLogger().info("CustomNPC plugin byl uspesne zapnut!");
    }

    public void createNPC(String id, String displayName, Location loc, EntityType type) {
        Entity entity = loc.getWorld().spawnEntity(loc, type);
        entity.setCustomName(ChatColor.translateAlternateColorCodes('&', displayName));
        entity.setCustomNameVisible(true);
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setPersistent(true);

        if (entity instanceof LivingEntity living) {
            living.setAI(false);
            living.setRemoveWhenFarAway(false);
        }

        entity.getPersistentDataContainer().set(npcKey, PersistentDataType.STRING, id);

        getConfig().set("npcs." + id + ".uuid", entity.getUniqueId().toString());
        getConfig().set("npcs." + id + ".name", displayName);
        getConfig().set("npcs." + id + ".command", "say Ahoj %player%!");
        getConfig().set("npcs." + id + ".console", true);
        saveConfig();
    }

    public boolean removeNPC(String id) {
        if (!getConfig().contains("npcs." + id)) return false;

        String uuidStr = getConfig().getString("npcs." + id + ".uuid");
        if (uuidStr != null) {
            Entity entity = Bukkit.getEntity(UUID.fromString(uuidStr));
            if (entity != null) {
                entity.remove();
            }
        }

        getConfig().set("npcs." + id, null);
        saveConfig();
        return true;
    }
}
