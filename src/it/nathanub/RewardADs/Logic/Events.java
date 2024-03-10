package it.nathanub.RewardADs.Logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import it.nathanub.RewardADs.Main;
import it.nathanub.RewardADs.Logic.GUIs.CategoriesGUI;
import it.nathanub.RewardADs.Logic.GUIs.RewardsGUI;
import it.nathanub.RewardADs.Tools.Api;
import it.nathanub.RewardADs.Tools.GetConfig;

import net.md_5.bungee.api.ChatColor;

public class Events implements Listener {
	private static Api api = new Api();
	private static Buy buy = new Buy();
	private boolean executed = false;
	private String nameServer;

	public Events(Main main) {
		api.getServer().thenAccept(object -> {
            nameServer = (String) object.get("name_server");
		}).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
	}

	@EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String commandFixed[] = event.getMessage().substring(1).toLowerCase().split(" ");
        Player player = event.getPlayer();
        
        boolean requested = false;
        boolean found = false;
        boolean foundCat = false;
    
        for(String command : new GetConfig().getCommands()) {
        	if(command != null) {
        		if(command.equals(commandFixed[0])) {
        			event.setCancelled(true);
        			
        			if(nameServer == null) {
        				player.sendMessage("§cRewardADs has not been configured, please contact an administrator.");
        				return;
        			}
        			
        			if(commandFixed.length > 1) {
        				requested = true;
        				
        				for(Map.Entry<String, Map<String, String>> catogories : new GetConfig().getCategories().entrySet()) {
            				if(catogories.getKey().toLowerCase().contains(commandFixed[1])) {
            					if(!foundCat) {
            						for(Map.Entry<String, Map<String, String>> rewards : new GetConfig().getRewards().entrySet()) {
                        				Map<String, String> rewardsData = rewards.getValue();
                        	            
                        				if(rewards.getKey().toLowerCase().equalsIgnoreCase(commandFixed[2])) {
                        					if(!found) {
                        						buy = new Buy();
                        						
                        						buy.process(player, rewards.getKey(), rewardsData);
                        					}
                        					
                        					found = true;
                        				}
                        	        }
            					}
            					
            					foundCat = true;
            				}
            	        }
            		} else {
            			new CategoriesGUI(event.getPlayer());
            		}
        		}
        	}
        }
        
        if(commandFixed[0].equals("rewardads")) {
        	event.setCancelled(true);
        	
        	if(commandFixed.length > 1) {
        		if(commandFixed[1].contains("reload")) {
        			if(player.hasPermission("rewardads.admin")) {
        				Main.load();
        				
        				player.sendMessage("§6Reward§eADs §asuccessful reloaded!");
        			}
        		}
        	} else {
        		if(nameServer == null) {
    				player.sendMessage("§cRewardADs has not been configured, please contact an administrator.");
    				return;
    			}
        		
        		player.sendMessage("§a" + nameServer + " §7is using §6Reward§eADs");
        		player.sendMessage("§7created by §6Nathanub §7& §6Kutateki§7!");
        	}
    		
    	}
        
        if(requested && !found) player.sendMessage("§cThere are no rewards in this category with this name!");
        
	}

	@EventHandler
	public void onCLick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack clickedItem = event.getCurrentItem();

		if(clickedItem != null && clickedItem.getType() != Material.AIR) {
			for(Map.Entry<String, Map<String, String>> categories : new GetConfig().getCategories().entrySet()) {
				HashMap<String, Map<String, String>> rewards = new HashMap<String, Map<String, String>>();
				String categoryName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
				
				if(event.getView().getTitle().contains("Rewards")) {
					for(Map.Entry<String, Map<String, String>> entry : new GetConfig().getRewards().entrySet()) {
						for(Map.Entry<String, String> rewardsData : entry.getValue().entrySet()) {
							if(rewardsData.getKey().contains("category")) {
								if(rewardsData.getValue().equalsIgnoreCase(categoryName)) {
									rewards.put(entry.getKey(), entry.getValue());
				                }
							}
						}
					}
					
					new RewardsGUI(player, categoryName, rewards);
				}
				
				if(event.getView().getTitle().contains(categories.getKey().toString())) {
					event.setCancelled(true);
					
					HashMap<String, Map<String, String>> rewardsFixed = new HashMap<String, Map<String, String>>();
		            String rewardMame = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
		            String categoryNameFixed = categories.getKey().toString();
		            
		            
		            for(Map.Entry<String, Map<String, String>> entry : new GetConfig().getRewards().entrySet()) {
						for(Map.Entry<String, String> rewardsData : entry.getValue().entrySet()) {
							if(rewardsData.getKey().contains("category")) {
								if(rewardsData.getValue().equalsIgnoreCase(categoryNameFixed)) {
									rewardsFixed.put(entry.getKey(), entry.getValue());
								}
							}
						}
						
						for(Entry<String, Map<String, String>> rewards2 : rewardsFixed.entrySet()) {
							Map<String, String> data = rewards2.getValue();
			                
			                if(rewards2.getKey().equalsIgnoreCase(rewardMame)) {
			                	if(!executed) {
	        						buy.process(player, rewardMame, data);
	        						
	        						executed = true;
			                	}
			                }
						}
		            }
				}
			}
			
			executed = false;
			
			if(clickedItem.getType() == Material.BARRIER && ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).contains("Back")) {
				new CategoriesGUI(player);
			}
		}
	}	
}
