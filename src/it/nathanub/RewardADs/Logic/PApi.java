package it.nathanub.RewardADs.Logic;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import it.nathanub.RewardADs.Tools.Api;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PApi extends PlaceholderExpansion {
	private static Api api = new Api();

	@Override
	public @Nonnull String getAuthor() {
		return "Nathanub";
	}

	@Override
	public @Nonnull String getIdentifier() {
		return "rewardads";
	}

	@Override
	public @Nonnull String getVersion() {
		return "1.0v";
	}
	
	@Override
	public @Nonnull boolean canRegister() {
		return true;
	}
	
	@Override
	public @Nonnull boolean persist() {
		return true;
	}

	@Override
	public @Nonnull String onPlaceholderRequest(Player player, @Nonnull String params) {
	    if(player == null) {
	        return null;
	    }
	    
	    CompletableFuture<String> futureResult = new CompletableFuture<>();
	    
	    if(params.equals("coins")) {
	    	String coins = api.getCoin(player) + "";
	    	
	    	futureResult.complete(coins);
	    } else if(params.startsWith("coinstop_")) {
	        int number;
	        
	        try {
	            number = Integer.parseInt(params.substring(9));
	        } catch (NumberFormatException e) {
	            return null;
	        }
	        
	        futureResult = topStyle(number);
	    } else {
	        return null;
	    }
	    
	    return futureResult.join();
	}

	
	private CompletableFuture<String> topStyle(int number) {
        return topTen().thenApply(top10 -> {
            if(top10.size() > (number - 1)) {
                return "§e" + number + ". §6" + top10.get(number - 1).getValue() + " §7- §e" + top10.get(number - 1).getKey();
            } else {
                return "§e" + number + ". §6Noplayer §7- §e0";
            }
        });
    }
	
	private CompletableFuture<List<Entry<Integer, String>>> topTen() {
        HashMap<Integer, String> top = new HashMap<>();

        return api.getCoins().thenApply(array -> {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                top.put(Integer.parseInt((String) object.get("coins_player")), (String) object.get("name_player"));
            }

            List<Map.Entry<Integer, String>> list = new LinkedList<>(top.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<Integer, String>>() {
                public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                    return o2.getKey().compareTo(o1.getKey());
                }
            });

            return list.subList(0, Math.min(10, list.size()));
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

}
