package ru.dakenviy.ya;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class YAPlugin extends JavaPlugin {
	private static YAPluginAPI api;
	
	public YamlConfiguration dataCfg;
	public HashMap<YourAchievement, List<UUID>> data = new HashMap<>();
	public boolean titlesAPI = false;

	public void onEnable() {
		api = new YAPluginAPI(this);
		getCommand("YourAchievements").setExecutor(new YAPluginCommands(api));
		
		if (Bukkit.getPluginManager().getPlugin("TitlesAPI") != null) {
			titlesAPI = true;
		}
		
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File dataFile = new File(getDataFolder(), "data.yml");
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		dataCfg = YamlConfiguration.loadConfiguration(dataFile);
	}
	
	public void onDisable() {
		for (YourAchievement achievements : data.keySet()) {
			List<String> uuidsString = new ArrayList<>();
			for (UUID uuid : data.get(achievements)) {
				uuidsString.add(uuid.toString());
			}
			dataCfg.set(achievements.name, uuidsString);
		}
		try {
			dataCfg.save(new File(getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static YAPluginAPI getAPI() {
		return api;
	}
}
