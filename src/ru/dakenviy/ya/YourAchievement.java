package ru.dakenviy.ya;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.event.Listener;

public abstract class YourAchievement implements Listener {
	private YAPluginAPI api;

	public String name, message, broadcastMessage;
	public ChatColor messageColor, achievementColor;
	public List<String> lore;
	public Sound sound;
	
	public YourAchievement(String name) {
		this.name = name;
		this.message = "You just earned the achievement ";
		this.broadcastMessage = " has just earned the achievement ";
		this.messageColor = ChatColor.WHITE;
		this.achievementColor = ChatColor.YELLOW;
		this.lore = new ArrayList<>();
		this.lore.add(ChatColor.ITALIC + "Your lore!");
		this.sound = null;
	}
	public void setAPI(YAPluginAPI api) {
		this.api = api;
	}
	
	public boolean give(OfflinePlayer player) {
		return api.giveAchievement(player, this);
	}
	public boolean give(OfflinePlayer player, int radius) {
		return api.giveAchievement(player, this, radius);
	}
	public boolean give(OfflinePlayer player, int radius, boolean useTitlesAPI, boolean playSound) {
		return api.giveAchievement(player, this, radius, useTitlesAPI, playSound);
	}
	
	public String getMessage(String player, boolean broadcast) {
		StringBuilder builder = new StringBuilder();
	    builder.append(achievementColor + name + "\n");
	    for (String s : lore) {
	    	builder.append(s + "\n");
	    }
	    String loreBuilded = builder.toString();
	    loreBuilded = loreBuilded.substring(0, loreBuilded.length() - 1);
	    
	    if (broadcast) {
	    	return api.jsonFormat.replace("%ACHIEVEMENT", achievementColor + "[" + name + "]")
		    		.replace("%MESSAGE", messageColor + player + broadcastMessage).replace("%LORE", loreBuilded);
	    } else {
	    	return api.jsonFormat.replace("%ACHIEVEMENT", achievementColor + "[" + name + "]")
		    		.replace("%MESSAGE", messageColor + message).replace("%LORE", loreBuilded);
	    }
	}
	public String getLoreString() {
		String loreString = "";
		for (String s : lore) {
			loreString = loreString + " " + s;
		}
		return loreString;
	}
}
