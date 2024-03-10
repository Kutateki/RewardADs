package it.nathanub.RewardADs.Logic.GUIs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import it.nathanub.RewardADs.Tools.Api;
import it.nathanub.RewardADs.Tools.Error;
import net.md_5.bungee.api.ChatColor;

public class RewardsGUI {
	private Api api = new Api();

	public RewardsGUI(Player player, String category, HashMap<String, Map<String, String>> rewardsMap) {
		Inventory inventory = Bukkit.createInventory(null, 54, category);
		
		for(Map.Entry<String, Map<String, String>> rewards : rewardsMap.entrySet()) {
			List<String> lore = new ArrayList<>();
			boolean isGlow = false;
			ItemStack item = null;
			String name = null;
			int slot = 0;
            
        	for(Map.Entry<String, String> rewardsData : rewards.getValue().entrySet()) {
        		String element = rewardsData.getKey();
        		
        		if(element.contains("name")) {
        			String inRGB = toRGB(removeSpaces(rewardsData.getValue().replace('"' + "", "").replace("&", "§")));
        			
        			name = toColors(inRGB);
        		} else if(element.contains("material")) {
        			Material material = Material.getMaterial(rewardsData.getValue());
        			
        			if(material == null) new Error("rewards.yml", "in " + rewards.getKey() + ", non-valid material!");
        			
        			item = new ItemStack(material);
        		} else if(element.contains("slot")) {
        			if(isNumber(rewardsData.getValue())) {
        				slot = Integer.parseInt(rewardsData.getValue());
        			} else {
        				new Error("rewards.yml", "in " + rewards.getKey() + ", slot have to be a number!");
        			}
        		} else if(element.contains("glow")) {
        			if(rewardsData.getValue().toString().contains("true")) {
        				isGlow = true;
        			} else if(rewardsData.getValue().toString().contains("false")) {
        				isGlow = false;
        			} else {
        				new Error("rewards.yml", "in " + rewards.getKey() + ", glow have to be true or false!");
        			}
        		} else if(element.contains("lore")) {
        			String[] lores = rewardsData.getValue().split("&&");
	            	
	            	if(lores != null) {
                        int coins = api.getCoin(player);
                        
		            	for(String Lore : lores) {
		            		String inRGB = toRGB(removeSpaces(Lore.replace('"' + "", "").replace("&", "§").replace("%money%", coins + "")).replace("%player%", player.getName()).replace("%playername%", player.getName()));
		            		
		            		lore.add(toColors(inRGB));
		            	}
	            	}
        		}
        	}
        	
        	if(slot > 54) new Error("rewards.yml", "in " + rewards.getKey() + ", slot have to be smaller or egual to 54!");
        	if(item == null) new Error("rewards.yml", "in " + rewards.getKey() + ", no material provided!");
        	if(name == null) name = "§e" + rewards.getKey();
        	
        	ItemMeta meta = item.getItemMeta();
        	
        	meta.setLore(lore);
            meta.setDisplayName(name);
            
            if(isGlow) meta.addEnchant(Enchantment.DURABILITY, 1, false); meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            
            item.setItemMeta(meta);
        	inventory.setItem(slot, item);
        	
        	ItemStack backItem = new ItemStack(Material.BARRIER);
            ItemMeta backMeta = backItem.getItemMeta();
            backMeta.setDisplayName("§cBack");
            backItem.setItemMeta(backMeta);
            inventory.setItem(45, backItem);
        }
		
		player.openInventory(inventory);
	}
	
	private String toColors(String message) {
	    String regex = "!rgb\\((\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)";
	    Matcher matcher = Pattern.compile(regex).matcher(message);

	    StringBuffer result = new StringBuffer();
	    while (matcher.find()) {
	        String match = matcher.group();
	        String[] rgbValues = match.substring(5, match.length() - 1).split(",");
	        int r = Integer.parseInt(rgbValues[0]);
	        int g = Integer.parseInt(rgbValues[1]);
	        int b = Integer.parseInt(rgbValues[2]);
	        String replacement = ChatColor.of(new Color(r, g, b)) + "";
	        
	        matcher.appendReplacement(result, replacement);
	    }
	    
	    matcher.appendTail(result);

	    return result.toString();
	}
	
	private static String toRGB(String input) {
		Pattern pattern = Pattern.compile("#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(input);
        StringBuffer output = new StringBuffer();
        
        while (matcher.find()) {
            String hexColor = matcher.group(1);
            Color rgbColor = hexToRgb(hexColor);
            String replacement = String.format("!rgb(%d,%d,%d)", rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());
            matcher.appendReplacement(output, replacement);
        }
        
        matcher.appendTail(output);
        
        return output + "";
	}
	
	private static Color hexToRgb(String hexColor) {
        int r = Integer.valueOf(hexColor.substring(0, 2), 16);
        int g = Integer.valueOf(hexColor.substring(2, 4), 16);
        int b = Integer.valueOf(hexColor.substring(4, 6), 16);
        return new Color(r, g, b);
    }
	
	private static String removeSpaces(String input) {
		String trimmed = input.trim();

	    int firstNonSpaceIndex = 0;
	    while (firstNonSpaceIndex < trimmed.length() && Character.isWhitespace(trimmed.charAt(firstNonSpaceIndex))) {
	        firstNonSpaceIndex++;
	    }

	    int lastNonSpaceIndex = trimmed.length() - 1;
	    while (lastNonSpaceIndex > firstNonSpaceIndex && Character.isWhitespace(trimmed.charAt(lastNonSpaceIndex))) {
	        lastNonSpaceIndex--;
	    }

	    String result = trimmed.substring(firstNonSpaceIndex, lastNonSpaceIndex + 1);
	    return result;
    }
	
	private static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
}
