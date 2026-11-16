package cz.tvojeserver.customnpc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

public class NPCListener implements Listener {

    private final CustomNPC plugin;

    public NPCListener(CustomNPC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNPCClick(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Entity entity = event.getRightClicked();
        if (!entity.getPersistentDataContainer().has(plugin.npcKey, PersistentDataType.STRING)) return;

        event.setCancelled(true);

        String id = entity.getPersistentDataContainer().get(plugin.npcKey, PersistentDataType.STRING);
        String rawCmd = plugin.getConfig().getString("npcs." + id + ".command");

        if (rawCmd != null && !rawCmd.isEmpty()) {
            Player player = event.getPlayer();
            String finalCmd = rawCmd.replace("%player%", player.getName());

            boolean asConsole = plugin.getConfig().getBoolean("npcs." + id + ".console", true);
            if (asConsole) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
            } else {
                player.performCommand(finalCmd);
            }
        }
    }

    @EventHandler
    public void onNPCDamage(EntityDamageEvent event) {
        if (event.getEntity().getPersistentDataContainer().has(plugin.npcKey, PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }
}
