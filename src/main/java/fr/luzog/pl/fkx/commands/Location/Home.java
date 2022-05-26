package fr.luzog.pl.fkx.commands.Location;//package fr.luzog.pl.uhc.commands.Location;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.command.TabCompleter;
//import org.bukkit.entity.Player;
//
//import fr.luzog78.adventure.Main;
//import fr.luzog78.adventure.config.CustomConfigFiles;
//import fr.luzog78.adventure.ressources.Function;
//
//public class Home implements CommandExecutor, TabCompleter {
//	Main main;
//
//	public Home(Main main) {
//		this.main = main;
//	}
//
//	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
//
//		CustomConfigFiles.refreshAll();
//
//		if (sender instanceof Player) {
//			Player player = (Player) sender;
//
//			if (args.length > 2 || args.length < 1) {
//				sender.sendMessage(ChatColor.RED + "La syntaxe est : /home <home|list> ou /home <set|del> <home>");
//				return true;
//			}
//
//			if (args.length == 1) {
//				if (args[0].equals("list")) {
//					main.reloadConfig();
//
//					StringBuilder sb = new StringBuilder();
//					for (String h : CustomConfigFiles.getCCF("Locations").getConfigurationSection("locations.homes").getKeys(false)) {
//						sb.append(" " + h);
//					}
//
//					String h = "homes";
//					if (CustomConfigFiles.getCCF("Locations").getConfigurationSection("locations.homes").getKeys(false).size() == 1) {
//						h = "home";
//					}
//
//					sender.sendMessage("�7[�2Homes�7]�r "
//							+ CustomConfigFiles.getCCF("Locations").getConfigurationSection("locations.homes").getKeys(false).size() + " �b"
//							+ h + "�r :" + sb);
//					return false;
//				} else {
//					for (String name : homes()) {
//						if (args[0].equals(name)) {
//							try {
//								player.teleport(Function.getLoc("Locations", "locations.homes." + args[0]));
//
//								if (args.length == 0 || !args[args.length - 1].equalsIgnoreCase("c")) {
//									Bukkit.broadcastMessage(
//											ChatColor.YELLOW + player.getDisplayName() + "�a c tp au home !");
//								}
//
//								return true;
//							} catch (Exception e) {
//								player.sendMessage(
//										"�cError ! You can't teleport to the home (verifie in the config.yml)");
//								Bukkit.broadcastMessage("�cError !");
//								e.printStackTrace();
//								return false;
//							}
//						}
//					}
//					player.sendMessage("�cAucun home trouve !");
//				}
//			} else {
//				if (args[0].equals("set")) {
//					player.sendMessage("�cUTILIER POUR L'INSTANT LE /SETHOME.");
//				} else if (args[0].equals("del")) {
//					try {
//						Function.setLocInConfig("Locations", "locations.DELHOMES",
//								args[0] + "_" + main.getConfig().getInt("locations.DELHOMES.count"),
//								Function.getLoc("Locations", "locations.homes." + args[1]));
//						CustomConfigFiles.force("Locations", "locations.DELHOMES.count",
//								CustomConfigFiles.getCCF("Locations").getInt("locations.DELHOMES.count") + 1);
//						Function.delLocInConfig("Locations", "locations.homes." + args[1]);
//						CustomConfigFiles.refresh("Locations");
//						Bukkit.broadcastMessage(player.getDisplayName() + "�a a supprime le home" + args[0]);
//						return false;
//					} catch (NullPointerException e) {
//						sender.sendMessage("�cAucun home trouve.");
//						return false;
//					}
//				} else {
//					sender.sendMessage(ChatColor.RED + "La syntaxe est : /home <home|list> ou /home <set|del> <home>");
//				}
//			}
//
//		} else {
//			sender.sendMessage(ChatColor.RED + "Desole mais tu n'es pas un joueur...");
//		}
//
//		return false;
//	}
//
//	public Set<String> homes() {
//		return CustomConfigFiles.getCCF("Locations").getConfigurationSection("locations.homes").getKeys(false);
//	}
//
//	@Override
//	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
//		if (args.length > 2)
//			return Function.stringList("");
//		List<String> list = new ArrayList<>();
//		if (args.length == 1) {
//			for (String h : homes()) {
//				if (h.startsWith(args[0])) {
//					list.add(h);
//				}
//			}
//			list.add("list");
//			list.add("set");
//			list.add("del");
//		}
//		if (args.length == 2) {
//			if (args[0].equals("del")) {
//				for (String h : homes()) {
//					if (h.startsWith(args[1])) {
//						list.add(h);
//					}
//				}
//			}
//		}
//		return list;
//	}
//}
