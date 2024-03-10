package it.nathanub.RewardADs.Logic;

import java.awt.Color;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import it.nathanub.RewardADs.Tools.Api;
import it.nathanub.RewardADs.Tools.Error;
import net.md_5.bungee.api.ChatColor;

public class Buy {
	private static Api api = new Api();
	
	public void process(Player player, String reward, Map<String, String> data) {
		int coins = api.getCoin(player);
		int cost = 0;
        
    	for(Map.Entry<String, String> dataEntry : data.entrySet()) {
    		if(dataEntry.getKey().contains("cost")) {
    			cost = Integer.parseInt(dataEntry.getValue());
    		} else if(dataEntry.getKey().contains("commands")) {
    			System.out.println(coins);
    			
    			if(coins >= cost) {
    				String[] commands = dataEntry.getValue().split("&&");
                	
                	if(commands != null) {
    	            	for(String command : commands) {
    	            		String inRGB = toRGB(removeSpaces(command.replace('"' + "", "").replace("&", "§").replace("%playername%", player.getName()).replace("&", "§").replace("%player%", player.getName()).replace("%playeruuid%", player.getUniqueId() + "").replace("%money%", coins + "")));
    	            		
    	            		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), toColors(inRGB));
    	            	}
                	} else {
                		new Error("configuration.yml", "no rewards-commands provided!");
                	}
    			}
    		}
    	}
    	
    	if(coins >= cost) {
    		//Inventory openInventory = player.getOpenInventory().getTopInventory();
    		
    		api.decreaseCoins(player, cost);
    		
    		/*if(openInventory.getTitle().equals("Rewards")) {
    			player.closeInventory();
    			
    			new RewardsGUI(player);
    		}*/
    		
    		player.sendMessage("§aYou've bought §6" + reward + "§a!");
    	} else {
    		player.closeInventory();
    		player.sendMessage("§cYou haven't got enough money!");
    	}
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
}
