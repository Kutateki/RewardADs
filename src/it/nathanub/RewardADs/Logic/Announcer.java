package it.nathanub.RewardADs.Logic;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import it.nathanub.RewardADs.Tools.Api;
import it.nathanub.RewardADs.Tools.GetConfig;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Announcer extends BukkitRunnable {
	private static Api api = new Api();

	@Override
    public void run() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			for(String message : new GetConfig().getAnnouncesMessages()) {
				if(message != null) {
					if(!message.contains("(button)")) {
						player.sendMessage(message);
					} else {
						api.getServer().thenAccept(object -> {
							String nameServer = (String) object.get("name_server");
							String result = removeSpacesBeforeFirstLetter(message.replace("(button)", ""));
							TextComponent clickableMessage = new TextComponent(result);
	
							clickableMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://rewardads.vpsgh.it/" + nameServer + "/?player=" + player.getName()));
				            player.spigot().sendMessage(clickableMessage);
						}).exceptionally(e -> {
				            e.printStackTrace();
				            return null;
				        });
					}
				}
			}
		}
    }
	
	private static String removeSpacesBeforeFirstLetter(String input) {
        return input.replaceAll("^\\s+", "");
    }
}