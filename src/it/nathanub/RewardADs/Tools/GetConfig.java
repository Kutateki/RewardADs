package it.nathanub.RewardADs.Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetConfig {

	public String getCode() {
		String[] lines = readInformations().split("\n");
		String code = null;
        
        for(String line : lines) {
        	if(!line.startsWith("#")) {
        		if(line.startsWith("code")) {
            		code = removeSpaces(line.replace("code:", "")).toLowerCase();
            	}
        	}
        }
		
		return code;
	}
	
	public String[] getAnnouncesMessages() {
		String[] lines = readInformations().split("\n");
		String[] announceMessages = new String[10];
        
        for(String line : lines) {
        	if(!line.startsWith("#")) {
        		if(line.startsWith("announce-message")) {
        			String[] messages = line.replace("announce-messages:", "").split("&&");
	            	 
	            	 if(messages != null) {
	            		 for(String message : messages) {
	            			 addString(announceMessages, removeSpaces(message.replace('"' + "", "").replace("&", "§")));
	            		 }
	            	 } else {
	            		 new Error("configuration.yml", "no announce-messages provided!");
	            	 }
            	}
        	}
        }
		
		return announceMessages;
	}
	
	public int getAnnouncesDelay() {
		String[] lines = readInformations().split("\n");
		Pattern pattern = Pattern.compile("\\d+");
        int delay = 0;
        
        for(String line : lines) {
        	if(!line.startsWith("#")) {
        		if(line.startsWith("announce-delay")) {
        			Matcher matcher = pattern.matcher(line.replace("announce-delay:", ""));
	            	 
	            	if(matcher.find()) {
	            		delay = Integer.parseInt(matcher.group());
	                } else {
	                	new Error("configuration.yml", "non-valid announce-delay provided!"); 
	                }
            	}
        	}
        }
		
		return delay;
	}
	
	public boolean isAnnouncerEnabled() {
		String[] lines = readInformations().split("\n");
		boolean isEnabled = false;
		
		for(String line : lines) {
        	if(!line.startsWith("#")) {
        		if(line.startsWith("annoucer-enabled")) {
        			String lineFixed = line.replace("annoucer-enabled:", "");
	            	
	            	if(lineFixed.contains("true")) {
	            		isEnabled = true;
	            		System.out.println("ciao");
	            	} else if(lineFixed.contains("false")) {
	            		isEnabled = false;
	            		System.out.println("no");
	            	} else {
	            		new Error("configuration.yml", "only true and false are valid in announcer-enabled!"); 
	            	}
        		}
        	}
		}
		
		System.out.println(isEnabled);
		
		return isEnabled;
	}
	
	public int getEarnPerAD() {
		String[] lines = readInformations().split("\n");
		int earn = 0;
		
		for(String line : lines) {
        	if(!line.startsWith("#")) {
        		if(line.startsWith("earn-per-ad")) {
        			Pattern pattern = Pattern.compile("\\d+");
	                Matcher matcher = pattern.matcher(line.replace("earn-per-ad:", ""));

	                if(matcher.find()) {
	                    earn = Integer.parseInt(matcher.group());
	                } else {
	                	new Error("configuration.yml", "non-valid earn-per-ad provided!"); 
	                }
        		}
        	}
		}
		
		return earn;
	}
	
	public String[] getCommands() {
		String[] lines = readInformations().split("\n");
		String[] rewardsCommands = new String[10];
		
		for(String line : lines) {
        	if(!line.startsWith("#")) {
        		if(line.startsWith("rewards-commands")) {
        			String[] commands = line.replace("rewards-commands:", "").split("&&");
	            	
	            	if(commands != null) {
		            	for(String command : commands) {
		            		addString(rewardsCommands, removeSpaces(command.replace('"' + "", "").replace("&", "§")));
		            	}
	            	} else {
	            		new Error("configuration.yml", "no rewards-commands provided!");
	            	}
        		}
        	}
		}
		
		return rewardsCommands;
	}
	
	public Map<String, Map<String, String>> getCategories() {
		Map<String, Map<String, String>> categories = new HashMap<String, Map<String, String>>();
		File rewardsFile = new File("plugins/RewardADs/categories.yml");
		StringBuilder content = new StringBuilder();
		
		try(Scanner reader = new Scanner(rewardsFile)) {
			while(reader.hasNextLine()) {
	            String line = reader.nextLine();
	            content.append(line).append("\n");
	        }
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		categories = parseConfig(content.toString());
		
		return categories;
	}
	
	public Map<String, Map<String, String>> getRewards() {
		Map<String, Map<String, String>> rewards = new HashMap<String, Map<String, String>>();
		File rewardsFile = new File("plugins/RewardADs/rewards.yml");
		StringBuilder content = new StringBuilder();
		
		try(Scanner reader = new Scanner(rewardsFile)) {
			while(reader.hasNextLine()) {
	            String line = reader.nextLine();
	            content.append(line).append("\n");
	        }
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		rewards = parseConfig(content.toString());
		
		return rewards;
	}
	
	private static String readInformations() {
		File configuration = new File("plugins/RewardADs/configuration.yml");
		StringBuilder content = new StringBuilder();
		
		try(Scanner reader = new Scanner(configuration)) {
			while(reader.hasNextLine()) {
	            String line = reader.nextLine();
	            content.append(line).append("\n");
	        }
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return content.toString();
	}
	
	private static Map<String, Map<String, String>> parseConfig(String config) {
        Map<String, Map<String, String>> result = new HashMap<>();
        String[] lines = config.split("\n");

        String currentReward = null;
        Map<String, String> currentRewardData = null;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }

            if (line.matches("\\w+:")) {
                if (currentReward != null) {
                    result.put(currentReward, currentRewardData);
                }
                currentReward = line.substring(0, line.length() - 1);
                currentRewardData = new HashMap<>();
            } else {
                String[] parts = line.trim().split(": ");
                if (parts.length == 2) {
                    currentRewardData.put(parts[0], parts[1]);
                }
            }
        }

        if (currentReward != null) {
            result.put(currentReward, currentRewardData);
        }

        return result;
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
	
	private static void addString(String[] array, String newString) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null || array[i].isEmpty()) {
                array[i] = newString;
                return;
            }
        }

        System.out.println("Array is full. Cannot add more elements.");
    }
}
