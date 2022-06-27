package dev.aarow.keepinv;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KeepInv extends JavaPlugin implements Listener {

    public static KeepInv instance;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(this, this);
    }
    

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Iterator<ItemStack> drops = event.getDrops().iterator();

        while(drops.hasNext()){
            drops.next();
            drops.remove();
        }

        Map<Integer, ItemStack> items = new HashMap<>();
        ItemStack[] armor = event.getEntity().getInventory().getArmorContents();

        int levels = event.getEntity().getLevel();
        float exp = event.getEntity().getExp();

        Player player = event.getEntity();

        for(int i = 0; i < 36; i++){
            items.put(i, player.getInventory().getItem(i));
        }


        new BukkitRunnable(){
            public void run(){
                player.spigot().respawn();

                Bukkit.getScheduler().runTaskLater(instance, () -> {
                    items.keySet().stream().filter(slot -> items.get(slot) != null).forEach(slot -> {
                        player.getInventory().setItem(slot, items.get(slot));
                    });
                    player.getInventory().setArmorContents(armor);

                    player.updateInventory();

                    player.setExp(exp);
                    player.setLevel(levels);
                }, 2L);
            }
        }.runTaskLater(this, 2L);
    }
}
