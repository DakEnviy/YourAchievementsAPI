package ru.dakenviy.ya;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.AdityaTD.TitlesAPI.TitlesAPI;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class YAPluginAPI {
	private YAPlugin main;
	public String jsonFormat =
			" {text:\"%MESSAGE\",extra:[{text:\"%ACHIEVEMENT\",hoverEvent:{action:show_text,value:\"%LORE\"}}]}";
	
	public YAPluginAPI(YAPlugin main) {
		this.main = main;
	}
	
	public void registerAchievement(YourAchievement achievement) {
		List<UUID> uuids =  new ArrayList<>();
		if (main.dataCfg.contains(achievement.name)) {
			for (String uuid : main.dataCfg.getStringList(achievement.name)) {
				uuids.add(UUID.fromString(uuid));
			}
		}
		main.data.put(achievement, uuids);
		Bukkit.getPluginManager().registerEvents(achievement, main);
		achievement.setAPI(this);
	}
	public boolean isRegistered(YourAchievement achievement) {
		return main.data.containsKey(achievement);
	}
	public YourAchievement getAchievement(String name) {
		for (YourAchievement achievement : main.data.keySet()) {
			if (achievement.name.equalsIgnoreCase(name)) return achievement;
		}
		return null;
	}
	
	public boolean giveAchievement(OfflinePlayer player, YourAchievement achievement) {
		return giveAchievement(player, achievement, -1, false, true);
	}
	public boolean giveAchievement(OfflinePlayer player, YourAchievement achievement, int radius) {
		return giveAchievement(player, achievement, radius, false, true);
	}
	public boolean giveAchievement(OfflinePlayer player, YourAchievement achievement, int radius, boolean useTitlesAPI, boolean playSound) {
		if (isRegistered(achievement)) {
			if (!hasAchievement(player, achievement)) {
				List<UUID> uuids = main.data.get(achievement);
				uuids.add(player.getUniqueId());
				main.data.put(achievement, uuids);
				
				broadcastAchievement(player, achievement, radius, useTitlesAPI, playSound);
				return true;
			}
		}
		return false;
	}
	public void broadcastAchievement(OfflinePlayer player, YourAchievement achievement, int radius, boolean useTitlesAPI, boolean playSound) {
		if (isRegistered(achievement)) {
			if (radius == -1) {
				for (Player op : Bukkit.getOnlinePlayers()) {
					sendJSONMessage(op, achievement.getMessage(player.getName(), !op.getName().equalsIgnoreCase(player.getName())));
				}
				if (player.isOnline()) {
					Player p = (Player) player;
					if (useTitlesAPI && main.titlesAPI)
						TitlesAPI.sendTitle(p, 20, 50, 20,
								achievement.achievementColor + achievement.name,
								achievement.messageColor + achievement.getLoreString());
					if (playSound && achievement.sound != null)
						p.playSound(p.getLocation(), achievement.sound, 1, 1);
				}
			}
			else if (radius == 0) {
				if (player.isOnline()) {
					Player p = (Player) player;
					sendJSONMessage(p, achievement.getMessage(player.getName(), false));
					
					if (useTitlesAPI && main.titlesAPI)
						TitlesAPI.sendTitle(p, 20, 50, 20,
								achievement.achievementColor + achievement.name,
								achievement.messageColor + achievement.getLoreString());
					if (playSound && achievement.sound != null)
						p.playSound(p.getLocation(), achievement.sound, 1, 1);
				}
			}
			else if (radius > 0) {
				if (player.isOnline()) {
					Player p = (Player) player;
					sendJSONMessage(p, achievement.getMessage(player.getName(), false));
					
					if (useTitlesAPI && main.titlesAPI)
						TitlesAPI.sendTitle(p, 20, 50, 20,
								achievement.achievementColor + achievement.name,
								achievement.messageColor + achievement.getLoreString());
					if (playSound && achievement.sound != null)
						p.playSound(p.getLocation(), achievement.sound, 1, 1);
					
					for (Entity e : p.getNearbyEntities(radius, radius, radius)) {
						if (e.getType() == EntityType.PLAYER)
							sendJSONMessage((Player)e, achievement.getMessage(player.getName(), true));
					}
				}
			}
		}
	}
	public boolean removeAchievement(OfflinePlayer player, YourAchievement achievement) {
		if (isRegistered(achievement)) {
			if (hasAchievement(player, achievement)) {
				List<UUID> uuids = main.data.get(achievement);
				uuids.remove(player.getUniqueId());
				main.data.put(achievement, uuids);
				return true;
			}
		}
		return false;
	}
	public boolean hasAchievement(OfflinePlayer player, YourAchievement achievement) {
		if (isRegistered(achievement)) {
			return main.data.get(achievement).contains(player.getUniqueId());
		}
		return false;
	}
	
	public void sendJSONMessage(Player player, String json) {
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(json));
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}
}
