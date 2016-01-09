package ru.dakenviy.ya;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class YAPluginCommands implements CommandExecutor {
	private YAPluginAPI api;
	
	public YAPluginCommands(YAPluginAPI api) {
		this.api = api;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("YourAchievements")) {
			if (args.length == 0) return false;
			
			if (args[0].equalsIgnoreCase("give")) {
				if (args.length < 3 || args.length > 6) {
					sender.sendMessage(ChatColor.RED + "Syntax error! Usage: " + ChatColor.YELLOW + "/ya give [player] [achievement] [radius] [useTitlesAPI] [playSound]");
					return true;
				}
				if (!sender.hasPermission("ya.give")) {
					sender.sendMessage(ChatColor.RED + "You don't have permission!");
					return true;
				}
				
				YourAchievement achievement = api.getAchievement(args[2].replace("_", " "));
				if (achievement == null) {
					sender.sendMessage(ChatColor.RED + "Achievement " + args[2].replace("_", " ") + " is not exist!");
					return true;
				}
				
				if (args.length == 3) {
					if (api.giveAchievement(Bukkit.getOfflinePlayer(args[1]), achievement)) {
						sender.sendMessage(ChatColor.GREEN + "Achievement " + args[2].replace("_", " ") + " was given " + args[1]);
					} else {
						sender.sendMessage(ChatColor.RED + "Player already has this achievement!");
					}
				}
				else if (args.length == 4) {
					try {
						int radius = Integer.valueOf(args[3]);
						
						if (api.giveAchievement(Bukkit.getOfflinePlayer(args[1]), achievement, radius)) {
							sender.sendMessage(ChatColor.GREEN + "Achievement " + args[2].replace("_", " ") + " was given " + args[1]);
						} else {
							sender.sendMessage(ChatColor.RED + "Player already has this achievement!");
						}
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "" + args[3] + " is not a number!");
					}
				}
				else if (args.length == 5) {
					try {
						int radius = Integer.parseInt(args[3]);
						try {
							boolean useTitlesAPI = Boolean.valueOf(args[4]);
							
							if (api.giveAchievement(Bukkit.getOfflinePlayer(args[1]), achievement, radius, useTitlesAPI, true)) {
								sender.sendMessage(ChatColor.GREEN + "Achievement " + args[2].replace("_", " ") + " was given " + args[1]);
							} else {
								sender.sendMessage(ChatColor.RED + "Player already has this achievement!");
							}
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "" + args[4] + " is not a boolean!");
						}
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "" + args[3] + " is not a number!");
					}
				}
				else if (args.length == 6) {
					try {
						int radius = Integer.parseInt(args[3]);
						try {
							boolean useTitlesAPI = Boolean.valueOf(args[4]);
							try {
								boolean playSound = Boolean.valueOf(args[5]);
								
								if (api.giveAchievement(Bukkit.getOfflinePlayer(args[1]), achievement, radius, useTitlesAPI, playSound)) {
									sender.sendMessage(ChatColor.GREEN + "Achievement " + args[2].replace("_", " ") + " was given " + args[1]);
								} else {
									sender.sendMessage(ChatColor.RED + "Player already has this achievement!");
								}
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "" + args[5] + " is not a boolean!");
							}
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "" + args[4] + " is not a boolean!");
						}
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "" + args[3] + " is not a number!");
					}
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length != 3) {
					sender.sendMessage(ChatColor.RED + "Syntax error! Usage: " + ChatColor.YELLOW + "/ya remove [player] [achievement]");
					return true;
				}
				if (!sender.hasPermission("ya.remove")) {
					sender.sendMessage(ChatColor.RED + "You don't have permission!");
					return true;
				}
				
				YourAchievement achievement = api.getAchievement(args[2].replace("_", " "));
				if (achievement == null) {
					sender.sendMessage(ChatColor.RED + "Achievement " + args[2].replace("_", " ") + " is not exist!");
					return true;
				}
				
				if (api.removeAchievement(Bukkit.getOfflinePlayer(args[1]), achievement)) {
					sender.sendMessage(ChatColor.GREEN + "Achievement " + args[2].replace("_", " ") + " was removed from " + args[1]);
				} else {
					sender.sendMessage(ChatColor.RED + "Player hasn't this achievement!");
				}
				return true;
			}
		}
		return false;
	}
}
