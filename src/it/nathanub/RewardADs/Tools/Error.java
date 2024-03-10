package it.nathanub.RewardADs.Tools;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import it.nathanub.RewardADs.Main;

public class Error {

	public Error(String where, String error) {
		Main.getInstance().getLogger().severe("At " + where + ", " + error);
		
		new BukkitRunnable() {
            @Override
            public void run() {
            	for(Player player : Bukkit.getOnlinePlayers()) {
        			if(player.hasPermission("rewardads.admin")) {
        				player.sendMessage("§cAt " + where + ", " + error);
        			}
        		}
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L * 30L);
	}
}